/*
 * Copyright © 2017-2018 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * Unless otherwise specified, all documentation contained herein is licensed under the Creative
 * Commons License, Attribution 4.0 Intl. (the "License"); you may not use this documentation except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.optf.cmso.ticketmgt;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.text.StringSubstitutor;
import org.joda.time.format.ISODateTimeFormat;
import org.onap.observations.Mdc;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.BasicAuthenticatorFilter;
import org.onap.optf.cmso.common.CmHelpers;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.common.exceptions.CmsoException;
import org.onap.optf.cmso.filters.CmsoClientFilters;
import org.onap.optf.cmso.model.ChangeManagementGroup;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.DomainData;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDao;
import org.onap.optf.cmso.service.rs.models.CmDomainDataEnum;
import org.onap.optf.cmso.service.rs.models.HealthCheckComponent;
import org.onap.optf.cmso.ticketmgt.TmEndpoints.Endpoint;
import org.onap.optf.cmso.ticketmgt.TmStatusClient.ClosureCode;
import org.onap.optf.cmso.ticketmgt.bean.BuildCreateRequest;
import org.onap.optf.cmso.ticketmgt.bean.BuildCreateRequest.Variables;
import org.onap.optf.cmso.ticketmgt.bean.TmAsset;
import org.onap.optf.cmso.ticketmgt.bean.TmChangeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The Class TmClient.
 */
@Component
public class TmClient {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    @Autowired
    PropertiesManagement pm;

    @Autowired
    ChangeManagementScheduleDao cmScheduleDao;

    @Autowired
    BuildCreateRequest buildCreateRequest;

    @Autowired
    TmEndpoints tmEndpoints;

    /**
     * Creates the change ticket.
     *
     * @param schedule the schedule
     * @param group the group
     * @param vnfNames the vnf names
     * @param domainData the domain data
     * @return the string
     * @throws CmsoException the CMS exception
     */
    public String createChangeTicket(Schedule schedule, ChangeManagementGroup group, List<String> vnfNames,
                    List<DomainData> domainData) throws CmsoException {

        String changeId = "";
        String workflowName = CmHelpers.getDomainData(domainData, CmDomainDataEnum.WorkflowName);
        Map<String, Object> variables = getVariables(schedule, group, vnfNames, domainData);
        List<TmAsset> assetList = getAssetList(vnfNames);
        JsonNode createChangeRecord = buildCreateRequest.createChangeRecordRequest(variables, assetList, workflowName);
        debug.debug("createChangeRecord=" + createChangeRecord.toString());
        changeId = postCreateChangeTicket(createChangeRecord, schedule.getScheduleId());
        return changeId;
    }

    /**
     * Close ticket.
     *
     * @param schedule the schedule
     * @param group the group
     * @param cmSchedules the cm schedules
     * @param changeId the change id
     * @param closureCode the closure code
     * @param closingComments the closing comments
     * @throws CmsoException the CMS exception
     */
    public void closeTicket(Schedule schedule, ChangeManagementGroup group, List<ChangeManagementSchedule> cmSchedules,
                    String changeId, ClosureCode closureCode, String closingComments) throws CmsoException {
        Map<String, Object> variables =
                        getCloseVariables(schedule, group, cmSchedules, changeId, closureCode, closingComments);
        JsonNode closeChangeRecord = buildCreateRequest.createCloseCancelChangeRecord(variables);
        debug.debug("closeChangeRecord=" + closeChangeRecord.toString());
        postCloseChangeTicket(closeChangeRecord, schedule.getScheduleId(), changeId);
    }

    /**
     * Cancel ticket.
     *
     * @param schedule the schedule
     * @param cms the cms
     * @param changeId the change id
     * @throws CmsoException the CMS exception
     */
    public void cancelTicket(Schedule schedule, ChangeManagementSchedule cms, String changeId) throws CmsoException {
        Map<String, Object> variables = getCancelVariables(schedule, changeId);
        JsonNode cancelChangeRecord = buildCreateRequest.createCancelChangeRecord(variables);
        debug.debug("cancelChangeRecord=" + cancelChangeRecord.toString());
        postCloseChangeTicket(cancelChangeRecord, schedule.getScheduleId(), changeId);
    }

    /**
     * Update ticket.
     *
     * @param schedule the schedule
     * @param cms the cms
     * @param changeId the change id
     * @throws CmsoException the CMS exception
     */
    public void updateTicket(Schedule schedule, ChangeManagementSchedule cms, String changeId) throws CmsoException {
        Map<String, Object> variables = getUpdateVariables(schedule, changeId);
        JsonNode updateChangeRecord = buildCreateRequest.createUpdateChangeRecord(variables);
        debug.debug("updateChangeRecord=" + updateChangeRecord.toString());
        postUpdateChangeTicket(updateChangeRecord, schedule.getScheduleId(), changeId);
    }

    /**
     * Gets the change ticket.
     *
     * @param changeId the change id
     * @return the change ticket
     */
    public TmChangeInfo getChangeTicket(String changeId) {
        Map<String, String> mdcSave = Mdc.save();
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("changeId", changeId);
            JsonNode getChangeRecord = buildCreateRequest.createUpdateChangeRecord(variables);
            Response response = tmPost(Endpoint.GET, getChangeRecord, UUID.randomUUID().toString());
            switch (response.getStatus()) {
                case 200: {
                    TmChangeInfo resp = response.readEntity(TmChangeInfo.class);
                    if (resp != null) {
                        return resp;
                    }
                }
                    break;
                default: {
                    Observation.report(LogMessages.UNEXPECTED_RESPONSE, "TM", String.valueOf(response.getStatus()),
                                    response.toString());
                }
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e.toString());
        } finally {
            Mdc.restore(mdcSave);
        }
        return null;
    }

    private Map<String, Object> getCloseVariables(Schedule schedule, ChangeManagementGroup group,
                    List<ChangeManagementSchedule> cmSchedules, String changeId, ClosureCode closureCode,
                    String closingComments) {
        String requesterId = schedule.getUserId();
        if (requesterId.length() > Variables.requesterId.getMaxLength()) {
            requesterId = requesterId.substring(0, Variables.requesterId.getMaxLength());
        }
        long actualStartDate = 0;
        long actualEndDate = 0;
        for (ChangeManagementSchedule cms : cmSchedules) {
            if ((cms.getDispatchTimeMillis() != null) && (actualStartDate == 0 || cms.getDispatchTimeMillis() < actualStartDate)) {
                    actualStartDate = cms.getDispatchTimeMillis();
            }
            if ((cms.getExecutionCompletedTimeMillis() != null) && (cms.getExecutionCompletedTimeMillis() > actualEndDate)) {
                    actualEndDate = cms.getExecutionCompletedTimeMillis();
            }
        }
        if (closureCode != ClosureCode.Successful) {
            if (actualEndDate == 0) {
                actualEndDate = System.currentTimeMillis();
            }
            if (actualStartDate == 0) {
                actualStartDate = actualEndDate - 1000;
            }

        }
        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put(Variables.status.toString(), "Closed");
        variables.put(Variables.requesterId.toString(), requesterId);
        variables.put(Variables.actualStartDate.toString(), actualStartDate / 1000);
        variables.put(Variables.actualEndDate.toString(), actualEndDate / 1000);
        variables.put(Variables.changeId.toString(), changeId);
        variables.put(Variables.closureCode.toString(), closureCode.toString());
        variables.put(Variables.closingComments.toString(), closingComments);
        return variables;
    }

    private Map<String, Object> getCancelVariables(Schedule schedule, String changeId) {
        String requesterId = schedule.getUserId();
        Map<String, Object> variables = new HashMap<String, Object>();
        if (requesterId.length() > Variables.requesterId.getMaxLength()) {
            requesterId = requesterId.substring(0, Variables.requesterId.getMaxLength());
        }
        variables.put(Variables.requesterId.toString(), requesterId);
        variables.put(Variables.changeId.toString(), changeId);
        return variables;
    }

    private Map<String, Object> getUpdateVariables(Schedule schedule, String changeId) {
        String requesterId = schedule.getUserId();
        Map<String, Object> variables = new HashMap<String, Object>();
        if (requesterId.length() > Variables.requesterId.getMaxLength()) {
            requesterId = requesterId.substring(0, Variables.requesterId.getMaxLength());
        }
        variables.put(Variables.requesterId.toString(), requesterId);
        variables.put(Variables.changeId.toString(), changeId);
        return variables;
    }

    private void postCloseChangeTicket(JsonNode closeChangeRecord, String scheduleId, String changeId)
                    throws CmsoException {
        Map<String, String> mdcSave = Mdc.save();
        try {
            Response response = null;
            debug.debug("postCloseChangeTicket {}", closeChangeRecord.asText());
            Observation.report(LogMessages.TM_CLOSE_CHANGE_RECORD, "Begin", scheduleId, changeId);
            // response = vtmPost(url, closeChangeRecord, scheduleId);
            response = tmPost(Endpoint.CLOSE, closeChangeRecord, scheduleId);
            Observation.report(LogMessages.TM_CLOSE_CHANGE_RECORD, "End", scheduleId, changeId);
            switch (response.getStatus()) {
                case 200: {
                    String resp = response.readEntity(String.class);
                    debug.debug("response=" + resp.toString());
                }
                    break;
                case 400: {
                    String respString = response.readEntity(String.class);
                    debug.debug("response=" + respString);
                    if (!isAlreadyClosed(respString)) {
                        Observation.report(LogMessages.UNEXPECTED_RESPONSE, "vTM", String.valueOf(response.getStatus()),
                                        response.toString() + " : " + respString);
                        throw new CmsoException(Status.PRECONDITION_FAILED, LogMessages.UNABLE_TO_CLOSE_CHANGE_TICKET,
                                        scheduleId, changeId, respString);
                    }
                }
                    break;
                default: {
                    String message = response.readEntity(String.class);
                    Observation.report(LogMessages.UNEXPECTED_RESPONSE, "vTM", String.valueOf(response.getStatus()),
                                    response.toString() + " : " + message);
                    throw new CmsoException(Status.PRECONDITION_FAILED, LogMessages.UNABLE_TO_CLOSE_CHANGE_TICKET,
                                    scheduleId, changeId, message);
                }
            }
        } catch (ProcessingException e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            throw new CmsoException(Status.PRECONDITION_FAILED, LogMessages.UNABLE_TO_CLOSE_CHANGE_TICKET, scheduleId,
                            changeId, e.toString());
        } finally {
            Mdc.restore(mdcSave);
        }

    }

    private boolean isAlreadyClosed(String respString) {
        try {
            ObjectMapper om = new ObjectMapper();
            ObjectNode resp = om.readValue(respString, ObjectNode.class);
            if (resp != null) {
                debug.debug("resp=" + resp.toString());
                ArrayNode errs = (ArrayNode) resp.get("serviceException");
                if (errs != null) {
                    for (JsonNode jn : errs) {
                        ObjectNode on = (ObjectNode) jn;
                        String messageId = on.get("messageId").asText();
                        String text = on.get("text").asText();
                        if (messageId.equals("SVC40006") && text.contains("is in Closed status")) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return false;
    }

    private List<TmAsset> getAssetList(List<String> vnfNames) {
        List<TmAsset> assetList = new ArrayList<TmAsset>();
        for (String vnfName : vnfNames) {
            TmAsset asset = new TmAsset();
            asset.setAssetId(vnfName);
            assetList.add(asset);
        }
        return assetList;
    }

    private Map<String, Object> getVariables(Schedule schedule, ChangeManagementGroup group, List<String> vnfNames,
                    List<DomainData> domainData) {
        String requesterId = schedule.getUserId();
        Map<String, Object> variables = new HashMap<String, Object>();

        String vnfList = vnfNames.toString();
        if (vnfList.length() > Variables.vnfList.getMaxLength()) {
            vnfList = vnfList.substring(0, Variables.vnfList.getMaxLength());
        }
        if (requesterId.length() > Variables.requesterId.getMaxLength()) {
            requesterId = requesterId.substring(0, Variables.requesterId.getMaxLength());
        }

        variables.put(Variables.vnfList.toString(), vnfList);
        variables.put(Variables.vnfName.toString(), vnfNames.get(0));
        variables.put(Variables.requesterId.toString(), requesterId);
        Long plannedStartDate = group.getStartTimeMillis();
        Long plannedEndDate = group.getFinishTimeMillis();
        variables.put(Variables.plannedStartDate.toString(), plannedStartDate / 1000);
        variables.put(Variables.plannedEndDate.toString(), plannedEndDate / 1000);
        Long validationStartTime = plannedStartDate + group.getNormalDurationInSecs();
        Long backoutStartTime = plannedEndDate - group.getAdditionalDurationInSecs();
        variables.put(Variables.validationStartTime.toString(), validationStartTime / 1000);
        variables.put(Variables.backoutStartTime.toString(), backoutStartTime / 1000);
        // These will be display UTC -
        variables.put(Variables.validationStartTimeDisplay.toString(),
                        ISODateTimeFormat.dateTimeNoMillis().print(validationStartTime));
        variables.put(Variables.backoutStartTimeDisplay.toString(),
                        ISODateTimeFormat.dateTimeNoMillis().print(backoutStartTime));
        variables.put(Variables.plannedStartTimeDisplay.toString(),
                        ISODateTimeFormat.dateTimeNoMillis().print(plannedStartDate));
        variables.put(Variables.plannedEndTimeDisplay.toString(),
                        ISODateTimeFormat.dateTimeNoMillis().print(plannedEndDate));

        // Ticket field values can be passed in via the DomainData
        JsonNode defaultValues = buildCreateRequest.getYaml("DefaultChangeTicketProperties");
        Iterator<String> names = defaultValues.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            JsonNode valueNode = defaultValues.get(name);
            String value = valueNode.asText("");
            variables.put("dd." + name, value);
        }

        // Override defaults from the request.
        for (DomainData dd : domainData) {
            String name = dd.getName();
            String value = dd.getValue();
            variables.put("dd." + name, value);
        }
        // Allow values to be templates as well
        // Did this so plans: ${dd.workflowName}
        for (String name : variables.keySet()) {
            Object value = variables.get(name);
            if (value instanceof String) {
                StringSubstitutor sub = new StringSubstitutor(variables);
                value = sub.replace(value.toString());
                variables.put(name, value);
            }
        }
        return variables;

    }

    private String postCreateChangeTicket(JsonNode createChangeRecord, String scheduleId) throws CmsoException {
        String changeId = null;
        Map<String, String> mdcSave = Mdc.save();
        try {
            Response response = null;
            debug.debug("postCreateChangeTicket {}", createChangeRecord.toString());
            Observation.report(LogMessages.TM_CREATE_CHANGE_RECORD, "Begin", scheduleId);
            // response = vtmPost(url, createChangeRecord, scheduleId);
            response = tmPost(Endpoint.CREATE, createChangeRecord, scheduleId);
            Observation.report(LogMessages.TM_CREATE_CHANGE_RECORD, "End", scheduleId);
            switch (response.getStatus()) {
                case 200: {
                    ObjectNode json = response.readEntity(ObjectNode.class);
                    if (json != null) {
                        debug.debug("Message returned by vTM " + json.toString());
                    }
                    if (json != null && json.get("changeId") != null) {
                        changeId = json.get("changeId").textValue();
                        if (changeId != null) {
                            debug.debug("ChangeId=" + changeId);
                        }
                    } else {
                        Observation.report(LogMessages.UNEXPECTED_RESPONSE, "vTM", String.valueOf(response.getStatus()),
                                        response.toString() + " : " + "Response is empty");
                        throw new CmsoException(Status.EXPECTATION_FAILED, LogMessages.UNABLE_TO_CREATE_CHANGE_TICKET,
                                        scheduleId, "Response is empty");
                    }
                }
                    break;
                default: {
                    String message = response.readEntity(String.class);
                    Observation.report(LogMessages.UNEXPECTED_RESPONSE, "vTM", String.valueOf(response.getStatus()),
                                    response.toString() + " : " + message);
                    throw new CmsoException(Status.EXPECTATION_FAILED, LogMessages.UNABLE_TO_CREATE_CHANGE_TICKET,
                                    scheduleId, message);
                }
            }
        } catch (ProcessingException e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            throw new CmsoException(Status.EXPECTATION_FAILED, LogMessages.UNABLE_TO_CREATE_CHANGE_TICKET, scheduleId,
                            e.toString());
        } finally {
            Mdc.restore(mdcSave);
        }
        return changeId;
    }

    private String postUpdateChangeTicket(JsonNode updateChangeRecord, String scheduleId, String changeId)
                    throws CmsoException {
        Map<String, String> mdcSave = Mdc.save();
        try {
            String url = env.getProperty("vtm.url") + env.getProperty("vtm.updatePath");

            Response response = null;
            debug.debug("postUpdateChangeTicket {}", updateChangeRecord.toString());
            Observation.report(LogMessages.TM_UPDATE_CHANGE_RECORD, "Begin", scheduleId, changeId, url);
            // response = vtmPost(url, updateChangeRecord, scheduleId);
            response = tmPost(Endpoint.UPDATE, updateChangeRecord, scheduleId);
            Observation.report(LogMessages.TM_UPDATE_CHANGE_RECORD, "End", scheduleId, changeId, url);
            switch (response.getStatus()) {
                case 200: {
                    String resp = response.readEntity(String.class);
                    debug.debug("response=" + resp.toString());
                }
                    break;
                default: {
                    String message = response.readEntity(String.class);
                    Observation.report(LogMessages.UNEXPECTED_RESPONSE, "vTM", String.valueOf(response.getStatus()),
                                    response.toString() + " : " + message);
                    throw new CmsoException(Status.PRECONDITION_FAILED, LogMessages.UNABLE_TO_UPDATE_CHANGE_TICKET,
                                    scheduleId, changeId, message);
                }
            }
        } catch (ProcessingException e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            throw new CmsoException(Status.PRECONDITION_FAILED, LogMessages.UNABLE_TO_UPDATE_CHANGE_TICKET, scheduleId,
                            changeId, e.toString());
        } finally {
            Mdc.restore(mdcSave);
        }
        return changeId;
    }


    private Response tmPost(Endpoint ep, Object request, String scheduleId) throws CmsoException {
        Response response = null;
        List<String> endpoints = new ArrayList<>();
        String url = tmEndpoints.getEndpoint(ep, endpoints);
        while (url != null) {
            try {
                String user = env.getProperty("mechid.user");
                String pass = pm.getProperty("mechid.pass", "");
                // Cannot provide changeId. Interesting.
                // This should be replaced by fetch
                // For now, make a best effort to get the passed changeId

                Client client = ClientBuilder.newClient();
                client.register(new BasicAuthenticatorFilter(user, pass));
                client.register(new CmsoClientFilters());
                WebTarget target = client.target(url);
                Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
                ObjectMapper mapper = new ObjectMapper();
                String jsonRequest = mapper.writeValueAsString(request);
                debug.debug("TM URL = " + url + " user=" + user + " : " + jsonRequest);
                response = invocationBuilder.post(Entity.json(request));
                // String message = response.readEntity(String.class);
                // debug.debug("Return from " + url + " : " + response.toString() + "\n" +
                // message);
                debug.debug("Return from " + url + " : " + response.toString());
                return response;
            } catch (ProcessingException e) {
                Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.toString());
                url = tmEndpoints.getNextEndpoint(ep, endpoints);
                if (url == null || !tryNextUrl(e)) {
                    throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.UNABLE_TO_CREATE_CHANGE_TICKET,
                                    scheduleId, e.getMessage());
                }
            } catch (Exception e) {
                Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.toString());
                throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.UNABLE_TO_CREATE_CHANGE_TICKET,
                                scheduleId, e.getMessage());
            }
        }
        return response;
    }

    private boolean tryNextUrl(ProcessingException exc) {
        if (exc.getCause() instanceof UnknownHostException) {
            return true;
        }
        return true;
    }

    /**
     * Health check.
     *
     * @return the health check component
     */
    public HealthCheckComponent healthCheck() {
        // No op
        HealthCheckComponent hcc = new HealthCheckComponent();
        hcc.setName("TM Interface");
        hcc.setUrl("");
        hcc.setHealthy(true);
        hcc.setStatus("OK");
        return hcc;
    }
}
