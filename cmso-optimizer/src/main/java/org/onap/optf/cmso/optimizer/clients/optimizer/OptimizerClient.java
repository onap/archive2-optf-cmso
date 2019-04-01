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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.BasicAuthenticatorFilter;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.common.exceptions.CmsoException;
import org.onap.optf.cmso.optimizer.clients.common.models.ElementCriteria;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerResults;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyPolicyInfo;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyRequest;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse.TopologyRequestStatus;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.filters.CmsoClientFilters;
import org.onap.optf.cmso.optimizer.model.Optimizer;
import org.onap.optf.cmso.optimizer.model.Request;
import org.onap.optf.cmso.optimizer.model.Topology;
import org.onap.optf.cmso.optimizer.model.dao.OptimizerDao;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
import org.onap.optf.cmso.optimizer.model.dao.TicketDao;
import org.onap.optf.cmso.optimizer.model.dao.TopologyDao;
import org.onap.optf.cmso.optimizer.service.rs.models.ElementInfo;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerRequest;
import org.onap.optf.cmso.optimizer.service.rs.models.PolicyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The Class OptimizerClient.
 */
@Component
public class OptimizerClient {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    @Autowired
    PropertiesManagement pm;

    @Autowired
    RequestDao requestDao;

    @Autowired
    TopologyDao topologyDao;

    @Autowired
    TicketDao ticketDao;

    @Autowired
    OptimizerDao optimizerDao;


    /**
     * Make request.
     *
     * @param request the request
     * @param optimizer the topology
     * @return the topology response
     */
    public OptimizerResults makeRequest(Request request, Optimizer optimizer) {
        Integer maxAttempts = env.getProperty("cmso.optimizer.maxAttempts", Integer.class, 20);
        OptimizerResults optimizerResults = new OptimizerResults();
        if (optimizer.getTopologyRetries() >= maxAttempts) {
            optimizerResults.setStatus(TopologyRequestStatus.FAILED);
            optimizerResults.setErrorMessage(LogMessages.EXCEEDED_RETRY_LIMIT.format(
                            "Topology", maxAttempts.toString()));
            Observation.report(LogMessages.EXCEEDED_RETRY_LIMIT, "Topology", maxAttempts.toString());
            return optimizerResults;
        }
        TopologyRequest topologyRequest = new TopologyRequest();
        ObjectMapper om = new ObjectMapper();
        String originalRequest = request.getRequest();
        OptimizerRequest optimizerRequest = null;;
        try {
            optimizerRequest = om.readValue(originalRequest, OptimizerRequest.class);
        } catch (Exception e) {
            optimizerResults.setStatus(TopologyRequestStatus.FAILED);
            optimizerResults.setErrorMessage(LogMessages.UNEXPECTED_EXCEPTION.format(e.getMessage()));
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        topologyRequest = new TopologyRequest();
        topologyRequest.setRequestId(optimizerRequest.getRequestId());
        topologyRequest.setCommonData(optimizerRequest.getCommonData());
        topologyRequest.setElements(getElementCriteria(optimizerRequest));
        topologyRequest.setPolicies(getPolicies(optimizerRequest));
        try {
            optimizerResults = initiateTopology(topologyRequest, optimizer, request);
        } catch (Exception e) {
            optimizerResults.setStatus(TopologyRequestStatus.FAILED);
            optimizerResults.setErrorMessage(LogMessages.UNEXPECTED_EXCEPTION.format(e.getMessage()));
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return optimizerResults;
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

    private List<ElementCriteria> getElementCriteria(OptimizerRequest optimizerRequest) {
        List<ElementCriteria> list = new ArrayList<>();
        for (ElementInfo info : optimizerRequest.getElements()) {
            ElementCriteria criteria = new ElementCriteria();
            criteria.setElementId(info.getElementId());
            criteria.setElementData(info.getElementData());
            list.add(criteria);
        }
        return list;
    }

    private OptimizerResults initiateTopology(TopologyRequest request, Topology topology, Request requestRow)
                    throws CmsoException, JsonProcessingException {
        String url = env.getProperty("cmso.topology.create.request.url");
        String username = env.getProperty("mechid.user");
        String password = pm.getProperty("mechid.pass", "");
        Client client = ClientBuilder.newClient();
        client.register(new BasicAuthenticatorFilter(username, password));
        client.register(new CmsoClientFilters());
        debug.debug("topology url / user: " + url + " / " + username);
        debug.debug("topology Request: " + new ObjectMapper().writeValueAsString(request));
        Observation.report(LogMessages.TOPOLOGY_REQUEST, "Begin", request.getRequestId(), url);
        topology.setTopologyStart(System.currentTimeMillis());
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(Entity.json(request));
        Observation.report(LogMessages.TOPOLOGY_REQUEST, "End", request.getRequestId(), url);
        OptimizerResults optimizerResults = null;
        switch (response.getStatus()) {
            case 202:
                debug.debug("Successfully scheduled asynchronous topology: " + request.getRequestId());
                break;
            case 200:
                debug.debug("Successfully retrieved topology: " + request.getRequestId());
                optimizerResults = processTopologyResponse(request, response, topology, requestRow);
                break;
            default:
                throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.UNEXPECTED_RESPONSE, url,
                                response.getStatusInfo().toString());
        }
        return optimizerResults;
    }

    private OptimizerResults processTopologyResponse(TopologyRequest request, Response response, Topology topology,
                    Request requestRow) {
        String responseString = response.readEntity(String.class);
        OptimizerResults optimizerResults = null;
        try {
            optimizerResults = new ObjectMapper().readValue(responseString, OptimizerResults.class);
            topology.setTopology(responseString);
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            optimizerResults = new OptimizerResults();
            optimizerResults.setRequestId(request.getRequestId());
            optimizerResults.setStatus(TopologyRequestStatus.FAILED);
            optimizerResults.setErrorMessage(e.getMessage());
        }
        return optimizerResults;
    }

}
