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

package org.onap.optf.cmso.dispatcher;

import java.util.Map;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.observations.Mdc;
import org.onap.optf.cmso.common.BasicAuthenticatorFilter;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.eventq.DispatchedEventList;
import org.onap.optf.cmso.filters.CmsoClientFilters;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.ticketmgt.TmClient;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * This is the Quartz Job that is run to send the workflow to VID for execution
 * 
 *
 */
@Component
@DisallowConcurrentExecution
public class CmJob implements Job {
    private static EELFLogger log = EELFManager.getInstance().getLogger(CmJob.class);
    private static EELFLogger metrics = EELFManager.getInstance().getMetricsLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    CMSOClient vidClient;

    @Autowired
    ChangeManagementScheduleDAO cmScheduleDAO;

    @Autowired
    ChangeManagementGroupDAO cmGroupDAO;

    @Autowired
    ScheduleDAO scheduleDAO;

    @Autowired
    TmClient tmClient;

    @Autowired
    PropertiesManagement pm;

    @Autowired
    Environment env;

    @Autowired
    DispatchedEventList dispatchedEventList;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Mdc.quartzJobBegin(context);
        debug.debug(LogMessages.CM_JOB, "Entered");
        String id = context.getJobDetail().getJobDataMap().getString("key");
        try {
            // Hand this off to a transactional service
            loopback(id);
        } catch (Exception e) {
            log.warn("Unexpected exception", e);
        } finally {
            dispatchedEventList.remove(UUID.fromString(id));
        }
        debug.debug(LogMessages.CM_JOB, "Exited");
    }

    public void loopback(String id) {
        Map<String, String> mdcSave = Mdc.save();
        try {
            String url = env.getProperty("cmso.dispatch.url", "http://localhost:8089");
            String path = env.getProperty("cmso.dispatch.schedule.path", "/cmso/dispatch/schedule/");
            url = url + path + id;
            String user = env.getProperty("mechid.user", "");
            String pass = pm.getProperty("mechid.pass", "");
            Client client = ClientBuilder.newClient();
            client.register(new BasicAuthenticatorFilter(user, pass));
			client.register(new CmsoClientFilters());
            WebTarget target = client.target(url);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = null;
            try {
                response = invocationBuilder.get();
                metrics.info(LogMessages.CM_JOB, id.toString());
                switch (response.getStatus()) {
                    case 200:
                        log.info("Returned from dispatch call");
                        break;
                    case 400: // Bad request
                    default: {

                        throw new SchedulerException(
                                "Invalid return from dispach service: " + url + " : " + response.toString());
                    }
                }
            } catch (Exception e) {
                debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
                errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            }
        } catch (Exception e) {
            debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        } finally {
            Mdc.restore(mdcSave);
        }

    }

}
