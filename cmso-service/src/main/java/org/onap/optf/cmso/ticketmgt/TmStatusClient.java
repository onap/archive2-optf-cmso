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

package org.onap.optf.cmso.ticketmgt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;
import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.exceptions.CMSException;
import org.onap.optf.cmso.model.ChangeManagementGroup;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.eelf.i18n.EELFResourceManager;

@Component
public class TmStatusClient {
    private static EELFLogger log = EELFManager.getInstance().getLogger(TmStatusClient.class);
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    public enum GroupAuditStatus {
        InProgress, Completed, CompletedWithErrors
    }

    public enum ClosureCode {
        // Map to TM closure codes
        Successful("Successful As Scheduled"), Unsuccessful("Unsuccessful");

        private final String closureCode;

        private ClosureCode(String code) {
            closureCode = code;
        }

        @Override
        public String toString() {
            return closureCode;
        }
    }

    @Autowired
    Environment env;

    @Autowired
    ScheduleDAO scheduleDAO;

    @Autowired
    ChangeManagementScheduleDAO cmScheduleDAO;

    @Autowired
    ChangeManagementGroupDAO cmGroupDAO;

    @Autowired
    TmClient tmClient;

    @Transactional
    public void checkStatus(String id) {
        debug.debug("Entered checkStatus id=" + id);
        try {
            // Multiple cmso instance support - re-get the record with a Schedule lock
        	UUID uuid = UUID.fromString(id);
            Schedule s = scheduleDAO.lockOne(uuid);
            if (!s.getStatus().equals(CMSStatusEnum.NotificationsInitiated.toString())) {
                debug.debug(s.getScheduleId() + " is no longer in " + CMSStatusEnum.NotificationsInitiated.toString()
                        + " : it is " + s.getStatus());
                // Attempt at avoiding race condition in a load balance env. ?
                return;
            }
            Map<GroupAuditStatus, List<ChangeManagementGroup>> groupStatus =
                    new HashMap<GroupAuditStatus, List<ChangeManagementGroup>>();
            List<ChangeManagementGroup> groups = cmGroupDAO.findBySchedulesID(uuid);

            // Close tickets for completed VNFs
            for (ChangeManagementGroup group : groups) {
                processGroup(s, group);
            }

            // Check overall status of schedule.
            //
            for (ChangeManagementGroup group : groups) {
                GroupAuditStatus status = auditGroupStatus(s, group);
                List<ChangeManagementGroup> list = groupStatus.get(status);
                if (list == null) {
                    list = new ArrayList<ChangeManagementGroup>();
                    groupStatus.put(status, list);
                }
                list.add(group);
            }
            // In progress
            if (groupStatus.containsKey(GroupAuditStatus.InProgress))
                return;
            //
            if (groupStatus.containsKey(GroupAuditStatus.CompletedWithErrors)) {
                s.setStatus(CMSStatusEnum.CompletedWithError.toString());
            }
            if (groupStatus.containsKey(GroupAuditStatus.Completed)) {
                s.setStatus(CMSStatusEnum.Completed.toString());
            }
            scheduleDAO.save(s);
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String msg = EELFResourceManager.format(LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
            errors.warn(msg, e);
        }
    }

    private void processGroup(Schedule s, ChangeManagementGroup group) throws CMSException {
        debug.debug("{Processing status of " + s.getScheduleId() + " group=" + group.getGroupId());
        // Get status of all VNFs within a ticket within the group (Tickets will not
        // span groups)
        Map<String, List<ChangeManagementSchedule>> failed = new HashMap<String, List<ChangeManagementSchedule>>();
        Map<String, List<ChangeManagementSchedule>> inProgress = new HashMap<String, List<ChangeManagementSchedule>>();
        Map<String, List<ChangeManagementSchedule>> completed = new HashMap<String, List<ChangeManagementSchedule>>();
        Map<String, List<ChangeManagementSchedule>> tmClosed = new HashMap<String, List<ChangeManagementSchedule>>();
        List<ChangeManagementSchedule> cmSchedules = cmScheduleDAO.findByChangeManagementGroupId(group.getUuid());
        for (ChangeManagementSchedule cmSchedule : cmSchedules) {
            String status = cmSchedule.getStatus();
            String changeId = cmSchedule.getTmChangeId();
            String tmStatus = cmSchedule.getTmStatus();
            CMSStatusEnum cmsStatus = CMSStatusEnum.Completed.fromString(status);
            switch (cmsStatus) {
                case Scheduled:
                case Triggered:
                    addTo(inProgress, cmSchedule);
                    break;
                case Failed:
                case PastDue:
                case Error:
                case Cancelled:
                case SchedulingFailed:
                case Deleted:
                    addTo(failed, cmSchedule);
                    break;
                case Completed:
                    addTo(completed, cmSchedule);
                    break;
                case ApprovalRejected:
                case PendingApproval:
                case PendingSchedule:
                case ScheduledImmediate:
                default:
                    errors.applicationEvent(
                            "Unexpected Change Management schedule event status {0} encountered after notification : scheduleId={1} vnfName={2}",
                            status, s.getScheduleId(), cmSchedule.getVnfName());
                    break;
            }
            if (tmStatus != null && tmStatus.equals("Closed"))
                addTo(tmClosed, cmSchedule);
        }
        debug.debug("{Status of " + s.getScheduleId() + " Group " + group.getGroupId() + "\ncompleted="
                + completed.keySet().toString() + "\ninProgress=" + inProgress.keySet().toString() + "\nfailed="
                + failed.keySet().toString() + "\ntmCLosed=" + tmClosed.keySet().toString());

        // Remove all tickets from completed where there are still 'Triggered' VNFs
        for (String changeId : inProgress.keySet())
            completed.remove(changeId);
        // Remove all tickets from completed where the ticket is already closed.
        for (String changeId : tmClosed.keySet())
            completed.remove(changeId);

        // Do not know what to do with failed
        for (String changeId : failed.keySet()) {
            completed.remove(changeId);
            closeTheTicket(s, group, changeId, failed.get(changeId), ClosureCode.Unsuccessful,
                    "Change management request failed for one or more VNFs");
        }

        // Remaining completed tickets should be closed
        debug.debug("{Final status of " + s.getScheduleId() + " Group " + group.getGroupId() + "\ncompleted="
                + completed.keySet().toString() + "\ninProgress=" + inProgress.keySet().toString() + "\nfailed="
                + failed.keySet().toString() + "\ntmCLosed=" + tmClosed.keySet().toString());
        for (String changeId : completed.keySet()) {
            closeTheTicket(s, group, changeId, completed.get(changeId), ClosureCode.Successful,
                    ClosureCode.Successful.toString());
        }

    }

    private GroupAuditStatus auditGroupStatus(Schedule s, ChangeManagementGroup group) {
        // Determine group status and synchronize VNF status with Ticket status.
        Map<String, List<ChangeManagementSchedule>> failed = new HashMap<String, List<ChangeManagementSchedule>>();
        Map<String, List<ChangeManagementSchedule>> inProgress = new HashMap<String, List<ChangeManagementSchedule>>();
        Map<String, List<ChangeManagementSchedule>> completed = new HashMap<String, List<ChangeManagementSchedule>>();
        Map<String, List<ChangeManagementSchedule>> tmClosed = new HashMap<String, List<ChangeManagementSchedule>>();
        Set<String> vnfNames = new HashSet<String>();
        Set<String> allNames = new HashSet<String>();
        Set<String> failedNames = new HashSet<String>();
        Set<String> completedNames = new HashSet<String>();
        Long startDate = group.getStartTimeMillis();
        List<ChangeManagementSchedule> cmSchedules = cmScheduleDAO.findByChangeManagementGroupId(group.getUuid());
        for (ChangeManagementSchedule cmSchedule : cmSchedules) {
            String vnfName = cmSchedule.getVnfName();
            vnfNames.add(vnfName);
            allNames.add(vnfName);
            String status = cmSchedule.getStatus();
            String tmStatus = cmSchedule.getTmStatus();
            CMSStatusEnum cmsStatus = CMSStatusEnum.Completed.fromString(status);
            switch (cmsStatus) {
                case Scheduled:
                case Triggered:
                    addTo(inProgress, cmSchedule);
                    break;
                case Failed:
                case PastDue:
                case Error:
                case Cancelled:
                case SchedulingFailed:
                case Deleted:
                    failedNames.add(vnfName);
                    addTo(failed, cmSchedule);
                    break;
                case Completed:
                    completedNames.add(vnfName);
                    addTo(completed, cmSchedule);
                    break;
                case ApprovalRejected:
                case PendingApproval:
                case PendingSchedule:
                case ScheduledImmediate:
                default:
                    errors.applicationEvent(
                            "Unexpected Change Management schedule event status {0} encountered after notification : scheduleId={1} vnfName={2}",
                            status, s.getScheduleId(), cmSchedule.getVnfName());
                    break;
            }
            if (tmStatus != null && tmStatus.equals("Closed"))
                addTo(tmClosed, cmSchedule);
        }

        // We have at least 1 ticket with VNFs in progress. Leave schedule status as is.
        if (inProgress.size() > 0)
            return GroupAuditStatus.InProgress;

        // All VNFs are either failed or completed (or there is a bug.)

        allNames.removeAll(completedNames);
        if (allNames.size() == 0) {
            return GroupAuditStatus.Completed;
        }
        allNames.removeAll(failedNames);
        if (allNames.size() == 0) {
            return GroupAuditStatus.CompletedWithErrors;
        }
        return null;
    }

    private void addTo(Map<String, List<ChangeManagementSchedule>> map, ChangeManagementSchedule cmSchedule) {
        String status = cmSchedule.getStatus();
        String changeId = cmSchedule.getTmChangeId();
        List<ChangeManagementSchedule> list = map.get(changeId);
        if (list == null) {
            list = new ArrayList<ChangeManagementSchedule>();
            map.put(changeId, list);
        }
        list.add(cmSchedule);

    }

    private void closeTheTicket(Schedule s, ChangeManagementGroup group, String changeId,
            List<ChangeManagementSchedule> list, ClosureCode closureCode, String closingComments) throws CMSException {
        debug.debug("Closing ticket " + changeId + ":" + closureCode);
        try {
            tmClient.closeTicket(s, group, list, changeId, closureCode, closingComments);
            for (ChangeManagementSchedule cms : list) {
                cms.setTmStatus("Closed");
                cmScheduleDAO.save(cms);
            }
        } catch (CMSException e) {
            throw e;
        }

    }

}
