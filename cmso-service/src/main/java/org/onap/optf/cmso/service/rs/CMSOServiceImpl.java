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

package org.onap.optf.cmso.service.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.joda.time.DateTime;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.ApprovalStatusEnum;
import org.onap.optf.cmso.common.ApprovalTypesEnum;
import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.DomainsEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.exceptions.CMSException;
import org.onap.optf.cmso.common.exceptions.CMSNotFoundException;
import org.onap.optf.cmso.eventq.CMSQueueJob;
import org.onap.optf.cmso.model.ChangeManagementChangeWindow;
import org.onap.optf.cmso.model.ChangeManagementDetail;
import org.onap.optf.cmso.model.ChangeManagementGroup;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.DomainData;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.ScheduleQuery;
import org.onap.optf.cmso.model.dao.ChangeManagementChangeWindowDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementDetailDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleQueryDAO;
import org.onap.optf.cmso.service.rs.models.ApprovalMessage;
import org.onap.optf.cmso.service.rs.models.CMSInfo;
import org.onap.optf.cmso.service.rs.models.CMSMessage;
import org.onap.optf.cmso.service.rs.models.ChangeWindowMessage;
import org.onap.optf.cmso.service.rs.models.CmDetailsMessage;
import org.onap.optf.cmso.service.rs.models.CmDomainDataEnum;
import org.onap.optf.cmso.service.rs.models.VnfDetailsMessage;
import org.onap.optf.cmso.ticketmgt.TmClient;
import org.onap.optf.cmso.ticketmgt.bean.BuildCreateRequest;
import org.onap.optf.cmso.ticketmgt.bean.TmApprovalStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

@Controller
public class CMSOServiceImpl extends BaseSchedulerServiceImpl implements CMSOService {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    CMSQueueJob qJob;

    @Autowired
    Environment env;

    @Autowired
    ChangeManagementScheduleDAO cmScheduleDAO;

    @Autowired
    ChangeManagementGroupDAO cmGroupDAO;

    @Autowired
    ChangeManagementChangeWindowDAO cmChangeWindowDAO;

    @Autowired
    ChangeManagementDetailDAO cmDetailsDAO;

    @Autowired
    ScheduleQueryDAO scheduleQueryDAO;

    @Autowired
    ScheduleDAO scheduleDAO;

    @Autowired
    TmClient tmClient;

    @Autowired
    BuildCreateRequest buildCreateRequest;

    @Override
    public Response searchScheduleRequests(String apiVersion, Boolean includeDetails, String scheduleId,
            String scheduleName, String userId, String status, String createDateTime, String optimizerStatus,
            String workflowName, UriInfo uri, HttpServletRequest request) {

        Observation.report(LogMessages.SEARCH_SCHEDULE_REQUEST, "Received", request.getRemoteAddr(), uri.toString(), "");
        Response response = null;
        List<Schedule> schedules = new ArrayList<Schedule>();
        try {
            debug.debug("Timezone={}", TimeZone.getDefault());
            StringBuilder where = new StringBuilder();
            int maxRows = 0;
            //MultivaluedMap<String, String> qp = uri.getQueryParameters();
            // buildWhere(qp, where);
            List<ScheduleQuery> list = scheduleQueryDAO.searchSchedules(where.toString(), maxRows);
            if (list == null || !list.iterator().hasNext()) {
                throw new CMSException(Status.NOT_FOUND, LogMessages.SCHEDULE_NOT_FOUND,
                        DomainsEnum.ChangeManagement.toString(), scheduleId);
            }
            Iterator<ScheduleQuery> iter = list.iterator();
            while (iter.hasNext()) {
                Schedule s = scheduleDAO.findById(iter.next().getId()).orElse(null);
                if (s != null) {
                    schedules.add(s);
                    if (includeDetails) {
                        List<ChangeManagementGroup> groups = cmGroupDAO.findBySchedulesID(s.getId());
                        s.setGroups(groups);
                        for (ChangeManagementGroup g : groups) {
                            List<ChangeManagementSchedule> cmSchedules =
                                    cmScheduleDAO.findByChangeManagementGroupId(g.getId());
                            g.setChangeManagementSchedules(cmSchedules);
                        }
                    }
                }
            }
            response = Response.ok(schedules.toArray(new Schedule[schedules.size()])).build();
        } catch (CMSException e) {
        	Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.serverError().build();
        }

        Observation.report(LogMessages.SEARCH_SCHEDULE_REQUEST, "Returned", request.getRemoteAddr(), schedules.toString(),
                response.getStatusInfo().toString());
        return response;
    }

    @Override
    @Transactional
    public Response createScheduleRequest(String apiVersion, String scheduleId, CMSMessage scheduleMessage,
            HttpServletRequest request) {
        Observation.report(LogMessages.CREATE_SCHEDULE_REQUEST, "Received", request.getRemoteAddr(), scheduleId,
                scheduleMessage.toString());
        Response response = null;
        try {
            if (!scheduleMessage.getDomain().equals(DomainsEnum.ChangeManagement.toString())) {
                throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, "domain",
                        scheduleMessage.getDomain());
            }
            if (scheduleMessage.getScheduleId() == null || !scheduleMessage.getScheduleId().equals(scheduleId)) {
                throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, "schedulerId",
                        scheduleMessage.getScheduleId());
            }
            // Force the name to be = to the ID because there is no way fot the provide a
            // name
            scheduleMessage.setScheduleName(scheduleMessage.getScheduleId());

            List<DomainData> domainData = validateDomainData(scheduleMessage);
            boolean immediate = validate(scheduleMessage);
            Schedule schedule = validateAndAddScheduleRequest(scheduleMessage, domainData);
            if (immediate) {
                createChangeManagementImmediate(schedule, scheduleMessage);

                // Create automatic approval
                ApprovalMessage am = new ApprovalMessage();
                am.setApprovalStatus(ApprovalStatusEnum.Accepted);
                am.setApprovalType(ApprovalTypesEnum.Tier2);
                am.setApprovalUserId(schedule.getUserId());
                processApproveScheduleRequest(schedule, am, domainData);

            } else {
                createChangeManagement(schedule, scheduleMessage);
            }
            response = Response.accepted().build();
        } catch (CMSException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        	Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.CREATE_SCHEDULE_REQUEST, "Returned", request.getRemoteAddr(), scheduleId,
                response.getStatusInfo().toString());
        return response;
    }

    /**
     * Returns whether this is an immediate request
     */
    private boolean validate(CMSMessage scheduleMessage) throws CMSException {
        Set<String> groups = new HashSet<String>();
        CMSInfo info = scheduleMessage.getSchedulingInfo();
        if (info == null) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.UNABLE_TO_PARSE_SCHEDULING_INFO);
        }

        if (scheduleMessage.getSchedulingInfo().getAdditionalDurationInSeconds() == null)
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE,
                    "additionalDurationInSeconds");
        if (scheduleMessage.getSchedulingInfo().getNormalDurationInSeconds() == null)
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE,
                    "normalDurationInSeconds");
        if (scheduleMessage.getSchedulingInfo().getAdditionalDurationInSeconds() < 0)
            throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, "additionalDurationInSeconds",
                    scheduleMessage.getSchedulingInfo().getAdditionalDurationInSeconds().toString());
        if (scheduleMessage.getSchedulingInfo().getNormalDurationInSeconds() < 1)
            throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, "normalDurationInSeconds",
                    scheduleMessage.getSchedulingInfo().getNormalDurationInSeconds().toString());
        try {
            for (VnfDetailsMessage vnfDetail : scheduleMessage.getSchedulingInfo().getVnfDetails()) {
                if (vnfDetail.getChangeWindow() != null && vnfDetail.getChangeWindow().size() > 0) {
                    if (vnfDetail.getNode().size() == 0) {
                        throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "node list");
                    }
                    for (String node : vnfDetail.getNode()) {
                        if (node.equals(""))
                            throw new CMSException(Status.BAD_REQUEST, LogMessages.NODE_LIST_CONTAINS_EMTPY_NODE);
                    }
                    for (ChangeWindowMessage cw : vnfDetail.getChangeWindow()) {
                        if (cw.getStartTime() == null || cw.getStartTime().equals(""))
                            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE,
                                    "startTime");
                        if (cw.getEndTime() == null || cw.getEndTime().equals(""))
                            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE,
                                    "endTime");
                        DateTime start = CMSCallbackImpl.convertISODate(cw.getStartTime(), "startTime");
                        DateTime end = CMSCallbackImpl.convertISODate(cw.getEndTime(), "endTime");
                        if (!end.isAfter(start))
                            throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_CHANGE_WINDOW,
                                    cw.getStartTime(), cw.getEndTime());
                    }
                    if (scheduleMessage.getSchedulingInfo().getConcurrencyLimit() == null)
                        throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE,
                                "concurrencyLimit");
                    if (scheduleMessage.getSchedulingInfo().getConcurrencyLimit() < 1)
                        throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, "concurrencyLimit",
                                scheduleMessage.getSchedulingInfo().getConcurrencyLimit().toString());
                    if (scheduleMessage.getSchedulingInfo().getPolicyId() == null
                            || scheduleMessage.getSchedulingInfo().getPolicyId().equals(""))
                        throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "policyId");
                    return false;
                }
                if (vnfDetail.getGroupId() == null || vnfDetail.getGroupId().equals(""))
                    groups.add("default");
                else
                    groups.add(vnfDetail.getGroupId());
            }
        } catch (CMSException e) {
            throw e;
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            throw new CMSException(Status.INTERNAL_SERVER_ERROR, LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
        }
        // If we got here, there are no change windows....
        if (groups.size() > 1)
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MULTIPLE_GROUPS_NOT_SUPPORTED);
        return true;

    }

    private void createChangeManagement(Schedule schedule, CMSMessage scheduleMessage) throws CMSException {
        CMSInfo schedulingInfo = scheduleMessage.getSchedulingInfo();
        for (VnfDetailsMessage vnfDetail : schedulingInfo.getVnfDetails()) {
            ChangeManagementGroup cmg = new ChangeManagementGroup();
            cmg.setSchedulesId(schedule.getId());
            cmg.setGroupId("");
            if (vnfDetail.getGroupId() != null)
                cmg.setGroupId(vnfDetail.getGroupId());
            cmg.setPolicyId(schedulingInfo.getPolicyId());
            cmg.setNormalDurationInSecs(schedulingInfo.getNormalDurationInSeconds());
            cmg.setAdditionalDurationInSecs(schedulingInfo.getAdditionalDurationInSeconds());
            cmg.setConcurrencyLimit(schedulingInfo.getConcurrencyLimit());
            cmGroupDAO.save(cmg);
            for (ChangeWindowMessage cw : vnfDetail.getChangeWindow()) {
                ChangeManagementChangeWindow cmcw = new ChangeManagementChangeWindow();
                cmcw.setChangeManagementGroupsId(cmg.getId());
                DateTime start = CMSCallbackImpl.convertISODate(cw.getStartTime(), "startTime");
                DateTime end = CMSCallbackImpl.convertISODate(cw.getEndTime(), "startTime");
                cmcw.setStartTimeMillis(start.getMillis());
                cmcw.setFinishTimeMillis(end.getMillis());
                cmChangeWindowDAO.save(cmcw);
            }

            for (String vnf : vnfDetail.getNode()) {
                ChangeManagementSchedule cms = new ChangeManagementSchedule();
                cms.setChangeManagementGroupsId(cmg.getId());
                cms.setVnfName(vnf);
                cms.setStatus(CMSStatusEnum.PendingSchedule.toString());
                cmScheduleDAO.save(cms);
            }
        }
    }

    private void createChangeManagementImmediate(Schedule schedule, CMSMessage scheduleMessage) throws CMSException {
        CMSInfo schedulingInfo = scheduleMessage.getSchedulingInfo();
        for (VnfDetailsMessage vnfDetail : schedulingInfo.getVnfDetails()) {
            ChangeManagementGroup cmg = new ChangeManagementGroup();
            cmg.setSchedulesId(schedule.getId());
            cmg.setGroupId("");
            int duration = schedulingInfo.getNormalDurationInSeconds();
            int backout = schedulingInfo.getAdditionalDurationInSeconds();
            cmg.setStartTimeMillis(System.currentTimeMillis());
            cmg.setFinishTimeMillis(System.currentTimeMillis() + ((duration * 1000) + (backout * 1000)));
            cmg.setNormalDurationInSecs(duration);
            cmg.setAdditionalDurationInSecs(backout);
            if (vnfDetail.getGroupId() != null)
                cmg.setGroupId(vnfDetail.getGroupId());
            cmGroupDAO.save(cmg);
            for (String vnf : vnfDetail.getNode()) {
                ChangeManagementSchedule cms = new ChangeManagementSchedule();
                cms.setChangeManagementGroupsId(cmg.getId());
                cms.setVnfName(vnf);
                cms.setStatus(CMSStatusEnum.PendingApproval.toString());
                cmScheduleDAO.save(cms);
            }
            schedule.setStatus(CMSStatusEnum.PendingApproval.toString());
        }
    }

    private void deleteChangeManagement(Schedule schedule) throws CMSException {
        List<ChangeManagementGroup> cmgs = cmGroupDAO.findBySchedulesID(schedule.getId());

        for (ChangeManagementGroup cmg : cmgs) {
            List<ChangeManagementSchedule> schedules = cmScheduleDAO.findByChangeManagementGroupId(cmg.getId());
            for (ChangeManagementSchedule s : schedules) {
                CMSStatusEnum currentState = CMSStatusEnum.Completed.fromString(s.getStatus());
                switch (currentState) {
                    case Scheduled:
                        if (s.getTmChangeId() != null && !s.getTmChangeId().equals(""))
                            tmClient.cancelTicket(schedule, s, s.getTmChangeId());
                        s.setStatus(CMSStatusEnum.Cancelled.toString());
                        break;
                    case Triggered:
                        // Too late...
                        break;
                    default:
                        s.setStatus(CMSStatusEnum.Deleted.toString());
                }
                cmScheduleDAO.save(s);
            }
        }

    }

    private List<DomainData> validateDomainData(CMSMessage scheduleMessage) throws CMSException {
        List<Map<String, String>> domainData = scheduleMessage.getDomainData();
        List<DomainData> domainDataList = new ArrayList<DomainData>();
        Set<String> requiredFields = new HashSet<String>();
        for (CmDomainDataEnum req : CmDomainDataEnum.values()) {
            if (req.isRequired())
                requiredFields.add(req.name());
        }
        for (Map<String, String> nameValue : domainData) {
            for (String name : nameValue.keySet()) {
                String value = nameValue.get(name);
                // Save for later validation
                DomainData dd = new DomainData();
                dd.setName(name);
                dd.setValue(value);
                domainDataList.add(dd);
                requiredFields.remove(name);
                try {
                    CmDomainDataEnum.valueOf(name);
                } catch (Exception e) {
                    Observation.report(LogMessages.UNDEFINED_DOMAIN_DATA_ATTRIBUTE, DomainsEnum.ChangeManagement.name(), name,
                            value);
                }
            }
        }
        if (requiredFields.size() > 0) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE,
                    requiredFields.toString());
        }
        return domainDataList;

    }

    @Override
    @Transactional
    public Response deleteScheduleRequest(String apiVersion, String scheduleId, HttpServletRequest request) {
        Response response = null;
        Observation.report(LogMessages.DELETE_SCHEDULE_REQUEST, "Received", request.getRemoteAddr(), scheduleId, "");
        try {
            Schedule schedule = scheduleDAO.findByDomainScheduleID(DomainsEnum.ChangeManagement.toString(), scheduleId);
            if (schedule == null) {
                throw new CMSNotFoundException(DomainsEnum.ChangeManagement.toString(), scheduleId);
            }
            deleteChangeManagement(schedule);
            deleteScheduleRequest(DomainsEnum.ChangeManagement.toString(), scheduleId);
            response = Response.noContent().build();
        } catch (CMSException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.DELETE_SCHEDULE_REQUEST, "Returned", request.getRemoteAddr(), scheduleId,
                response.getStatusInfo().toString());
        return response;
    }

    @Override
    public Response getScheduleRequestInfo(String apiVersion, String scheduleId, HttpServletRequest request) {
        Response response = null;
        Observation.report(LogMessages.GET_SCHEDULE_REQUEST_INFO, "Received", request.getRemoteAddr(), scheduleId, "");
        Schedule schedule = null;
        try {
            schedule = scheduleDAO.findByDomainScheduleID(DomainsEnum.ChangeManagement.toString(), scheduleId);
            if (schedule == null) {
                throw new CMSException(Status.NOT_FOUND, LogMessages.SCHEDULE_NOT_FOUND,
                        DomainsEnum.ChangeManagement.toString(), scheduleId);
            }
            response = Response.ok().entity(schedule).build();
        } catch (CMSException e) {
            Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.GET_SCHEDULE_REQUEST_INFO, "Returned", request.getRemoteAddr(), scheduleId,
                response.getStatusInfo().toString());
        return response;
    }

    @Override
    @Transactional
    public Response approveScheduleRequest(String apiVersion, String scheduleId, ApprovalMessage approval,
            HttpServletRequest request) {
        Response response = null;
        Observation.report(LogMessages.APPROVE_SCHEDULE_REQUEST, "Received", request.getRemoteAddr(), scheduleId,
                approval.toString());
        try {
            String domain = DomainsEnum.ChangeManagement.toString();
            Schedule s = scheduleDAO.findByDomainScheduleID(domain, scheduleId);
            if (s == null) {
                throw new CMSNotFoundException(domain, scheduleId);
            }
            processApproveScheduleRequest(s, approval, s.getDomainData());
            response = Response.noContent().build();
        } catch (CMSException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.APPROVE_SCHEDULE_REQUEST, "Returned", request.getRemoteAddr(), scheduleId, "");
        return response;
    }

    private void processApproveScheduleRequest(Schedule s, ApprovalMessage approval, List<DomainData> domainData)
            throws CMSException {
        s = scheduleDAO.lockOne(s.getId());
        String domain = DomainsEnum.ChangeManagement.toString();
        processApproval(s, domain, approval);
        if (s.getStatus().equals(CMSStatusEnum.Accepted.toString())) {
            openTickets(s, domainData);
        }
        if (s.getStatus().equals(CMSStatusEnum.Rejected.toString())) {
            updateChangeManagementSchedules(s, CMSStatusEnum.ApprovalRejected);
        }
    }

    private void openTickets(Schedule s, List<DomainData> domainData) throws CMSException {
        debug.debug("Entered openTickets scheduleId=" + s.getScheduleId());

        Integer max_vnfs_per_ticket = env.getProperty("tm.vnfs.per.ticket", Integer.class, 1);

        List<ChangeManagementGroup> groups = cmGroupDAO.findBySchedulesID(s.getId());
        for (ChangeManagementGroup group : groups) {

            List<ChangeManagementSchedule> schedules = cmScheduleDAO.findByChangeManagementGroupId(group.getId());
            List<List<ChangeManagementSchedule>> ticketList = new ArrayList<List<ChangeManagementSchedule>>();
            List<ChangeManagementSchedule> current = null;
            for (ChangeManagementSchedule cms : schedules) {
                if (current == null || current.size() == max_vnfs_per_ticket) {
                    current = new ArrayList<ChangeManagementSchedule>();
                    ticketList.add(current);
                }
                current.add(cms);
            }
            for (List<ChangeManagementSchedule> list : ticketList) {
                openTicketForList(s, group, list, domainData);
            }
        }
        debug.debug("Exited openTickets scheduleId=" + s.getScheduleId());
    }

    private void openTicketForList(Schedule schedule, ChangeManagementGroup group, List<ChangeManagementSchedule> list,
            List<DomainData> domainData) throws CMSException {
        List<String> vnfNames = new ArrayList<>();
        for (ChangeManagementSchedule cms : list) {
            vnfNames.add(cms.getVnfName());
        }

        debug.debug(
                "Calling createChangeTicket scheduleId=" + schedule.getScheduleId() + ", group=" + group.getGroupId(),
                ", vnfNames=" + vnfNames);
        String changeId = tmClient.createChangeTicket(schedule, group, vnfNames, domainData);

        // Pre-approve the ticket
        for (ChangeManagementSchedule cms : list) {
            cms.getTmApprovalStatus();
            cms.setTmChangeId(changeId);
            cms.setTmApprovalStatus(TmApprovalStatusEnum.Approved.toString());
            // cms.setStatus(CMSStatusEnum.PendingApproval.toString());
            if (cms.getStartTimeMillis() == null)
                cms.setStatus(CMSStatusEnum.ScheduledImmediate.toString());
            else
                cms.setStatus(CMSStatusEnum.Scheduled.toString());
            cmScheduleDAO.save(cms);
        }
        schedule.setStatus(CMSStatusEnum.Scheduled.toString());
        scheduleDAO.save(schedule);
    }

    private void updateChangeManagementSchedules(Schedule s, CMSStatusEnum approvalrejected) {
        debug.debug("Entered updateChangeManagementSchedules");
        List<ChangeManagementGroup> groups = cmGroupDAO.findBySchedulesID(s.getId());
        for (ChangeManagementGroup group : groups) {
            List<ChangeManagementSchedule> schedules = cmScheduleDAO.findByChangeManagementGroupId(group.getId());
            for (ChangeManagementSchedule schedule : schedules) {
                schedule.setStatus(approvalrejected.toString());
                cmScheduleDAO.save(schedule);
            }
        }
        debug.debug("Exited updateChangeManagementSchedules");
    }

    @Override
    public Response searchScheduleRequestDetails(String apiVersion, String scheduleId, String scheduleName,
            String userId, String status, String createDateTime, String optimizerStatus, String requestApprovalUserId,
            String requestApprovalStatus, String requestApprovalType, String workflowName, String vnfName, String vnfId,
            String vnfStatus,
            // String vnfScheduleId,
            String startTime, String finishTime, String lastInstanceTime, String tmChangeId,
            // String approvalUserId,
            // String approvalStatus,
            // String approvalType,
            Integer maxSchedules, String lastScheduleId, Integer concurrencyLimit, UriInfo uri,
            HttpServletRequest request) {

        Response response = null;
        Observation.report(LogMessages.SEARCH_SCHEDULE_REQUEST_DETAILS, "Received", request.getRemoteAddr(),
                uri.getRequestUri().getQuery());
        List<CmDetailsMessage> schedules = new ArrayList<CmDetailsMessage>();

        try {
            debug.debug("Timezone={}" , TimeZone.getDefault());
            MultivaluedMap<String, String> qp = uri.getQueryParameters();
            StringBuilder where = new StringBuilder();
            int maxRows = 0;
            if (maxSchedules != null && maxSchedules > 0)
                maxRows = maxSchedules;
            buildWhere(qp, where);
            List<ChangeManagementDetail> list = cmDetailsDAO.searchScheduleDetails(where.toString(), maxRows);
            if (list == null || !list.iterator().hasNext()) {
                throw new CMSException(Status.NOT_FOUND, LogMessages.SCHEDULE_NOT_FOUND,
                        DomainsEnum.ChangeManagement.toString(), scheduleId);
            }
            Iterator<ChangeManagementDetail> iter = list.iterator();
            Map<Integer, Schedule> scheduleMap = new HashMap<Integer, Schedule>();
            while (iter.hasNext()) {
                ChangeManagementDetail cms = iter.next();
                CmDetailsMessage msg = buildResponse(cms, scheduleMap);
                schedules.add(msg);
            }
            response = Response.ok(schedules.toArray(new CmDetailsMessage[schedules.size()])).build();
        } catch (CMSException e) {
            Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.SEARCH_SCHEDULE_REQUEST_DETAILS, "Returned", request.getRemoteAddr(),
                response.getStatusInfo().toString());
        return response;

    }

    private void buildWhere(MultivaluedMap<String, String> qp, StringBuilder where) throws CMSException {
        String delim = " where ";
        for (String urlName : qp.keySet()) {
            List<String> values = qp.get(urlName);
            String clause = CmQueryParameters.buildClause(urlName, values);
            if (clause != null && !clause.equals("")) {
                where.append(delim).append("\n").append(clause).append("\n");
                delim = "AND";
            }
        }

    }

    private CmDetailsMessage buildResponse(ChangeManagementDetail cms, Map<Integer, Schedule> scheduleMap) {
        CmDetailsMessage msg = new CmDetailsMessage();
        msg.setVnfId(cms.getVnfId());
        msg.setVnfName(cms.getVnfName());
        msg.setStatus(cms.getStatus());
        msg.setTmChangeId(cms.getTmChangeId());
        msg.setFinishTimeMillis(cms.getFinishTimeMillis());
        msg.setStartTimeMillis(cms.getStartTimeMillis());
        msg.setLastInstanceStartTimeMillis(cms.getLastInstanceStartTimeMillis());
        msg.setGroupId(cms.getGroupId());
        msg.setPolicyId(cms.getPolicyId());
        msg.setTmApprovalStatus(cms.getTmApprovalStatus());
        msg.setTmStatus(cms.getTmStatus());
        msg.setStatusMessage(cms.getStatusMessage());
        msg.setDispatchTimeMillis(cms.getDispatchTimeMillis());
        msg.setExecutionCompletedTimeMillis(cms.getExecutionCompletedTimeMillis());
        msg.setMsoMessage(cms.getMsoMessage());
        msg.setMsoRequestId(cms.getMsoRequestId());
        msg.setMsoStatus(cms.getMsoStatus());
        msg.setMsoTimeMillis(cms.getMsoTimeMillis());
        if (!scheduleMap.containsKey(cms.getSchedulesId())) {
            Schedule schedule = scheduleDAO.findById(cms.getSchedulesId()).orElse(null);
            if (schedule != null) {
                // DO not innclude in the results
                schedule.setScheduleInfo(null);
                // schedule.setSchedule(null);
                scheduleMap.put(cms.getSchedulesId(), schedule);
            }
        }
        if (scheduleMap.containsKey(cms.getSchedulesId())) {
            msg.setScheduleRequest(scheduleMap.get(cms.getSchedulesId()));
        }
        return msg;
    }

}
