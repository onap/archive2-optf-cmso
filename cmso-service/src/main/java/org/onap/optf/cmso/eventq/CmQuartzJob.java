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

package org.onap.optf.cmso.eventq;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.onap.observations.Mdc;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDao;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * This quartz job runs periodically to query the ChangeManagementSChedule table
 * to create CmJobs to queue in quartx.
 *
 */
@Component
@DisallowConcurrentExecution
public class CmQuartzJob extends QuartzJobBean {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    CmsoQueueJob qqJob;

    // This is not called directly. Using @Autowired to ensure that startup
    // Runs before the fist invocation of executeInternal
    @Autowired
    CmQuartzJobStartup startup;

    @Autowired
    ChangeManagementScheduleDao cmScheduleDao;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Mdc.quartzJobBegin(context);
        debug.debug(LogMessages.CM_QUARTZ_JOB, "Entered");

        // This logic does not make sense in k8s since we cannot count on IP address
        // as the dispatch.
        // Need to understand how to requeue when instances go down while dispatching
        // jobs
        // between "Queued for Dispatch" and "Triggered"
        // startup.startup(); // Runs once

        // Turns out that this is not necessary, Quartz does what makes sense after all
        // if (isStale(context))
        // return;

        try {

            boolean moreToSchedule = true;
            while (moreToSchedule) {
                try {
                    moreToSchedule = qqJob.queueImminentJobs();
                } catch (Exception e) {
                    debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
                    moreToSchedule = false;
                }
            }

        } catch (Exception e) {
            debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        debug.debug(LogMessages.CM_QUARTZ_JOB, "Exited");

    }

}
