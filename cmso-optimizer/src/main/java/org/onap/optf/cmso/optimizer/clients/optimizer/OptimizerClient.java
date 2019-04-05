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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.common.exceptions.CmsoException;
import org.onap.optf.cmso.optimizer.availability.policies.PolicyManager;
import org.onap.optf.cmso.optimizer.availability.policies.model.TimeLimitAndVerticalTopology;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerEngineResponse;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerEngineResponse.OptimizerEngineResponseStatus;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerParameters;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerResponseUtility;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerResults;
import org.onap.optf.cmso.optimizer.clients.ticketmgt.TicketMgtRequestManager;
import org.onap.optf.cmso.optimizer.clients.ticketmgt.models.ActiveTicketsResponse;
import org.onap.optf.cmso.optimizer.clients.topology.TopologyRequestManager;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.model.Optimizer;
import org.onap.optf.cmso.optimizer.model.Request;
import org.onap.optf.cmso.optimizer.model.Ticket;
import org.onap.optf.cmso.optimizer.model.Topology;
import org.onap.optf.cmso.optimizer.model.dao.OptimizerDao;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
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
    TopologyRequestManager topologyRequestManager;

    @Autowired
    TicketMgtRequestManager ticketMgtRequestManager;

    @Autowired
    OptimizerDao optimizerDao;

    @Autowired
    PolicyManager policyManager;

    /**
     * Make request.
     *
     * @param request the request
     * @param optimizer the Optimizer
     * @return the Optimizer response
     */
    public OptimizerEngineResponse makeRequest(Request request, Optimizer optimizer) {
        Integer maxAttempts = env.getProperty("cmso.optimizer.maxAttempts", Integer.class, 20);
        OptimizerEngineResponse apiResponse = new OptimizerEngineResponse();
        if (optimizer.getOptimizeRetries() >= maxAttempts) {
            apiResponse.setStatus(OptimizerEngineResponseStatus.FAILED);
            apiResponse.setErrorMessage(LogMessages.EXCEEDED_RETRY_LIMIT.format("Optimizer", maxAttempts.toString()));
            Observation.report(LogMessages.EXCEEDED_RETRY_LIMIT, "Optimizer", maxAttempts.toString());
            return apiResponse;
        }
        OptimizerRequest optimizerRequest = null;
        TopologyResponse topologyResponse = null;
        ActiveTicketsResponse ticketResponse = null;
        try {
            optimizerRequest = getOptimizerRequest(request);
            topologyResponse = getTopologyResponse(request.getUuid());
            ticketResponse   = getTicketResponse(request.getUuid());
            OptimizerParameters optimizerParameters =
                            buildOptimizerParameters(optimizerRequest, topologyResponse, ticketResponse);
            apiResponse = initiateOptimizer(optimizerParameters, request);
        } catch (Exception e) {
            apiResponse.setStatus(OptimizerEngineResponseStatus.FAILED);
            apiResponse.setErrorMessage(LogMessages.UNEXPECTED_EXCEPTION.format(e.getMessage()));
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return apiResponse;
    }

    private OptimizerParameters buildOptimizerParameters(OptimizerRequest optimizerRequest,
                    TopologyResponse topologyResponse, ActiveTicketsResponse ticketResponse) throws ParseException {
        List<TimeLimitAndVerticalTopology> policies = getPolicies(optimizerRequest);
        OptimizerParameters parameters = new OptimizerParameters();
        ElementAvailability elementAvailability =
                        new ElementAvailability(policies, optimizerRequest, topologyResponse, ticketResponse);
        elementAvailability.populate(parameters);

        // Policies for this are undefined...
        parameters.setAttributes(getAttributes(policies, optimizerRequest));
        parameters.setAttributesRange(getAttributesRange(policies, optimizerRequest));
        parameters.setAttributeConcurrencyLimit(getAttributeConcrrencyLimit(policies, optimizerRequest));
        parameters.setNumAttributes(new Long(parameters.getAttributesRange().size()));
        return parameters;
    }

    private List<List<Long>> getAttributeConcrrencyLimit(List<TimeLimitAndVerticalTopology> policies,
                    OptimizerRequest optimizerRequest) {
        List<List<Long>> list = new ArrayList<>();
        return list;
    }

    private List<Long> getAttributesRange(List<TimeLimitAndVerticalTopology> policies,
                    OptimizerRequest optimizerRequest) {
        List<Long> list = new ArrayList<>();
        return list;
    }

    private List<List<Long>> getAttributes(List<TimeLimitAndVerticalTopology> policies,
                    OptimizerRequest optimizerRequest) {
        List<List<Long>> list = new ArrayList<>();
        return list;
    }


    private List<TimeLimitAndVerticalTopology> getPolicies(OptimizerRequest optimizerRequest) {
        List<TimeLimitAndVerticalTopology> list = new ArrayList<>();
        for (PolicyInfo policyInfo : optimizerRequest.getPolicies()) {
            TimeLimitAndVerticalTopology policy =
                            policyManager.getTimeLimitAndVerticalTopologyByName(policyInfo.getPolicyName());
            list.add(policy);
        }
        return list;
    }

    private ActiveTicketsResponse getTicketResponse(UUID uuid)
                    throws JsonParseException, JsonMappingException, IOException {
        Ticket ticketRow = ticketMgtRequestManager.getExistingTickets(uuid);
        String ticketString = ticketRow.getTickets();
        ObjectMapper om = new ObjectMapper();
        return om.readValue(ticketString, ActiveTicketsResponse.class);
    }

    private TopologyResponse getTopologyResponse(UUID uuid)
                    throws JsonParseException, JsonMappingException, IOException {
        Topology topologyRow = topologyRequestManager.getExistingTopology(uuid);
        String topologyString = topologyRow.getTopology();
        ObjectMapper om = new ObjectMapper();
        return om.readValue(topologyString, TopologyResponse.class);
    }

    private OptimizerRequest getOptimizerRequest(Request request)
                    throws JsonParseException, JsonMappingException, IOException {
        String requestString = request.getRequest();
        ObjectMapper om = new ObjectMapper();
        return om.readValue(requestString, OptimizerRequest.class);
    }


    private OptimizerEngineResponse initiateOptimizer(OptimizerParameters request, Request requestRow)
                    throws CmsoException, JsonProcessingException {


        UUID uuid = requestRow.getUuid();
        OptimizerEngineResponse apiResponse = new OptimizerEngineResponse();
        apiResponse.setRequestId(uuid.toString());

        String workingFolderString = env.getProperty("cmso.optimizer.engine.working.folder", "data/engine");
        File workingFolder = new File(workingFolderString + File.separator + requestRow.getUuid().toString());
        workingFolder.mkdirs();
        Long timeLimit = env.getProperty("cmso.minizinc.command.timelimit", Long.class);
        // TODO calculate time limit
        Process process = null;
        try {
            Path inputFileName = Paths.get(workingFolder.getAbsolutePath(), "input.dzn");
            Path outputFileName = Paths.get(workingFolder.getAbsolutePath(), "results.yaml");
            String dzn = request.toMiniZinc();
            Files.write(inputFileName, dzn.getBytes());
            ProcessBuilder processBuilder = buildCommand(inputFileName, outputFileName, timeLimit.toString());
            process = processBuilder.start();
            // debug.debug("engine command=" + commandString);
            String stdout = IOUtils.toString(process.getInputStream(), "UTF-8");
            String stderr = IOUtils.toString(process.getErrorStream(), "UTF-8");
            debug.debug("stdout=" + stdout);
            debug.debug("stderr=" + stderr);
            OptimizerResponseUtility responseUtility = new OptimizerResponseUtility();
            OptimizerResults optimizerResults = responseUtility.parseOptimizerResult(outputFileName.toFile());
            apiResponse.setOptimizerResults(optimizerResults);
            apiResponse.setStatus(OptimizerEngineResponseStatus.COMPLETED);

        } catch (Exception e) {
            apiResponse.setStatus(OptimizerEngineResponseStatus.FAILED);
            apiResponse.setErrorMessage(LogMessages.UNEXPECTED_EXCEPTION.format(e.getMessage()));
            Observation.report(LogMessages.UNEXPECTED_RESPONSE, e, e.getMessage());
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
            if (workingFolder.exists()) {
                workingFolder.delete();
            }
        }
        return apiResponse;
    }

    private ProcessBuilder buildCommand(Path inputFileName, Path outputFileName, String timeLimit) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> command = new ArrayList<>();
        String commandline =
                        env.getProperty("cmso.minizinc.command.commandline", "/bin/bash -x scripts/minizinc/run.sh");
        String minizinc = env.getProperty("cmso.minizinc.command.exe", "minizinc");
        String solver = env.getProperty("cmso.minizinc.command.solver", "OSICBC");
        String script = env.getProperty("cmso.minizinc.command.mzn", "scripts/minizinc/generic_attributes.mzn");
        Map<String, String> environment = processBuilder.environment();
        environment.put("MINIZINC", minizinc);
        environment.put("MINIZINC_SOLVER", solver);
        environment.put("MINIZINC_TIMELIMIT", timeLimit);
        environment.put("MINIZINC_OUTPUT", outputFileName.toString());
        environment.put("MINIZINC_MZN", script);
        environment.put("MINIZINC_DZN", inputFileName.toString());
        for (String arg : commandline.split(" ")) {
            command.add(arg);
        }
        processBuilder.command(command);
        return processBuilder;
    }


}
