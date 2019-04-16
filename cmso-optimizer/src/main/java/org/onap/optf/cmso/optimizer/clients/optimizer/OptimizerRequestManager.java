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

package org.onap.optf.cmso.optimizer.clients.optimizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import org.onap.observations.Observation;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerEngineResponse;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerEngineResponse.OptimizerEngineResponseStatus;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerResults;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerSchedule;
import org.onap.optf.cmso.optimizer.clients.topology.TopologyRequestManager;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.model.Optimizer;
import org.onap.optf.cmso.optimizer.model.Request;
import org.onap.optf.cmso.optimizer.model.Response;
import org.onap.optf.cmso.optimizer.model.dao.OptimizerDao;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
import org.onap.optf.cmso.optimizer.model.dao.ResponseDao;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerRequest;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerResponse;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerResponse.OptimizeScheduleStatus;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerScheduleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The Class OPtimizerRequestManager.
 */
@Component
public class OptimizerRequestManager {

    @Autowired
    Environment env;

    @Autowired
    RequestDao requestDao;

    @Autowired
    OptimizerDao optimizerDao;

    @Autowired
    ResponseDao responseDao;

    @Autowired
    OptimizerClient optimizerClient;

    @Autowired
    TopologyRequestManager topologyRequestManager;

    /**
     * Creates the Optimizer request.
     *
     * @param requestRow the request row
     * @return the Optimizer response
     */
    public OptimizerEngineResponse createOptimizerRequest(Request requestRow) {
        //
        if (okToDispatch(false)) {
            Optimizer optimizer = getExistingOptmizer(requestRow.getUuid());
            if (optimizer == null) {
                optimizer = new Optimizer();
                optimizer.setUuid(requestRow.getUuid());
                optimizer.setOptimizeRetries(0);
            }
            optimizer.setOptimizeStart(System.currentTimeMillis());
            OptimizerEngineResponse apiResponse = optimizerClient.makeRequest(requestRow, optimizer);
            if (apiResponse.getStatus() == OptimizerEngineResponseStatus.COMPLETED) {
                optimizer.setOptimizeEnd(System.currentTimeMillis());
                optimizer.setOptimizeResponse(""); // Perhaps we do not need to persist...
                buildFinalResponse(requestRow, apiResponse);
            }
            optimizerDao.save(optimizer);
            return apiResponse;
        } else {
            OptimizerEngineResponse apiResponse = new OptimizerEngineResponse();
            apiResponse.setRequestId(requestRow.getUuid().toString());
            apiResponse.setStatus(OptimizerEngineResponseStatus.IN_QUEUE);
            apiResponse.setPollingSeconds(60);
            return apiResponse;
        }
    }


    private void buildFinalResponse(Request requestRow, OptimizerEngineResponse apiResponse) {
        Optional<Response> opt = responseDao.findById(requestRow.getUuid());
        Response responseRow = null;
        if (opt.isPresent()) {
            responseRow = opt.get();
        }
        if (responseRow == null) {
            responseRow = new Response();
            responseRow.setUuid(requestRow.getUuid());
        }

        try {
            OptimizerResponse response = new OptimizerResponse();
            response.setRequestId(requestRow.getUuid().toString());
            response.setStatus(OptimizeScheduleStatus.COMPLETED);
            String optString = requestRow.getRequest();

            OptimizerRequest optimizerResquest = new ObjectMapper().readValue(optString, OptimizerRequest.class);
            TopologyResponse topologyResponse = topologyRequestManager.getTopologyResponse(requestRow.getUuid());
            ElementWindowMapping ewm = new ElementWindowMapping(optimizerResquest, topologyResponse);
            ewm.initializeForProcessResult();
            OptimizerResults results = apiResponse.getOptimizerResults();
            for (OptimizerSchedule result : results.getSchedules()) {
                OptimizerScheduleInfo info = ewm.processResult(result);
                if (info != null) {
                    response.getSchedules().add(info);
                }
            }
            responseRow.setRepsonse(new ObjectMapper().writeValueAsString(response));
            requestRow.setStatus(OptimizeScheduleStatus.COMPLETED.toString());
            responseDao.save(responseRow);
            requestDao.save(requestRow);
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            requestRow.setStatus(OptimizeScheduleStatus.FAILED.toString());
            requestRow.setMessage(e.getMessage());
            responseRow.setRepsonse("");
            responseDao.save(responseRow);
            requestDao.save(requestRow);
        }
    }



    private boolean okToDispatch(boolean checkDispatchability) {
        if (checkDispatchability) {
            // not yet implemented
            return false;
        }
        else {
            return true;
        }
    }


    /**
     * Gets the existing optimizer row.
     *
     * @param uuid the uuid
     * @return the existing optmizer row
     */
    public Optimizer getExistingOptmizer(UUID uuid) {
        Optional<Optimizer> oppt = optimizerDao.findById(uuid);
        if (oppt.isPresent()) {
            return oppt.get();
        }
        return null;
    }



}
