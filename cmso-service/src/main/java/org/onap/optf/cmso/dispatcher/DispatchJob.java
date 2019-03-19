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

import java.net.InetAddress;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.model.ChangeManagementGroup;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.ticketmgt.TmClient;
import org.onap.optf.cmso.ticketmgt.bean.TmApprovalStatusEnum;
import org.onap.optf.cmso.ticketmgt.bean.TmChangeInfo;
import org.onap.optf.cmso.ticketmgt.bean.TmStatusEnum;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * This is the service used to dispatch a job COuld not get QuartzJobBean to
 * be @Transactional
 *
 */
@Component
public class DispatchJob {
    private static EELFLogger log = EELFManager.getInstance().getLogger(DispatchJob.class);
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
    Environment env;

    public void execute(UUID id) throws JobExecutionException {
        debug.debug(LogMessages.CM_JOB, "Entered");
        try {
            // No other instance can read this cmso until we are done.
            ChangeManagementSchedule cmSchedule = cmScheduleDAO.lockOne(id);
            cmSchedule.setDispatcherInstance(InetAddress.getLocalHost().getHostAddress());
            ChangeManagementGroup group = cmGroupDAO.findById(cmSchedule.getChangeManagementGroupUuid()).orElse(null);
            if (group != null) {
                Schedule schedule = scheduleDAO.findById(group.getSchedulesUuid()).orElse(null);
                if (schedule != null) {
                    schedule.setStatus(CMSStatusEnum.NotificationsInitiated.toString());
                    if (safeToDispatch(cmSchedule, schedule))
                        vidClient.dispatch(cmSchedule, schedule);
                }
            }

        } catch (Exception e) {
            log.warn("Unexpected exception", e);
        }
        debug.debug(LogMessages.CM_JOB, "Exited");

    }

    private boolean safeToDispatch(ChangeManagementSchedule cmSchedule, Schedule schedule) {

        Integer leadTime = env.getProperty("cmso.cm.dispatch.lead.time.ms", Integer.class, 1000);
        Boolean scheduleImmediateEnabled = env.getProperty("cmso.cm.dispatch.immediate.enabled", Boolean.class, false);

        // *******************************************************************
        // SHould we read with a lock to avoid race condition?
        // Not sure there is any real value to that at the moment.
        //

        // *******************************************************************
        // Validate that the state is accurate.
        // Another instance may have dispatched
        Long startTime = cmSchedule.getStartTimeMillis();
        if (!cmSchedule.getStatus().equals(CMSStatusEnum.Scheduled.toString())
                && !cmSchedule.getStatus().equals(CMSStatusEnum.ScheduledImmediate.toString())) {
            log.info("Attempt to dispatch an event that is in the incorrect state scheduleId={0}, vnf={1}, status={2}",
                    schedule.getScheduleId(), cmSchedule.getVnfName(), cmSchedule.getStatus());
            return false;
        }

        // *******************************************************************
        //
        // Validate ticket is still active with tm
        //
        // TODO
        if (cmSchedule.getTmChangeId() != null && !cmSchedule.getTmChangeId().equals("")) {
            if (!isChangeApproved(schedule, cmSchedule, scheduleImmediateEnabled)) {
                return false;
            }
        }

        // *******************************************************************
        //
        // If this is schedule immediate. Dispatch it!
        //
        if (cmSchedule.getFinishTimeMillis() == null) {
            if (scheduleImmediateEnabled) {
                debug.info("Dispatching an immediate request" + cmSchedule.getVnfName());
                return true;
            } else {
                // THis should not happen. If schedule immediate is not enables, it will fail
                // ticket approval check.
                // ... If we see this, it is a bug...
                String message = "Attempt to schedule immediate when immmediate scheduling is disabled: "
                        + cmSchedule.getVnfName();
                log.info(message);
                cmSchedule.setStatus(CMSStatusEnum.SchedulingFailed.toString());
                cmSchedule.setStatusMessage(message);
                updateScheduleStatus(cmSchedule, schedule);
                return false;
            }
        }

        // *******************************************************************
        //
        // Validate that event is not past due. Time sensitive. Do not dispatch
        // Do not account for lead time. This should be inconsequential, maybe????
        //
        long now = System.currentTimeMillis();
        long startMillis = startTime;
        if (now > startMillis) {
            String message = EELFResourceManager.format(LogMessages.SCHEDULE_PAST_DUE, schedule.getScheduleId(),
                    cmSchedule.getVnfName(), new Date(now).toString(), new Date(startMillis).toString());
            log.info(message);

            cmSchedule.setStatus(CMSStatusEnum.PastDue.toString());
            cmSchedule.setStatusMessage(message);
            updateScheduleStatus(cmSchedule, schedule);
            return false;
        }

        // *******************************************************************
        // (Sleep until actual dispatch time...)
        // leadTime allows for preparing call to VID to the start of workflow.
        long sleep = (startMillis - leadTime) - System.currentTimeMillis();
        if (sleep > 0l) {
            try {
                Thread.sleep(sleep);
            } catch (Exception e) {
                debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            }
        }
        return true;
    }

    private boolean isChangeApproved(Schedule schedule, ChangeManagementSchedule cmSchedule,
            Boolean scheduleImmediateEnabled) {
        String message = "";
        // ChangeManagementGroup group =
        // cmGroupDAO.findById(cmSchedule.getChangeManagementGroupsId()).orElse(null);
        // Long startDate = group.getStartTimeMillis();
        Set<String> vnfNames = new HashSet<String>();
        vnfNames.add(cmSchedule.getVnfName());
        TmChangeInfo info = tmClient.getChangeTicket(cmSchedule.getTmChangeId());
        String otherStatus = env.getProperty("vtm.status", "");
        String otherApprovalStatus = env.getProperty("vtm.approvalStatus", "");
        String statusConfig = env.getProperty("vtm.approvalStatus",
                TmApprovalStatusEnum.Approved.toString() + "|" + TmStatusEnum.Scheduled.toString());
        String[] statusList = statusConfig.split(",");
        if (info != null) {
            String approvalStatus = info.getApprovalStatus();
            String status = info.getStatus();
            debug.debug("Retrieved changeid=" + info.getChangeId() + " approvlStatus=" + approvalStatus + " status="
                    + status);
            String actualStatus = approvalStatus + "|" + status;
            for (String okStatus : statusList) {
                if (actualStatus.equals(okStatus)) {
                    return true;
                }
            }
            // Look for proper state for immediate VNFs
            debug.debug("Retrieved changeid=" + info.getChangeId() + " otherApprovlStatus=" + otherApprovalStatus
                    + " status=" + otherStatus);
            if (cmSchedule.getStartTime() == null && scheduleImmediateEnabled) {
                debug.debug("Ignoring status on immediate schedule: " + cmSchedule.getTmChangeId());
                return true;
            }
            message = EELFResourceManager.format(LogMessages.CM_TICKET_NOT_APPROVED, schedule.getScheduleId(),
                    cmSchedule.getVnfName(), cmSchedule.getTmChangeId(), status, approvalStatus);
            log.warn(message);
        } else {
            message = EELFResourceManager.format(LogMessages.UNABLE_TO_LOCATE_CHANGE_RECORD, schedule.getScheduleId(),
                    cmSchedule.getVnfName(), cmSchedule.getTmChangeId());
            errors.error(message);
        }
        cmSchedule.setStatus(CMSStatusEnum.SchedulingFailed.toString());
        cmSchedule.setStatusMessage(message);
        updateScheduleStatus(cmSchedule, schedule);
        return false;
    }

    @Transactional
    public void updateScheduleStatus(ChangeManagementSchedule cmSchedule, Schedule schedule) {
        cmScheduleDAO.save(cmSchedule);
        scheduleDAO.save(schedule);

    }

}
