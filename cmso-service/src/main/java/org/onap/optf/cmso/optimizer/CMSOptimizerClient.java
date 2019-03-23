/*
 * Copyright © 2017-2019 AT&T Intellectual Property.
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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.onap.observations.Mdc;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.BasicAuthenticatorFilter;
import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.filters.CMSOClientFilters;
import org.onap.optf.cmso.model.DomainData;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.optimizer.model.OptimizerRequest;
import org.onap.optf.cmso.optimizer.model.OptimizerResponse;
import org.onap.optf.cmso.service.rs.models.HealthCheckComponent;
import org.onap.optf.cmso.service.rs.models.v2.NameValue;
import org.onap.optf.cmso.service.rs.models.v2.SchedulingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CMSOptimizerClient {
  private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

  @Autowired ScheduleDAO scheduleDAO;

  @Autowired Environment env;

  @Autowired PropertiesManagement pm;

  @Autowired CMSOptimizerHandler optimizerHandler;

  public void scheduleOptimization(UUID uuid) {
    Map<String, String> mdcSave = Mdc.save();
    try {
      // Ensure that only one cmso is requsting this call to optimizer
      Schedule schedule = scheduleDAO.lockOne(uuid);
      if (schedule.getStatus().equals(CMSStatusEnum.PendingSchedule.toString())) {
        scheduleNewOptimization(schedule);
      }
      if (schedule.getStatus().equals(CMSStatusEnum.OptimizationInProgress.toString())) {
        pollOptimizer(schedule);
      }
      return;
    } catch (Exception e) {
      Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
    } finally {
      Mdc.restore(mdcSave);
    }
  }

  public void scheduleNewOptimization(Schedule schedule) {
    try {
      Integer maxAttempts = env.getProperty("cmso.optimizer.maxAttempts", Integer.class, 20);
      //
      // Only 'successfully' process one schedule per invocation
      // If a schedule attemp fails and it could be because of the data in the
      // message, try the next one. We don't want bad data to
      //
      if (schedule.getOptimizerAttemptsToSchedule() >= maxAttempts) {
        schedule.setStatus(CMSStatusEnum.OptimizationFailed.toString());
        schedule.setOptimizerMessage("Maximum number of attempts exceeded " + maxAttempts);
        updateScheduleStatus(schedule);
        return;
      }
      OptimizerRequest cmReq = null;
      try {
        cmReq = buildRequestMessage(schedule);
        if (cmReq == null) {
          return;
        }
      } catch (Exception e) {
        Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        schedule.setStatus(CMSStatusEnum.OptimizationFailed.toString());
        schedule.setOptimizerMessage("Unexpected exception: " + e.getMessage());
        updateScheduleStatus(schedule);
        return;
      }
      initiateOptimization(schedule, cmReq);
    } catch (Exception e) {
      Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
    }
  }

  private void initiateOptimization(Schedule schedule, OptimizerRequest cmReq) {
    try {
      String optimizerurl = env.getProperty("cmso.optimizer.request.url");
      String username = env.getProperty("mechid.user");
      String password = pm.getProperty("mechid.pass", "");
      // This service will call optimizer for each PendingSchedule
      // If the request is successfully scheduled in optimizer, status will be
      // updated to OptimizationInProgress.
      Client client = ClientBuilder.newClient();
      client.register(new BasicAuthenticatorFilter(username, password));
      client.register(new CMSOClientFilters());
      WebTarget optimizerTarget = client.target(optimizerurl);
      Invocation.Builder invocationBuilder = optimizerTarget.request(MediaType.APPLICATION_JSON);
      try {
        //
        // First, push OptimizationInProgress to the DB (flush()) assuming a 202 status,
        // in case the optimizer callback is received prior to the
        // commit of this transaction.
        // optimizer Callback will throw an error if it receives a response in the incorrect
        // state.
        //
        schedule.setOptimizerTransactionId(cmReq.getRequestId());
        schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
        schedule.setStatus(CMSStatusEnum.OptimizationInProgress.toString());
        updateScheduleStatus(schedule);
        debug.debug("optimizer url / user: " + optimizerurl + " / " + username);
        debug.debug("optimizer Request: " + new ObjectMapper().writeValueAsString(cmReq));
        Observation.report(
            LogMessages.OPTIMIZER_REQUEST, "Begin", schedule.getScheduleId(), optimizerurl);
        Response response = invocationBuilder.post(Entity.json(cmReq));
        Observation.report(
            LogMessages.OPTIMIZER_REQUEST, "End", schedule.getScheduleId(), optimizerurl);
        switch (response.getStatus()) {
          case 202:
            debug.debug("Successfully scheduled optimization: " + schedule.getScheduleId());
            // Scheduled with optimizer
            break;
          case 400: // Bad request
            {
              schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
              schedule.setOptimizerStatus("HTTP Status: " + response.getStatus());
              String message = response.readEntity(String.class);
              schedule.setOptimizerMessage(message);
              schedule.setStatus(CMSStatusEnum.ScheduleFailed.toString());
              // Need to understand the cause of this error. May be teh same as optimizer
              // down.
              int tries = schedule.getOptimizerAttemptsToSchedule();
              tries++;
              schedule.setOptimizerAttemptsToSchedule(tries);
              updateScheduleStatus(schedule);
              Observation.report(LogMessages.OPTIMIZER_EXCEPTION, message);
            }
            break;
          case 500:
          default:
            {
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
              Observation.report(LogMessages.OPTIMIZER_EXCEPTION, message);
              // Wait until next cycle and try again.
            }
        }
      } catch (ResponseProcessingException e) {
        schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
        schedule.setOptimizerStatus("Failed to parse optimizer response");
        schedule.setStatus(CMSStatusEnum.ScheduleFailed.toString());
        // Need to understand the cause of this error. May be teh same as optimizer down.
        int tries = schedule.getOptimizerAttemptsToSchedule();
        tries++;
        schedule.setOptimizerAttemptsToSchedule(tries);
        updateScheduleStatus(schedule);
        // Getting invalid response from optimizer.
        // May be data related.
        Observation.report(LogMessages.OPTIMIZER_EXCEPTION, e, e.getMessage());
      } catch (ProcessingException e) {
        // Don't track number of retries on IO error (optimizer is down)
        schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
        schedule.setStatus(CMSStatusEnum.PendingSchedule.toString());
        updateScheduleStatus(schedule);
        /// Cannot connect to optimizer
        Observation.report(LogMessages.OPTIMIZER_EXCEPTION, e, e.getMessage());
        // Wait until next cycle
      }
    } catch (Exception e) {
      Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
    }

  }

  public void pollOptimizer(Schedule schedule) {
    try {
      String optimizerurl = env.getProperty("cmso.optimizer.status.url");
      String username = env.getProperty("mechid.user");
      String password = pm.getProperty("mechid.pass", "");
      Long timeout = env.getProperty("cmso.optimizer.request.timeout.secs", Long.class);
      if (timeout == null) {
        timeout = 3600l;
      }
      if (!optimizerurl.endsWith("/")) {
        optimizerurl += "/";
      }
      Long now = System.currentTimeMillis();
      if (now > schedule.getOptimizerDateTimeMillis() + (timeout*1000)) {
        schedule.setStatus(CMSStatusEnum.ScheduleFailed.toString());
        updateScheduleStatus(schedule);
        return;
      }

      optimizerurl += schedule.getOptimizerTransactionId();
      // This service will call optimizer for each PendingSchedule
      // If the request is successfully scheduled in optimizer, status will be
      // updated to OptimizationInProgress.
      Client client = ClientBuilder.newClient();
      client.register(new BasicAuthenticatorFilter(username, password));
      client.register(new CMSOClientFilters());
      WebTarget optimizerTarget = client.target(optimizerurl);
      Invocation.Builder invocationBuilder = optimizerTarget.request(MediaType.APPLICATION_JSON);
      debug.debug("optimizer url / user: " + optimizerurl + " / " + username);
      Observation.report(
          LogMessages.OPTIMIZER_REQUEST, "Begin", schedule.getScheduleId(), optimizerurl);
      Response response = invocationBuilder.get();
      Observation.report(
          LogMessages.OPTIMIZER_REQUEST, "End", schedule.getScheduleId(), optimizerurl);
      switch (response.getStatus()) {
        case 200:

          String optimizerResponseString = response.readEntity(String.class);
          ObjectMapper om = new ObjectMapper();
          OptimizerResponse optimizerResponse = om.readValue(optimizerResponseString,
                  OptimizerResponse.class);
          debug.debug("Successfully retrieved optimization: " + schedule.getScheduleId());
          optimizerHandler.handleOptimizerResponse(optimizerResponse, schedule);
          break;
        default: // Bad request
          {
            schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
            schedule.setOptimizerStatus("HTTP Status: " + response.getStatus());
            String message = response.readEntity(String.class);
            schedule.setOptimizerMessage(message);
            schedule.setStatus(CMSStatusEnum.ScheduleFailed.toString());
            updateScheduleStatus(schedule);
            Observation.report(LogMessages.OPTIMIZER_EXCEPTION, message);
          }
      }
    } catch (Exception e) {
      Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
      schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
      schedule.setOptimizerMessage(e.getMessage());
      schedule.setStatus(CMSStatusEnum.ScheduleFailed.toString());
      updateScheduleStatus(schedule);
    }
  }

  private OptimizerRequest buildRequestMessage(Schedule schedule) {
    String request = schedule.getScheduleInfo();
    ObjectMapper om = new ObjectMapper();
    try {
      SchedulingData info = om.readValue(request, SchedulingData.class);
      OptimizerRequest orequest = new OptimizerRequest();
      orequest.setChangeWindows(info.getChangeWindows());
      orequest.setPolicies(info.getPolicies());
      orequest.setRequestId(schedule.getScheduleId());
      orequest.setCommonData(marshallCommonData(schedule));
      orequest.setElements(info.getElements());
      orequest.setAdditionalDuration(info.getAdditionalDurationInSeconds());
      orequest.setNormalDuration(info.getNormalDurationInSeconds());
      orequest.setConcurrencyLimit(info.getConcurrencyLimit());
      return orequest;
    } catch (Exception e) {
      // Parsing should work here because this was a toString on the original object.
      // We may have an issue when upgrading....
      // Perhaps We create ChangeManagementSchedulingInfoV1, ...V2, etc.
      // ANd try them one after another....
      Observation.report(
          LogMessages.UNEXPECTED_EXCEPTION, e, "Unable to parse message. Format changed?");
      schedule.setOptimizerStatus("Failed to parse optimizer request");
      schedule.setOptimizerDateTimeMillis(System.currentTimeMillis());
      schedule.setStatus(CMSStatusEnum.OptimizationFailed.toString());
      scheduleDAO.save(schedule);
    }
    return null;
  }

  private List<NameValue> marshallCommonData(Schedule schedule) {
    List<NameValue> nvList = new ArrayList<>();
    List<DomainData> ddList = schedule.getDomainData();
    for (DomainData dd : ddList) {
      NameValue nv = new NameValue();
      nv.setName(dd.getName());
      // TODO: handle other than String values
      nv.setValue(dd.getValue());
      nvList.add(nv);
    }
    return nvList;
  }

  @Transactional
  public void updateScheduleStatus(Schedule schedule) {
    scheduleDAO.save(schedule);
  }

  public HealthCheckComponent healthCheck() {
    Map<String, String> mdcSave = Mdc.save();
    HealthCheckComponent hcc = new HealthCheckComponent();
    hcc.setName("OPtimizer Interface");
    String optimizerurl = env.getProperty("cmso.optimizer.health.url");
    String username = env.getProperty("mechid.user");
    String password = pm.getProperty("mechid.pass", "");
    hcc.setUrl(optimizerurl);
    try {
      Client client = ClientBuilder.newClient();
      client.register(new BasicAuthenticatorFilter(username, password));
      client.register(new CMSOClientFilters());

      WebTarget optimizerTarget = client.target(optimizerurl);
      Invocation.Builder invocationBuilder = optimizerTarget.request(MediaType.APPLICATION_JSON);
      debug.debug("Optimizer url / user: " + optimizerurl + " / " + username);
      Response response = invocationBuilder.get();
      Observation.report(LogMessages.OPTIMIZER_REQUEST, "End", "healthcheck", optimizerurl);
      String message = response.getStatus() + ":" + response.readEntity(String.class);
      switch (response.getStatus()) {
        case 200:
          debug.debug("Successful optimizer healthcheck");
          hcc.setHealthy(true);
          break;
        case 400:
        default:
          hcc.setStatus(message);
          break;
      }
    } catch (Exception e) {
      Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e.toString());
      hcc.setStatus(e.toString());
    } finally {
      Mdc.restore(mdcSave);
    }
    return hcc;
  }
}
