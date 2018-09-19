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

package org.onap.optf.cmso.optimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.onap.optf.cmso.common.Mdc;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.filters.CMSOClientFilters;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.optimizer.bean.CMOptimizerRequest;
import org.onap.optf.cmso.optimizer.bean.CMRequestInfo;
import org.onap.optf.cmso.optimizer.bean.CMSchedulingInfo;
import org.onap.optf.cmso.optimizer.bean.CMVnfDetails;
import org.onap.optf.cmso.service.rs.models.CMSInfo;
import org.onap.optf.cmso.service.rs.models.ChangeWindowMessage;
import org.onap.optf.cmso.service.rs.models.HealthCheckComponent;
import org.onap.optf.cmso.service.rs.models.VnfDetailsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CMSOptimizerClient {
    private static EELFLogger log = EELFManager.getInstance().getLogger(CMSOptimizerClient.class);
    private static EELFLogger metrics = EELFManager.getInstance().getMetricsLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    ScheduleDAO scheduleDAO;

    @Autowired
    Environment env;

    @Autowired
    PropertiesManagement pm;

    public boolean scheduleSniroOptimization(Integer id) {
        Map<String, String> mdcSave = Mdc.save();
        try {
            String snirourl = env.getProperty("cmso.optimizer.url");
            String snirocallbackurl = env.getProperty("cmso.optimizer.callbackurl");
            String username = env.getProperty("mechid.user");
            Integer maxAttempts = env.getProperty("cmso.sniro.maxAttempts", Integer.class, 20);

            // Ensure that only one cmso is requsting this call to SNIRO
            Schedule schedule = scheduleDAO.lockOne(id);
            if (!schedule.getStatus().equals(CMSStatusEnum.PendingSchedule.toString()))
                return false;

            String password = pm.getProperty("mechid.pass", "");
            //
            // Only 'successfully' process one schedule per invocation
            // If a schedule attemp fails and it could be because of the data in the
            // message, try the next one. We don't want bad data to
            //
            if (schedule.getOptimizerAttemptsToSchedule() >= maxAttempts) {
                schedule.setStatus(CMSStatusEnum.OptimizationFailed.toString());
                schedule.setOptimizerMessage("Maximum number of attempts exceeded " + maxAttempts);
                updateScheduleStatus(schedule);
                return true;
            }
            CMOptimizerRequest cmReq = new CMOptimizerRequest();
            try {
                CMSInfo info = reconstituteMessage(schedule);
                if (info == null) {
                    return true;
                }
                buildRequest(cmReq, info, schedule, snirocallbackurl);
            } catch (Exception e) {
                errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
                schedule.setStatus(CMSStatusEnum.OptimizationFailed.toString());
                schedule.setOptimizerMessage("Unexpected exception: " + e.getMessage());
                updateScheduleStatus(schedule);
                return true;
            }

            // This service will call SNIO for each PendingOptimiztion
            // If the request is successfully scheduled in SNIRO, status will be
            // updated to OptimizationInProgress.
            Client client = ClientBuilder.newClient();
            client.register(new BasicAuthenticatorFilter(username, password));
            client.register(new CMSOClientFilters());
            WebTarget sniroTarget = client.target(snirourl);
            Invocation.Builder invocationBuilder = sniroTarget.request(MediaType.APPLICATION_JSON);
            try {
                //
                // First, push OptimizationInProgress to the DB (flush()) assuming a 202 status,
                // in case the SNIRO callback is received prior to the
                // commit of this transaction.
                // SNIRO Callback will throw an error if it receives a response in the incorrect
                // state.
                //
                schedule.setOptimizerTransactionId(cmReq.getRequestInfo().getTransactionId());
                schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
                schedule.setStatus(CMSStatusEnum.OptimizationInProgress.toString());
                updateScheduleStatus(schedule);
                debug.debug("SNIRO url / user: " + snirourl + " / " + username);
                debug.debug("SNIRO Request: " + new ObjectMapper().writeValueAsString(cmReq));
                log.info(LogMessages.OPTIMIZER_REQUEST, "Begin", schedule.getScheduleId(), snirourl);
                Mdc.metricStart(schedule.getScheduleId(), snirourl);
                Response response = invocationBuilder.post(Entity.json(cmReq));

                Mdc.metricEnd(response);
                metrics.info(LogMessages.OPTIMIZER_REQUEST, "End", schedule.getScheduleId(), snirourl);
                log.info(LogMessages.OPTIMIZER_REQUEST, "End", schedule.getScheduleId(), snirourl);
                switch (response.getStatus()) {
                    case 202:
                        debug.debug("Successfully scheduled optimization: " + schedule.getScheduleId());
                        // Scheduled with SNIRO
                        break;
                    case 400: // Bad request
                    {
                        schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
                        schedule.setOptimizerStatus("HTTP Status: " + response.getStatus());
                        String message = response.readEntity(String.class);
                        schedule.setOptimizerMessage(message);
                        schedule.setStatus(CMSStatusEnum.ScheduleFailed.toString());
                        // Need to understand the cause of this error. May be teh same as SNIRO
                        // down.
                        int tries = schedule.getOptimizerAttemptsToSchedule();
                        tries++;
                        schedule.setOptimizerAttemptsToSchedule(tries);
                        updateScheduleStatus(schedule);
                        errors.error(LogMessages.OPTIMIZER_EXCEPTION, message);
                        return true;
                    }

                    case 500:
                    default: {
                        String message = response.readEntity(String.class);
                        // SHould probably track the number of retries.
                        schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
                        int tries = schedule.getOptimizerAttemptsToSchedule();
                        tries++;
                        schedule.setStatus(CMSStatusEnum.ScheduleFailed.toString());
                        schedule.setOptimizerAttemptsToSchedule(tries);
                        schedule.setOptimizerMessage(message);
                        updateScheduleStatus(schedule);
                        /// Got processing error response
                        // may be transient, wait for next cycle.
                        errors.error(LogMessages.OPTIMIZER_EXCEPTION, message);
                        // Wait until next cycle and try again.
                        return false;
                    }

                }
                //
                return true;
            } catch (ResponseProcessingException e) {
                schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
                schedule.setOptimizerStatus("Failed to parse SNIRO response");
                schedule.setStatus(CMSStatusEnum.ScheduleFailed.toString());
                // Need to understand the cause of this error. May be teh same as SNIRO down.
                int tries = schedule.getOptimizerAttemptsToSchedule();
                tries++;
                schedule.setOptimizerAttemptsToSchedule(tries);
                updateScheduleStatus(schedule);
                // Getting invalid response from SNIRO.
                // May be data related.
                errors.error(LogMessages.OPTIMIZER_EXCEPTION, e, e.getMessage());
                return false;

            } catch (ProcessingException e) {
                // Don't track number of retries on IO error (SNIRO is down)
                schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
                schedule.setStatus(CMSStatusEnum.PendingSchedule.toString());
                updateScheduleStatus(schedule);
                /// Cannot connect to SNIRO
                errors.error(LogMessages.OPTIMIZER_EXCEPTION, e, e.getMessage());
                // Wait until next cycle
                return false;
            }
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());

        } finally {
            Mdc.restore(mdcSave);
        }
        return false;
    }

    private void buildRequest(CMOptimizerRequest cmReq, CMSInfo info, Schedule schedule, String snirocallbackurl) {

        // TODO: Need to get SNIRO to accept ChangeManagementSchedulingInfo
        // This is to support 1707 SNIRO interface
        CMRequestInfo reqInfo = cmReq.getRequestInfo();
        CMSchedulingInfo schInfo = cmReq.getSchedulingInfo();

        UUID uuid = UUID.randomUUID();
        reqInfo.setCallbackUrl(snirocallbackurl);
        reqInfo.setOptimizer(new String[] {"scheduling"});
        reqInfo.setTransactionId(schedule.getOptimizerTransactionId());
        reqInfo.setRequestId("CM-" + uuid.toString());
        reqInfo.setSourceId("cmso");

        String[] policyArray = new String[1];
        policyArray[0] = info.getPolicyId();
        schInfo.setPolicyId(policyArray);
        schInfo.setAdditionalDurationInSecs(info.getAdditionalDurationInSeconds());
        schInfo.setConcurrencyLimit(info.getConcurrencyLimit());
        schInfo.setNormalDurationInSecs(info.getNormalDurationInSeconds());
        schInfo.setScheduleId(schedule.getScheduleId());
        List<CMVnfDetails> list = new ArrayList<CMVnfDetails>();
        String startTime = "";
        String endTime = "";

        for (VnfDetailsMessage vnf : info.getVnfDetails()) {
            String groupId = vnf.getGroupId();
            for (String node : vnf.getNode()) {
                CMVnfDetails d = new CMVnfDetails(vnf.getGroupId());
                d.setNode(node);
                list.add(d);
            }
            if (startTime.equals("")) {
                // Only supporting 1 CW for 1710
                ChangeWindowMessage cw = vnf.getChangeWindow().get(0);
                startTime = cw.getStartTime();
                endTime = cw.getEndTime();
            }
        }
        schInfo.setStartTime(startTime);
        schInfo.setEndTime(endTime);
        schInfo.setVnfDetails(list.toArray(new CMVnfDetails[list.size()]));

    }

    private CMSInfo reconstituteMessage(Schedule schedule) {
        String request = schedule.getScheduleInfo();
        ObjectMapper om = new ObjectMapper();
        try {
            CMSInfo info = om.readValue(request, CMSInfo.class);
            return info;
        } catch (Exception e) {
            // Parsing should work here because this was a toString on the original object.
            // We may have an issue when upgrading....
            // Perhaps We create ChangeManagementSchedulingInfoV1, ...V2, etc.
            // ANd try them one after another....
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, "Unable to parse message. Format changed?");
            schedule.setOptimizerStatus("Failed to parse SNIRO request");
            schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
            schedule.setStatus(CMSStatusEnum.OptimizationFailed.toString());
            scheduleDAO.save(schedule);
        }
        return null;
    }

    @Transactional
    public void updateScheduleStatus(Schedule schedule) {
        scheduleDAO.save(schedule);

    }

    public HealthCheckComponent healthCheck() {
        Map<String, String> mdcSave = Mdc.save();
        HealthCheckComponent hcc = new HealthCheckComponent();
        hcc.setName("SNIRO Interface");
        String snirourl = env.getProperty("cmso.optimizer.url");
        String snirocallbackurl = env.getProperty("cmso.optimizer.callbackurl");
        String username = env.getProperty("mechid.user");
        String password = pm.getProperty("mechid.pass", "");
        hcc.setUrl(snirourl);
        try {
            UUID uuid = UUID.randomUUID();
            // Build a bogus request should fail policy validation
            CMRequestInfo requestInfo = new CMRequestInfo();
            CMSchedulingInfo schedulingInfo = new CMSchedulingInfo();
            CMOptimizerRequest cmReq = new CMOptimizerRequest();
            cmReq.setRequestInfo(requestInfo);
            cmReq.setSchedulingInfo(schedulingInfo);
            requestInfo.setCallbackUrl("http://callbackurl.onap.org:8080/healthcheck");
            requestInfo.setOptimizer(new String[] {"scheduling"});
            requestInfo.setTransactionId(uuid.toString());
            requestInfo.setRequestId("CM-" + uuid.toString());
            requestInfo.setSourceId("cmso");
            schedulingInfo.setAdditionalDurationInSecs(10);
            schedulingInfo.setConcurrencyLimit(10);
            schedulingInfo.setNormalDurationInSecs(10);
            schedulingInfo.setPolicyId(new String[] {"healthcheck"});
            schedulingInfo.setScheduleId(uuid.toString());
            CMVnfDetails details = new CMVnfDetails();
            details.setGroupId("");
            details.setNode("healtcheck");
            schedulingInfo.setVnfDetails(new CMVnfDetails[] {details});
            schedulingInfo.setStartTime("2017-12-11T15:23:24Z");
            schedulingInfo.setEndTime("2017-12-11T22:23:24Z");

            Client client = ClientBuilder.newClient();
            client.register(new BasicAuthenticatorFilter(username, password));
            WebTarget sniroTarget = client.target(snirourl);
            Invocation.Builder invocationBuilder = sniroTarget.request(MediaType.APPLICATION_JSON);
            debug.debug("SNIRO url / user: " + snirourl + " / " + username);
            log.info(LogMessages.OPTIMIZER_REQUEST, "Begin", "healthcheck", snirourl);
            Mdc.metricStart("healthcjeck", snirourl);
            Response response = invocationBuilder.post(Entity.json(cmReq));
            Mdc.metricEnd(response);
            metrics.info(LogMessages.OPTIMIZER_REQUEST, "End", "healthcheck", snirourl);
            log.info(LogMessages.OPTIMIZER_REQUEST, "End", "healthcheck", snirourl);
            String message = response.getStatus() + ":" + response.readEntity(String.class);
            switch (response.getStatus()) {
                case 202:
                    debug.debug("Successful SNIRO healthcheck");
                    hcc.setHealthy(true);
                    break;
                case 400:
                    // Expecting policy not found.
                    if (message.contains("Cannot fetch policy")) {
                        debug.debug("Successful SNIRO healthcheck");
                        hcc.setHealthy(true);
                        hcc.setStatus("OK");
                    } else {
                        hcc.setStatus(message);
                    }
                    break;
                default:
                    hcc.setStatus(message);
                    break;
            }
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e.toString());
            hcc.setStatus(e.toString());
        } finally {
            Mdc.restore(mdcSave);
        }
        return hcc;

    }
}
