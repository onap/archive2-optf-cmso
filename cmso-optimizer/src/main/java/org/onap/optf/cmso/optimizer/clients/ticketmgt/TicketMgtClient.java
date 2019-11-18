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

package org.onap.optf.cmso.optimizer.clients.ticketmgt;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.onap.optf.cmso.optimizer.clients.common.models.ElementCriteria;
import org.onap.optf.cmso.optimizer.clients.ticketmgt.models.ActiveTicketsRequest;
import org.onap.optf.cmso.optimizer.clients.ticketmgt.models.ActiveTicketsResponse;
import org.onap.optf.cmso.optimizer.clients.ticketmgt.models.ActiveTicketsResponse.ActiveTicketResponseStatus;
import org.onap.optf.cmso.optimizer.clients.topology.TopologyRequestManager;
import org.onap.optf.cmso.optimizer.clients.topology.models.ConstraintElements;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyElementInfo;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyPolicyInfo;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse;
import org.onap.optf.cmso.optimizer.common.BasicAuthenticatorFilter;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.common.PropertiesManagement;
import org.onap.optf.cmso.optimizer.exceptions.CmsoException;
import org.onap.optf.cmso.optimizer.filters.CmsoClientFilters;
import org.onap.optf.cmso.optimizer.model.Request;
import org.onap.optf.cmso.optimizer.model.Ticket;
import org.onap.optf.cmso.optimizer.model.Topology;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
import org.onap.optf.cmso.optimizer.model.dao.TicketDao;
import org.onap.optf.cmso.optimizer.observations.Observation;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerRequest;
import org.onap.optf.cmso.optimizer.service.rs.models.PolicyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The Class TicketMgtClient.
 */
@Component
public class TicketMgtClient {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    @Autowired
    PropertiesManagement pm;

    @Autowired
    RequestDao requestDao;

    @Autowired
    TicketDao ticketDao;

    @Autowired
    TopologyRequestManager topologyRequestManager;

    /**
     * Make request of ticket mgt system.
     *
     * @param requestRow the request row
     * @param ticketRow the ticket row
     * @return the active tickets response
     */
    public ActiveTicketsResponse makeRequest(Request requestRow, Ticket ticketRow) {
        ActiveTicketsResponse ticketResponse = new ActiveTicketsResponse();
        Integer maxAttempts = env.getProperty("cmso.optimizer.maxAttempts", Integer.class, 20);
        if (ticketRow.getTicketsRetries() >= maxAttempts) {
            ticketResponse.setStatus(ActiveTicketResponseStatus.FAILED);
            ticketResponse.setErrorMessage(LogMessages.EXCEEDED_RETRY_LIMIT.format("Topology", maxAttempts.toString()));
            Observation.report(LogMessages.EXCEEDED_RETRY_LIMIT, "TicketMgt", maxAttempts.toString());
            return ticketResponse;
        }
        ObjectMapper om = new ObjectMapper();
        String originalRequest = requestRow.getRequest();
        OptimizerRequest optimizerRequest = null;;
        try {
            optimizerRequest = om.readValue(originalRequest, OptimizerRequest.class);
            ActiveTicketsRequest apiRequest = buildRequest(optimizerRequest);
            ticketResponse = initiateApiRequest(apiRequest, ticketRow, requestRow);
        } catch (Exception e) {
            ticketResponse.setStatus(ActiveTicketResponseStatus.FAILED);
            ticketResponse.setErrorMessage(LogMessages.UNEXPECTED_EXCEPTION.format(e.getMessage()));
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return ticketResponse;
    }

    private ActiveTicketsRequest buildRequest(OptimizerRequest optimizerRequest)
                    throws IOException {
        UUID uuid = UUID.fromString(optimizerRequest.getRequestId());
        Topology topologyRow = topologyRequestManager.getExistingTopology(uuid);
        String topologyString = topologyRow.getTopology();
        TopologyResponse topologyResponse = new ObjectMapper().readValue(topologyString, TopologyResponse.class);
        ActiveTicketsRequest apiRequest = new ActiveTicketsRequest();
        apiRequest.setRequestId(optimizerRequest.getRequestId());
        apiRequest.setCommonData(optimizerRequest.getCommonData());
        apiRequest.setChangeWindows(optimizerRequest.getChangeWindows());
        apiRequest.setElements(getElementCriteria(topologyResponse));
        return apiRequest;
    }

    private List<TopologyPolicyInfo> getPolicies(OptimizerRequest optimizerRequest) {
        List<TopologyPolicyInfo> list = new ArrayList<>();
        for (PolicyInfo optInfo : optimizerRequest.getPolicies()) {
            TopologyPolicyInfo topInfo = new TopologyPolicyInfo();
            topInfo.setPolicyDescription(optInfo.getPolicyDescription());
            topInfo.setPolicyName(optInfo.getPolicyName());
            topInfo.setPolicyModifiers(optInfo.getPolicyModifiers());
            list.add(topInfo);
        }
        return list;
    }

    private List<ElementCriteria> getElementCriteria(TopologyResponse topologyResponse) {
        Map<String, ElementCriteria> map = new HashMap<>();
        for (TopologyElementInfo info : topologyResponse.getElements()) {
            ElementCriteria criteria = new ElementCriteria();
            criteria.setElementId(info.getElementId());
            criteria.setElementData(info.getElementData());
            map.put(criteria.getElementId(), criteria);
            if (info.getRequiredElements() != null) {
                for (String required : info.getRequiredElements()) {
                    if (!map.containsKey(required)) {
                        ElementCriteria crit = new ElementCriteria();
                        crit.setElementId(required);
                        map.put(crit.getElementId(), crit);
                    }
                }
            }
            if (info.getConstraintElements() != null) {
                for (ConstraintElements constraint : info.getConstraintElements()) {
                    if (!map.containsKey(constraint.getElementId())) {
                        ElementCriteria crit = new ElementCriteria();
                        crit.setElementId(constraint.getElementId());
                        map.put(crit.getElementId(), crit);
                    }
                }
            }

        }
        List<ElementCriteria> list = new ArrayList<>();
        list.addAll(map.values());
        return list;
    }


    private ActiveTicketsResponse initiateApiRequest(ActiveTicketsRequest apiRequest, Ticket ticketRow,
                    Request requestRow) throws CmsoException, JsonProcessingException {
        String url = env.getProperty("cmso.ticket.create.request.url");
        String username = env.getProperty("mechid.user");
        String password = pm.getProperty("mechid.pass", "");
        Client client = ClientBuilder.newClient();
        client.register(new BasicAuthenticatorFilter(username, password));
        client.register(new CmsoClientFilters());
        debug.debug("topology url / user: " + url + " / " + username);
        debug.debug("topology Request: " + new ObjectMapper().writeValueAsString(apiRequest));
        Observation.report(LogMessages.TOPOLOGY_REQUEST, "Begin", apiRequest.getRequestId(), url);
        ticketRow.setTicketsStart(System.currentTimeMillis());

        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(Entity.json(apiRequest));
        Observation.report(LogMessages.TOPOLOGY_REQUEST, "End", apiRequest.getRequestId(), url);
        ActiveTicketsResponse apiResponse = null;
        switch (response.getStatus()) {
            case 202:
                debug.debug("Successfully scheduled asynchronous ticket request: " + apiRequest.getRequestId());
                break;
            case 200:
                debug.debug("Successfully retrieved tickets: " + apiRequest.getRequestId());
                apiResponse = processApiResponse(apiRequest, response, ticketRow, requestRow);
                break;
            default:
                throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.UNEXPECTED_RESPONSE, url,
                                response.getStatusInfo().toString());
        }
        return apiResponse;
    }

    private ActiveTicketsResponse processApiResponse(ActiveTicketsRequest apiRequest, Response response,
                    Ticket ticketRow, Request requestRow) {
        String responseString = response.readEntity(String.class);
        ActiveTicketsResponse apiResponse = null;
        try {
            apiResponse = new ObjectMapper().readValue(responseString, ActiveTicketsResponse.class);
            ticketRow.setTickets(responseString);
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            apiResponse = new ActiveTicketsResponse();
            apiResponse.setRequestId(apiRequest.getRequestId());
            apiResponse.setStatus(ActiveTicketResponseStatus.FAILED);
            apiResponse.setErrorMessage(e.getMessage());
        }
        return apiResponse;
    }

}
