/*
 * Copyright © 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright © 2018 IBM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.onap.optf.cmso.dispatcher;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.onap.optf.cmso.common.BasicAuthenticatorFilter;
import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.filters.CmsoClientFilters;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.ticketmgt.TmClient;
import org.onap.optf.cmso.wf.bean.WfChangeManagementResponse;
import org.onap.optf.cmso.wf.bean.WfVidCmResponse;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CMSOClient {
    private static EELFLogger log = EELFManager.getInstance().getLogger(CMSOClient.class);
    private static EELFLogger metrics = EELFManager.getInstance().getMetricsLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    ChangeManagementScheduleDAO cmScheduleDAO;

    @Autowired
    ChangeManagementGroupDAO cmGroupDAO;

    @Autowired
    ScheduleDAO scheduleDAO;


    @Autowired
    Environment env;

    @Autowired
    PropertiesManagement pm;

    @Autowired
    TmClient tmClient;

    public void dispatch(ChangeManagementSchedule cmSchedule, Schedule schedule) {
        try {

            String url = env.getProperty("so.url");
            if (!url.endsWith("/"))
                url += "/";
            url = url + "schedule/" + cmSchedule.getVnfName();
            String callbackData = cmSchedule.getRequest();
            String user = env.getProperty("so.user", "");
            String pass = pm.getProperty("so.pass", "");
            Client client = ClientBuilder.newClient();
            if (!user.equals(""))
                client.register(new BasicAuthenticatorFilter(user, pass));
            client.register(new CmsoClientFilters());
            WebTarget target = client.target(url);
            ObjectMapper om = new ObjectMapper();
            JsonNode jsonEntity = om.readTree(callbackData);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            try {
                Response response = invocationBuilder.post(Entity.json(jsonEntity));
                switch (response.getStatus()) {
                    case 201: {
                        debug.debug("Response status=" + response.getStatus());
                        // Push the state up to the schedule.
                        processScheduleResponse(cmSchedule, schedule, response);
                        break;
                    }
                    case 200: {
                        debug.debug("Response status=" + response.getStatus());
                        // Push the state up to the schedule.
                        processScheduleResponse200(cmSchedule, schedule, response);
                        break;
                    }
                    case 400: // Bad request
                    case 500:
                    default: {
                        errors.error(LogMessages.UNEXPECTED_RESPONSE.toString(), "VID", response.getStatus(),
                                response.toString());
                        cmSchedule.setStatus(CMSStatusEnum.SchedulingFailed.toString());
                        cmSchedule.setStatusMessage(response.toString());
                        updateScheduleStatus(cmSchedule, schedule);
                        return;
                    }
                }
            }
            // No sense in retrying. These are time sensitive actions
            catch (ResponseProcessingException e) {
                errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
                cmSchedule.setStatus(CMSStatusEnum.SchedulingFailed.toString());
                cmSchedule.setStatusMessage(e.toString());
                updateScheduleStatus(cmSchedule, schedule);

            } catch (ProcessingException e) {
                errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
                cmSchedule.setStatus(CMSStatusEnum.SchedulingFailed.toString());
                cmSchedule.setStatusMessage(e.toString());
                updateScheduleStatus(cmSchedule, schedule);
            }
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            cmSchedule.setStatus(CMSStatusEnum.SchedulingFailed.toString());
            cmSchedule.setStatusMessage(e.toString());
            updateScheduleStatus(cmSchedule, schedule);
        }
    }

    private void processScheduleResponse(ChangeManagementSchedule cmSchedule, Schedule schedule, Response response)
            throws SchedulerException {
        WfChangeManagementResponse resp = response.readEntity(WfChangeManagementResponse.class);
        for (WfVidCmResponse cmResponse : resp.getCmResponses()) {
            if (cmResponse.getVnfName().equals(cmSchedule.getVnfName())) {
                cmSchedule.setStatus(CMSStatusEnum.Triggered.toString());
                cmSchedule.setDispatchTimeMillis(System.currentTimeMillis());
                cmSchedule.setMsoRequestId(cmResponse.getOrchestratorRequestId());
                // Push the state up to the schedule.
                updateTicket(cmSchedule, schedule);
                updateScheduleStatus(cmSchedule, schedule);
                return;
            }
        }
        cmSchedule.setStatus(CMSStatusEnum.SchedulingFailed.toString());
        cmSchedule.setStatusMessage("Response did not contain vnfName=" + cmSchedule.getVnfName());
        updateScheduleStatus(cmSchedule, schedule);
    }

    private void processScheduleResponse200(ChangeManagementSchedule cmSchedule, Schedule schedule, Response response)
            throws SchedulerException {

        JsonNode node = response.readEntity(JsonNode.class);
        log.info("Message returned from VID callback: " + node.toString());
        JsonNode status = node.get("status");
        Integer msoStatus = status.asInt();
        JsonNode entity = node.get("entity");
        if (msoStatus == 202) {
            JsonNode rr = entity.get("requestReferences");
            if (rr != null) {
                JsonNode requestId = rr.get("requestId");
                if (requestId != null) {
                    cmSchedule.setStatus(CMSStatusEnum.Triggered.toString());
                    cmSchedule.setDispatchTimeMillis(System.currentTimeMillis());
                    cmSchedule.setMsoRequestId(requestId.asText());
                    // Push the state up to the schedule.
                    updateTicket(cmSchedule, schedule);
                    updateScheduleStatus(cmSchedule, schedule);
                    return;
                }
            }
        }
        cmSchedule.setStatus(CMSStatusEnum.SchedulingFailed.toString());
        cmSchedule.setStatusMessage(node.toString());
        updateScheduleStatus(cmSchedule, schedule);
    }

    private void updateTicket(ChangeManagementSchedule cmSchedule, Schedule schedule) {
        try {
            String changeId = cmSchedule.getTmChangeId();
            if (changeId != null && !changeId.equals(""))
                tmClient.updateTicket(schedule, cmSchedule, changeId);
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
    }

    @Transactional
    public void updateScheduleStatus(ChangeManagementSchedule cmSchedule, Schedule schedule) {
        cmScheduleDAO.save(cmSchedule);
        if (schedule != null)
            scheduleDAO.save(schedule);

    }
}
