/*
 * Copyright © 2017-2019 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.joda.time.DateTime;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.DomainsEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.exceptions.CMSException;
import org.onap.optf.cmso.common.exceptions.CMSNotFoundException;
import org.onap.optf.cmso.eventq.CMSQueueJob;
import org.onap.optf.cmso.model.ChangeManagementDetail;
import org.onap.optf.cmso.model.ChangeManagementGroup;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.ScheduleQuery;
import org.onap.optf.cmso.model.dao.ChangeManagementChangeWindowDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementDetailDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.onap.optf.cmso.model.dao.ElementDataDAO;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleQueryDAO;
import org.onap.optf.cmso.service.rs.models.ApprovalMessage;
import org.onap.optf.cmso.service.rs.models.ChangeWindowMessage;
import org.onap.optf.cmso.service.rs.models.CmDetailsMessage;
import org.onap.optf.cmso.service.rs.models.CmDomainDataEnum;
import org.onap.optf.cmso.service.rs.models.CmsoInfo;
import org.onap.optf.cmso.service.rs.models.CmsoMessage;
import org.onap.optf.cmso.service.rs.models.VnfDetailsMessage;
import org.onap.optf.cmso.service.rs.models.v2.ChangeWindow;
import org.onap.optf.cmso.service.rs.models.v2.ElementInfo;
import org.onap.optf.cmso.service.rs.models.v2.NameValue;
import org.onap.optf.cmso.service.rs.models.v2.OptimizedScheduleMessage;
import org.onap.optf.cmso.service.rs.models.v2.PolicyInfo;
import org.onap.optf.cmso.service.rs.models.v2.SchedulingData;
import org.onap.optf.cmso.ticketmgt.TmClient;
import org.onap.optf.cmso.ticketmgt.bean.BuildCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Controller
public class CmsoServiceImpl extends CommonServiceImpl implements CmsoService {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    CMSQueueJob qqJob;

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
    ElementDataDAO elementDataDAO;

    @Autowired
    TmClient tmClient;

    @Autowired
    BuildCreateRequest buildCreateRequest;

    @Override
    public Response searchScheduleRequests(String apiVersion, Boolean includeDetails, String scheduleId,
                    String scheduleName, String userId, String status, String createDateTime, String optimizerStatus,
                    String workflowName, UriInfo uri, HttpServletRequest request) {

        Observation.report(LogMessages.SEARCH_SCHEDULE_REQUEST, "Received", request.getRemoteAddr(), uri.toString(),
                        "");
        Response response = null;
        List<Schedule> schedules = new ArrayList<Schedule>();
        try {
            debug.debug("Timezone={}", TimeZone.getDefault());
            StringBuilder where = new StringBuilder();
            int maxRows = 0;
            // MultivaluedMap<String, String> qp = uri.getQueryParameters();
            // buildWhere(qp, where);
            List<ScheduleQuery> list = scheduleQueryDAO.searchSchedules(where.toString(), maxRows);
            if (list == null || !list.iterator().hasNext()) {
                throw new CMSException(Status.NOT_FOUND, LogMessages.SCHEDULE_NOT_FOUND,
                                DomainsEnum.ChangeManagement.toString(), scheduleId);
            }
            Iterator<ScheduleQuery> iter = list.iterator();
            while (iter.hasNext()) {
                Schedule sch = scheduleDAO.findById(iter.next().getUuid()).orElse(null);
                if (sch != null) {
                    schedules.add(sch);
                    if (includeDetails) {
                        List<ChangeManagementGroup> groups = cmGroupDAO.findBySchedulesID(sch.getUuid());
                        sch.setGroups(groups);
                        for (ChangeManagementGroup g : groups) {
                            List<ChangeManagementSchedule> cmSchedules =
                                            cmScheduleDAO.findByChangeManagementGroupId(g.getUuid());
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

        Observation.report(LogMessages.SEARCH_SCHEDULE_REQUEST, "Returned", request.getRemoteAddr(),
                        schedules.toString(), response.getStatusInfo().toString());
        return response;
    }

    @Override
    @Transactional
    public Response createScheduleRequest(String apiVersion, String scheduleId, CmsoMessage scheduleMessage,
                    HttpServletRequest request) {
        Observation.report(LogMessages.CREATE_SCHEDULE_REQUEST, "Received", request.getRemoteAddr(), scheduleId,
                        scheduleMessage.toString());
        Response response = null;
        try {
            OptimizedScheduleMessage osm = adaptScheduleMessage(scheduleMessage);
            createSchedule(osm, scheduleId, request);
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

    private OptimizedScheduleMessage adaptScheduleMessage(CmsoMessage sm)
                    throws CMSException, JsonParseException, JsonMappingException, IOException {
        OptimizedScheduleMessage osm = new OptimizedScheduleMessage();
        osm.setScheduleId(sm.getScheduleId());
        osm.setDomain(sm.getDomain());
        osm.setScheduleName(sm.getScheduleName());
        osm.setUserId(sm.getUserId());
        List<NameValue> dd = new ArrayList<>();
        List<Map<String, String>> smdd = sm.getDomainData();
        for (Map<String, String> map : smdd) {
            for (String name : map.keySet()) {
                NameValue nv = new NameValue(name, map.get(name));
                dd.add(nv);
            }
        }
        osm.setCommonData(dd);
        CmsoInfo sinfo = sm.getSchedulingInfo();
        if (sinfo == null) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "schedulingInfo");
        }
        SchedulingData sd = new SchedulingData();
        osm.setSchedulingData(sd);
        sd.setAdditionalDurationInSeconds(sinfo.getAdditionalDurationInSeconds());
        sd.setConcurrencyLimit(sinfo.getConcurrencyLimit());
        sd.setNormalDurationInSeconds(sinfo.getNormalDurationInSeconds());

        List<PolicyInfo> policies = new ArrayList<>();
        PolicyInfo policyInfo = new PolicyInfo();
        policyInfo.setPolicyName(sinfo.getPolicyId());
        sd.setPolicies(policies);

        List<VnfDetailsMessage> details = sinfo.getVnfDetails();
        if (details == null) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "vnfDetails");
        }
        List<ChangeWindow> windows = new ArrayList<>();
        List<ElementInfo> elements = new ArrayList<>();
        for (VnfDetailsMessage vdm : details) {
            if (vdm.getChangeWindow() != null) {
                for (ChangeWindowMessage cwm : vdm.getChangeWindow()) {
                    ChangeWindow cw = new ChangeWindow();
                    DateTime start = CmsoOptimizerCallbackImpl.convertIsoDate(cwm.getStartTime(), "startTime");
                    DateTime end = CmsoOptimizerCallbackImpl.convertIsoDate(cwm.getEndTime(), "endTime");
                    cw.setStartTime(start.toDate());
                    cw.setEndTime(end.toDate());
                    windows.add(cw);
                }
            }
            for (String node : vdm.getNode()) {
                ElementInfo element = new ElementInfo();
                element.setElementId(node);
                element.setRequest(getRequestFromCallbackData(node, dd));
                element.setGroupId(vdm.getGroupId());
                elements.add(element);
            }
        }
        sd.setElements(elements);
        sd.setChangeWindows(windows);
        return osm;
    }

    private Object getRequestFromCallbackData(String node, List<NameValue> dd)
                    throws CMSException, JsonParseException, JsonMappingException, IOException {
        for (NameValue nv : dd) {
            if (nv.getName().equals(CmDomainDataEnum.CallbackData.toString())) {
                String value = nv.getValue().toString();
                ObjectMapper om = new ObjectMapper();
                JsonNode json = om.readValue(value, JsonNode.class);
                JsonNode details = json.get("requestDetails");
                int ii = 0;
                for (ii = 0; ii < details.size(); ii++) {
                    JsonNode request = details.get(ii);
                    String id = request.get("vnfName").asText();
                    if (id.equals(node)) {
                        return request;
                    }
                }
            }
        }
        throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "CallbackData", "");
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
            Schedule sch = scheduleDAO.findByDomainScheduleID(domain, scheduleId);
            if (sch == null) {
                throw new CMSNotFoundException(domain, scheduleId);
            }
            processApproveScheduleRequest(sch, approval, sch.getDomainData());
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

    @Override
    public Response searchScheduleRequestDetails(String apiVersion, String scheduleId, String scheduleName,
                    String userId, String status, String createDateTime, String optimizerStatus,
                    String requestApprovalUserId, String requestApprovalStatus, String requestApprovalType,
                    String workflowName, String vnfName, String vnfId, String vnfStatus,
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
            debug.debug("Timezone={}", TimeZone.getDefault());
            MultivaluedMap<String, String> qp = uri.getQueryParameters();
            StringBuilder where = new StringBuilder();
            int maxRows = 0;
            if (maxSchedules != null && maxSchedules > 0) {
                maxRows = maxSchedules;
            }
            buildWhere(qp, where);
            List<ChangeManagementDetail> list = cmDetailsDAO.searchScheduleDetails(where.toString(), maxRows);
            if (list == null || !list.iterator().hasNext()) {
                throw new CMSException(Status.NOT_FOUND, LogMessages.SCHEDULE_NOT_FOUND,
                                DomainsEnum.ChangeManagement.toString(), scheduleId);
            }
            Iterator<ChangeManagementDetail> iter = list.iterator();
            Map<UUID, Schedule> scheduleMap = new HashMap<UUID, Schedule>();
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

    private CmDetailsMessage buildResponse(ChangeManagementDetail cms, Map<UUID, Schedule> scheduleMap) {
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
        if (!scheduleMap.containsKey(cms.getSchedulesUuid())) {
            Schedule schedule = scheduleDAO.findById(cms.getSchedulesUuid()).orElse(null);
            if (schedule != null) {
                // DO not innclude in the results
                schedule.setScheduleInfo(null);
                // schedule.setSchedule(null);
                scheduleMap.put(cms.getSchedulesUuid(), schedule);
            }
        }
        if (scheduleMap.containsKey(cms.getSchedulesUuid())) {
            msg.setScheduleRequest(scheduleMap.get(cms.getSchedulesUuid()));
        }
        return msg;
    }
}
