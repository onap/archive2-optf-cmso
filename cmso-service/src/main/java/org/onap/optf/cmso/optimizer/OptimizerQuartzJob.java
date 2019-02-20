/*
 * Copyright � 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright � 2018 IBM.
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

import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.observations.Mdc;
import org.onap.optf.cmso.common.BasicAuthenticatorFilter;
import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.DomainsEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.filters.CMSOClientFilters;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

@Component
@DisallowConcurrentExecution
public class OptimizerQuartzJob extends QuartzJobBean {
    private static EELFLogger log = EELFManager.getInstance().getLogger(OptimizerQuartzJob.class);
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();
    private static EELFLogger metrics = EELFManager.getInstance().getMetricsLogger();

    @Autowired
    ScheduleDAO scheduleDAO;

    @Autowired
    PropertiesManagement pm;

    @Autowired
    Environment env;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Mdc.quartzJobBegin(context);
        debug.debug(LogMessages.OPTIMIZER_QUARTZ_JOB, "Entered");

        // Turns out that this is not necessary. Quartz behaves in a way that makes
        // sense.
        // if (isStale(context))
        // return;

        try {
            // This job will look at the schedules waiting to go to SNIRO
            // (PendingSchedule),
            // schedule the request and update the status to PendingSchedule
            // and update the state to OptimizationInProgress
            List<Schedule> schedules = scheduleDAO.findByDomainStatus(DomainsEnum.ChangeManagement.toString(),
                    CMSStatusEnum.PendingSchedule.toString());
            for (Schedule s : schedules) {
                scheduleOptimization(s);
            }

        } catch (Exception e) {
            debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        debug.debug(LogMessages.OPTIMIZER_QUARTZ_JOB, "Exited");

    }

    public void scheduleOptimization(Schedule s) {
        Integer id = s.getId();
        Map<String, String> mdcSave = Mdc.save();
        try {
            String url = env.getProperty("cmso.dispatch.url", "http://localhost:8089");
            String path = env.getProperty("cmso.dispatch.optimizer .path", "/cmso/dispatch/optimizer/");
            url = url + path + id;
            String user = env.getProperty("mechid.user", "");
            String pass = pm.getProperty("mechid.pass", "");
            Client client = ClientBuilder.newClient();
            client.register(new BasicAuthenticatorFilter(user, pass));
            client.register(new CMSOClientFilters());
            WebTarget target = client.target(url);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = null;
            response = invocationBuilder.get();
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
        } finally {
            Mdc.restore(mdcSave);
        }

    }

    /**
     * According to the documentation I read, Quartz would queue a job without
     * waiting for the completion of the job with @DisallowConcurrentExecution to
     * complete so that there would be a backlog of triggers to process
     * 
     * This was designed to spin though these stale triggers. When this didn't work,
     * I discovered that the behavior is that Quartz will wait for the appropriate
     * interval after @DisallowConcurrentExecution jobs complete.
     * 
     * I tested by adding a sleep for an interval > the trigger interval
     * 
     * QUartz appears to do what makes sense. Leaving this here in case issues
     * arise...
     * 
     */
    @SuppressWarnings("unused")
    private boolean isStale(JobExecutionContext context) {
        // DO not process stale requests.
        long now = System.currentTimeMillis();
        long next = context.getNextFireTime().getTime();
        long sch = context.getScheduledFireTime().getTime();
        log.info("now=" + now);
        log.info("nxt=" + next);
        log.info("sch=" + sch);
        if (now > sch) {
            log.info("Skipping stale SNIRO job");
            // return true;
        }
        return false;
    }

}
