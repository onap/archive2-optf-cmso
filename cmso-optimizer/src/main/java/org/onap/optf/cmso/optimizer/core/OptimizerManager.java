/*
 * ============LICENSE_START==============================================
 * Copyright (c) 2019 AT&T Intellectual Property.
 * =======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * ============LICENSE_END=================================================
 *
 */

package org.onap.optf.cmso.optimizer.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.ws.rs.core.Response.Status;
import org.onap.optf.cmso.optimizer.clients.optimizer.OptimizerRequestManager;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerEngineResponse;
import org.onap.optf.cmso.optimizer.clients.ticketmgt.TicketMgtRequestManager;
import org.onap.optf.cmso.optimizer.clients.ticketmgt.models.ActiveTicketsResponse;
import org.onap.optf.cmso.optimizer.clients.topology.TopologyRequestManager;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.exceptions.CmsoException;
import org.onap.optf.cmso.optimizer.model.Request;
import org.onap.optf.cmso.optimizer.model.Response;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
import org.onap.optf.cmso.optimizer.model.dao.ResponseDao;
import org.onap.optf.cmso.optimizer.observations.Observation;
import org.onap.optf.cmso.optimizer.service.rs.models.ChangeWindow;
import org.onap.optf.cmso.optimizer.service.rs.models.ElementInfo;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerRequest;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerResponse;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerResponse.OptimizeScheduleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class OptimizerManager.
 */
@Component
public class OptimizerManager {

    @Autowired
    RequestDao requestDao;

    @Autowired
    ResponseDao responseDao;

    @Autowired
    TopologyRequestManager topologyRequestManager;

    @Autowired
    TicketMgtRequestManager ticketMgtRequestManager;

    @Autowired
    OptimizerRequestManager optimizerRequestManager;

    /**
     * Validate optimizer request.
     *
     * @param request the request
     * @throws CmsoException cmso exception
     */
    public void validate(OptimizerRequest request) throws CmsoException {
        if (request.getRequestId() == null) {
            reportRequiredMissing("requestId");
        }
        if (request.getConcurrencyLimit() == null) {
            reportRequiredMissing("concurrencyLimit");
        }
        if (request.getChangeWindows() == null || request.getChangeWindows().size() < 1) {
            reportRequiredMissing("changeWindows");
        }
        if (request.getElements() == null || request.getElements().size() < 1) {
            reportRequiredMissing("elements");
        }
        if (request.getNormalDuration() == null) {
            reportRequiredMissing("normalDuration");
        }
        validateElements(request.getElements());
        validateChangeWindows(request.getChangeWindows());
    }

    private void validateChangeWindows(List<ChangeWindow> changeWindows) throws CmsoException {
        for (ChangeWindow changeWindow : changeWindows) {
            validateChangeWindow(changeWindow);
        }
    }

    private void validateChangeWindow(ChangeWindow changeWindow) throws CmsoException {
        if (changeWindow.getStartTime() == null) {
            reportRequiredMissing("startTime");
        }
        if (changeWindow.getEndTime() == null) {
            reportRequiredMissing("endTime");
        }
        if (!changeWindow.getEndTime().after(changeWindow.getStartTime())) {
            throw new CmsoException(Status.BAD_REQUEST, LogMessages.INVALID_CHANGE_WINDOW,
                            changeWindow.getEndTime().toString(), changeWindow.getStartTime().toString());
        }

    }

    private void validateElements(List<ElementInfo> elements) throws CmsoException {
        // Perhaps check for duplicate elements....
        for (ElementInfo element : elements) {
            validateElement(element);
        }
    }

    private void validateElement(ElementInfo element) throws CmsoException {
        if (element.getElementId() == null || element.getElementId().equals("")) {
            reportRequiredMissing("elementId");
        }
    }

    private void reportRequiredMissing(String name) throws CmsoException {
        throw new CmsoException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, name);
    }

    /**
     * Process optimizer request.
     *
     * @param request the request
     * @return the optimizer response
     * @throws CmsoException the cmso exception
     */
    public OptimizerResponse processOptimizerRequest(OptimizerRequest request) throws CmsoException {
        UUID uuid = UUID.fromString(request.getRequestId());
        Request requestRow = null;
        Optional<Request> rrOptional = requestDao.findById(uuid);
        if (rrOptional.isPresent()) {
            requestRow = rrOptional.get();
        }
        OptimizerResponse optimizerResponse = new OptimizerResponse();
        optimizerResponse.setRequestId(request.getRequestId());
        if (requestRow != null) {
            throw new CmsoException(Status.BAD_REQUEST, LogMessages.DUPLICATE_REQUEST_ID, request.getRequestId());
        }
        requestRow = new Request();
        requestRow.setUuid(uuid);
        requestRow.setCreatedTime(System.currentTimeMillis());
        ObjectMapper om = new ObjectMapper();
        try {
            requestRow.setRequest(om.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new CmsoException(Status.BAD_REQUEST, LogMessages.INVALID_REQUEST, e.getMessage());
        }
        requestRow.setStatus(OptimizeScheduleStatus.PENDING_TOPOLOGY.toString());
        requestDao.save(requestRow);
        initiateDataGathering(requestRow);
        requestDao.save(requestRow);
        OptimizeScheduleStatus status = OptimizeScheduleStatus.valueOf(requestRow.getStatus());
        if (status == OptimizeScheduleStatus.COMPLETED)
        {
            // COmpletely synchronous optimization
            optimizerResponse = getCompletedOptimizerResponse(uuid);
        }
        else
        {
            // One or more steps are asynchronous
            optimizerResponse.setStatus(status);
            optimizerResponse.setErrorMessage("");
        }
        return optimizerResponse;
    }

    private void initiateDataGathering(Request requestRow) throws CmsoException {
        TopologyResponse topologyResponse = topologyRequestManager.createTopologyRequest(requestRow);
        if (topologyResponse != null) {
            switch (topologyResponse.getStatus()) {
                case COMPLETED:
                    requestRow.setRequestStart(System.currentTimeMillis());
                    requestRow.setStatus(OptimizeScheduleStatus.PENDING_TICKETS.toString());
                    initiateTicketGathering(requestRow); // continue synchronous flow
                    return;
                case FAILED:
                    requestRow.setRequestStart(System.currentTimeMillis());
                    requestRow.setRequestEnd(System.currentTimeMillis());
                    requestRow.setStatus(OptimizeScheduleStatus.FAILED.toString());
                    requestRow.setMessage(topologyResponse.getErrorMessage());
                    return;
                case IN_PROGRESS:
                    requestRow.setRequestStart(System.currentTimeMillis());
                    requestRow.setStatus(OptimizeScheduleStatus.TOPOLOGY_IN_PROGRESS.toString());
                   return;
                default:
                    break;
            }
        }
        throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.FAILED_TO_CREATE_TOPOLOGY_REQUEST,
                        requestRow.getUuid().toString());
    }

    private void initiateTicketGathering(Request requestRow) throws CmsoException {
        ActiveTicketsResponse apiResponse = ticketMgtRequestManager.createTicketsRequest(requestRow);
        if (apiResponse != null) {
            switch (apiResponse.getStatus()) {
                case COMPLETED:
                    requestRow.setRequestStart(System.currentTimeMillis());
                    requestRow.setStatus(OptimizeScheduleStatus.PENDING_OPTIMIZER.toString());
                    initiateOptimizer(requestRow);
                    return;
                case FAILED:
                    requestRow.setRequestStart(System.currentTimeMillis());
                    requestRow.setRequestEnd(System.currentTimeMillis());
                    requestRow.setStatus(OptimizeScheduleStatus.FAILED.toString());
                    requestRow.setMessage(apiResponse.getErrorMessage());
                    return;
                case IN_PROGRESS:
                    requestRow.setRequestStart(System.currentTimeMillis());
                    requestRow.setStatus(OptimizeScheduleStatus.TICKETS_IN_PROGRESS.toString());
                    return;
                default:
                    break;
            }
        }
        throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.FAILED_TO_CREATE_TICKET_REQUEST,
                        requestRow.getUuid().toString());
    }


    private void initiateOptimizer(Request requestRow) throws CmsoException {
        OptimizerEngineResponse apiResponse = optimizerRequestManager.createOptimizerRequest(requestRow);
        if (apiResponse != null) {
            switch (apiResponse.getStatus()) {
                case COMPLETED:
                    requestRow.setRequestEnd(System.currentTimeMillis());
                    requestRow.setStatus(OptimizeScheduleStatus.COMPLETED.toString());
                    return;
                case FAILED:
                    requestRow.setRequestStart(System.currentTimeMillis());
                    requestRow.setRequestEnd(System.currentTimeMillis());
                    requestRow.setStatus(OptimizeScheduleStatus.FAILED.toString());
                    requestRow.setMessage(apiResponse.getErrorMessage());
                    return;
                case IN_PROGRESS:
                case IN_QUEUE:
                    requestRow.setRequestStart(System.currentTimeMillis());
                    requestRow.setStatus(OptimizeScheduleStatus.OPTIMIZER_IN_PROGRESS.toString());
                    return;
                default:
                    break;
            }
        }
        throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.FAILED_TO_CREATE_TICKET_REQUEST,
                        requestRow.getUuid().toString());
    }

    public OptimizerResponse getCompletedOptimizerResponse(UUID uuid)
    {
        OptimizerResponse response = null;
        Response responseRow  = getResponseRow(uuid);
        try
        {
            String responseStr = responseRow.getRepsonse();
            response = new ObjectMapper().readValue(responseStr, OptimizerResponse.class);
        }
        catch (Exception e)
        {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            response = new OptimizerResponse();
            response.setRequestId(uuid.toString());
            response.setStatus(OptimizeScheduleStatus.FAILED);
            response.setErrorMessage(LogMessages.UNEXPECTED_EXCEPTION.format(e.getMessage()));
        }
        return response;
    }

    public Response getResponseRow(UUID uuid)
    {
        Optional<Response> opt = responseDao.findById(uuid);
        if (opt.isPresent())
        {
            return opt.get();
        }
        return null;
    }

    @SuppressWarnings("unused")
    private Request getRequestRow(UUID uuid) throws CmsoException {
        Request requestRow = null;
        Optional<Request> requestOptional = requestDao.findById(uuid);
        if (requestOptional.isPresent()) {
            return requestOptional.get();
        }
        throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.EXPECTED_DATA_NOT_FOUND,
                       uuid.toString(), "Request table");
    }


}
