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

import java.util.Date;
import java.util.List;
import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.dispatcher.CmJob;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * This job will look for ChangeManagementSchedule approved jobs that are due
 * within the next n intervals of this job and schedule them in as a Quartz
 * CmJob to be dispatched. These ChangeManagementSchedule rows will be
 * status=Queued The CmJob will confirm that the job is OK to be dispatched and
 * set status=.... The next N intervals should be enough to ensure sluggish
 * performance of this process does not miss events but does not exhaust
 * memory....
 */
@Component
public class CMSQueueJob {
    private static EELFLogger log = EELFManager.getInstance().getLogger(CMSQueueJob.class);
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    ChangeManagementScheduleDAO cmScheduleDAO;

    @Autowired
    SchedulerFactoryBean qsScheduler;

    @Autowired
    Environment env;

    @Autowired
    DispatchedEventList dispatchedEventList;

    public boolean queueImminentJobs() {
        Integer interval = env.getProperty("cmso.cm.polling.job.interval.ms", Integer.class, 10000);
        Integer lookahead = env.getProperty("cmso.cm.polling.job.lookahead.intervals", Integer.class, 5);
        long now = System.currentTimeMillis();
        Long endTime = now + (interval * lookahead);
        List<ChangeManagementSchedule> schedules =
                cmScheduleDAO.findByStatusAndEndTime(CMSStatusEnum.Scheduled.toString(), endTime);
        if (schedules.size() == 0)
            return false;
        for (ChangeManagementSchedule schedule : schedules) {
            try {
                if (!dispatchedEventList.isAlreadyDispatched(schedule.getUuid())) {
                    scheduleCmJob(schedule);
                    dispatchedEventList.addToDispathcedEventList(schedule.getUuid());
                }
            } catch (org.quartz.SchedulerException e) {
                debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
                // WIll keep trying until it goes Past due
                schedule.setStatus(CMSStatusEnum.Scheduled.toString());
                schedule.setDispatcherInstance("");
                updateScheduleStatus(schedule);
            } catch (Exception e) {
                debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
                schedule.setStatus(CMSStatusEnum.Scheduled.toString());
                schedule.setDispatcherInstance("");
                updateScheduleStatus(schedule);
            }
        }
        return false;
    }

    public void scheduleCmJob(ChangeManagementSchedule schedule) throws org.quartz.SchedulerException {
        //
        Integer dispatherLeadTime = env.getProperty("cmso.cm.dispatcher.lead.time.ms", Integer.class, 5000);
        long dispatchTime = 0;
        Long startTime = schedule.getStartTimeMillis();

        /// If startTIme is null, it is an immediate start
        if (startTime != null)
            dispatchTime = startTime - dispatherLeadTime;

        JobDetail jobDetail = JobBuilder.newJob(CmJob.class).build();
        jobDetail.getJobDataMap().put("key", schedule.getUuid().toString());

        TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger().forJob(jobDetail);

        long now = System.currentTimeMillis();
        if (now > dispatchTime)
            tb.startNow();
        else
            tb.startAt(new Date(dispatchTime));
        Trigger trigger = tb.build();
        qsScheduler.getScheduler().scheduleJob(jobDetail, trigger);

    }

    @Transactional
    public void updateScheduleStatus(ChangeManagementSchedule cmSchedule) {
        cmScheduleDAO.save(cmSchedule);

    }

}
