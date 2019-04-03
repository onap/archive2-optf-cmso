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

package org.onap.optf.cmso.service.rs;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.core.Response.Status;
import org.onap.optf.cmso.common.ApprovalStatusEnum;
import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.exceptions.CMSAlreadyExistsException;
import org.onap.optf.cmso.common.exceptions.CMSException;
import org.onap.optf.cmso.common.exceptions.CMSNotFoundException;
import org.onap.optf.cmso.model.ApprovalType;
import org.onap.optf.cmso.model.DomainData;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.ScheduleApproval;
import org.onap.optf.cmso.model.dao.ApprovalTypeDAO;
import org.onap.optf.cmso.model.dao.DomainDataDAO;
import org.onap.optf.cmso.model.dao.ScheduleApprovalDAO;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.service.rs.models.ApprovalMessage;
import org.onap.optf.cmso.service.rs.models.v2.OptimizedScheduleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BaseSchedulerServiceImpl {
    private static EELFLogger log = EELFManager.getInstance().getLogger(BaseSchedulerServiceImpl.class);

    @Autowired
    protected ScheduleDAO scheduleDao;

    @Autowired
    DomainDataDAO domainDataDao;

    @Autowired
    ApprovalTypeDAO approvalTypeDao;

    @Autowired
    ScheduleApprovalDAO scheduleApprovalDao;

    protected Schedule validateAndAddScheduleRequest(OptimizedScheduleMessage scheduleMessage,
                    List<DomainData> domainData) throws CMSException {
        messageValidations(scheduleMessage);
        Schedule sch = scheduleDao.findByDomainScheduleId(scheduleMessage.getDomain(), scheduleMessage.getScheduleId());

        if (sch != null) {
            throw new CMSAlreadyExistsException(scheduleMessage.getDomain(), scheduleMessage.getScheduleId());
        }
        sch = new Schedule();
        sch.setUuid(UUID.randomUUID());
        sch.setUserId(scheduleMessage.getUserId());
        sch.setCreateDateTimeMillis(System.currentTimeMillis());
        sch.setDomain(scheduleMessage.getDomain());
        sch.setScheduleId(scheduleMessage.getScheduleId());
        sch.setOptimizerTransactionId(sch.getScheduleId()); // No reason these cannot be the same as
        // these
        // are 1<=>1 at this
        // point.
        sch.setScheduleName(scheduleMessage.getScheduleName());
        sch.setOptimizerAttemptsToSchedule(0);
        sch.setScheduleInfo(scheduleMessage.getSchedulingData().toString());
        sch.setStatus(CMSStatusEnum.PendingSchedule.toString());
        scheduleDao.save(sch);
        for (DomainData dd : domainData) {
            dd.setUuid(UUID.randomUUID());
            sch.addDomainData(dd);
            domainDataDao.save(dd);
        }
        scheduleDao.save(sch);
        return sch;
    }

    private void messageValidations(OptimizedScheduleMessage scheduleMessage) throws CMSException {
        if (scheduleMessage.getScheduleName() == null || scheduleMessage.getScheduleName().equals("")) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "schedulerName", "");
        }
        if (scheduleMessage.getUserId() == null || scheduleMessage.getUserId().equals("")) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "userId", "");
        }
    }

    protected void deleteScheduleRequest(String domain, String scheduleId) throws CMSException {
        Schedule sch = scheduleDao.findByDomainScheduleId(domain, scheduleId);
        if (sch == null) {
            throw new CMSNotFoundException(domain, scheduleId);
        }
        CMSStatusEnum currentStatus = CMSStatusEnum.Completed.fromString(sch.getStatus());
        sch.setDeleteDateTimeMillis(System.currentTimeMillis());
        switch (currentStatus) {
            case Scheduled:
                // TODO CLose all tickets....
                sch.setStatus(CMSStatusEnum.Cancelled.toString());
                break;
            case NotificationsInitiated:
                throw new CMSException(Status.NOT_ACCEPTABLE, LogMessages.CANNOT_CANCEL_IN_PROGRESS);
            default:
                sch.setStatus(CMSStatusEnum.Deleted.toString());
        }
        scheduleDao.save(sch);
    }

    protected Schedule processApproval(Schedule sch, String domain, ApprovalMessage approvalMessage)
                    throws CMSException {
        String scheduleId = sch.getScheduleId();
        ApprovalType approvalType =
                        approvalTypeDao.findByDomainAndType(domain, approvalMessage.getApprovalType().toString());
        if (approvalType == null) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, "approvalType",
                            approvalMessage.getApprovalType().toString());
        }

        if (!sch.getStatus().equals(CMSStatusEnum.PendingApproval.toString())) {
            throw new CMSException(Status.PRECONDITION_FAILED, LogMessages.NOT_PENDING_APPROVAL, domain, scheduleId,
                            sch.getStatus());
        }
        if (approvalMessage.getApprovalUserId() == null || approvalMessage.getApprovalUserId().equals("")) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "userId");
        }
        ScheduleApproval sa = null;
        // only 1 approval per user....
        if (sch.getScheduleApprovals() != null) {
            for (ScheduleApproval scheduleApproval : sch.getScheduleApprovals()) {
                if (scheduleApproval.getUserId().equals(approvalMessage.getApprovalUserId())
                                && scheduleApproval.getApprovalTypesUuid().equals(approvalType.getUuid())) {
                    sa = scheduleApproval;
                }
            }
        }
        if (sa == null) {
            sa = new ScheduleApproval();
            sa.setUuid(UUID.randomUUID());
            sa.setSchedule(sch);
            sa.setApprovalTypesUuid(approvalType.getUuid());
            sa.setUserId(approvalMessage.getApprovalUserId());
        }
        // Ignore what time is on the message
        sa.setApprovalDateTimeMillis(System.currentTimeMillis());
        sa.setStatus(approvalMessage.getApprovalStatus().toString());
        sa.setSchedule(sch);
        sch.addScheduleApproval(sa);
        scheduleDao.save(sch);
        if (sa.getStatus().equals(ApprovalStatusEnum.Rejected.toString())) {
            sch.setStatus(CMSStatusEnum.Rejected.toString());
        } else {
            if (allApprovalsReceived(sch, sa)) {
                sch.setStatus(CMSStatusEnum.Accepted.toString());
            }
        }
        scheduleDao.save(sch);
        return sch;
    }

    private boolean allApprovalsReceived(Schedule schedule, ScheduleApproval sa) {
        Map<UUID, Integer> requiredApprovalsByType = new HashMap<>(); // Approval
        // countdown
        Map<UUID, ApprovalType> approvalsByType = new HashMap<>(); // Just
        // for
        // logging

        List<ApprovalType> approvalTypes = approvalTypeDao.findByDomain(schedule.getDomain());
        for (ApprovalType at : approvalTypes) {
            UUID type = at.getUuid();
            Integer count = at.getApprovalCount();
            requiredApprovalsByType.put(type, count);
            approvalsByType.put(at.getUuid(), at);
        }

        // Account for approvals so far
        List<ScheduleApproval> existingApprovals = schedule.getScheduleApprovals();
        if (existingApprovals == null) {
            // This is necessary when doing automatic approvals because
            // the schedule will not return the approvals here
            existingApprovals = new ArrayList<ScheduleApproval>();
            existingApprovals.add(sa);
        }
        for (ScheduleApproval approval : existingApprovals) {
            if (approval.getStatus().equals(ApprovalStatusEnum.Accepted.toString())) {
                Integer remaining = requiredApprovalsByType.get(approval.getApprovalTypesUuid());
                if (remaining != null) {
                    remaining = remaining - 1;
                    requiredApprovalsByType.put(approval.getApprovalTypesUuid(), remaining);
                } else {
                    log.warn("Ignored Unidentified approval type {0} for domain {1}", approval.getApprovalTypesUuid(),
                                    schedule.getDomain());
                }
            }
        }
        for (UUID id : requiredApprovalsByType.keySet()) {
            Integer remaining = requiredApprovalsByType.get(id);
            if (remaining > 0) {
                return false;
            }
        }
        return true;
    }
}
