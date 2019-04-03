/*oaoo
 * Copyright © 2017-2019 AT&T Intellectaoual Property. Modifications Copyright © 2018 IBM.
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
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;
import org.onap.optf.cmso.common.ApprovalStatusEnum;
import org.onap.optf.cmso.common.ApprovalTypesEnum;
import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.DomainsEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.exceptions.CMSException;
import org.onap.optf.cmso.eventq.CMSQueueJob;
import org.onap.optf.cmso.model.ChangeManagementChangeWindow;
import org.onap.optf.cmso.model.ChangeManagementGroup;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.DomainData;
import org.onap.optf.cmso.model.ElementData;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ChangeManagementChangeWindowDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementDetailDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.onap.optf.cmso.model.dao.ElementDataDAO;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleQueryDAO;
import org.onap.optf.cmso.service.rs.models.ApprovalMessage;
import org.onap.optf.cmso.service.rs.models.v2.ChangeWindow;
import org.onap.optf.cmso.service.rs.models.v2.ElementInfo;
import org.onap.optf.cmso.service.rs.models.v2.NameValue;
import org.onap.optf.cmso.service.rs.models.v2.OptimizedScheduleMessage;
import org.onap.optf.cmso.service.rs.models.v2.SchedulingData;
import org.onap.optf.cmso.ticketmgt.TmClient;
import org.onap.optf.cmso.ticketmgt.bean.BuildCreateRequest;
import org.onap.optf.cmso.ticketmgt.bean.TmApprovalStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

@Controller
public class CommonServiceImpl extends BaseSchedulerServiceImpl {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    CMSQueueJob qqJob;

    @Autowired
    Environment env;

    @Autowired
    ChangeManagementScheduleDAO cmScheduleDao;

    @Autowired
    ChangeManagementGroupDAO cmGroupDao;

    @Autowired
    ChangeManagementChangeWindowDAO cmChangeWindowDao;

    @Autowired
    ChangeManagementDetailDAO cmDetailsDao;

    @Autowired
    ScheduleQueryDAO scheduleQueryDao;

    @Autowired
    ScheduleDAO scheduleDao;

    @Autowired
    ElementDataDAO elementDataDao;

    @Autowired
    TmClient tmClient;

    @Autowired
    BuildCreateRequest buildCreateRequest;


    protected void createSchedule(OptimizedScheduleMessage scheduleMessage, String scheduleId,
                    HttpServletRequest request) throws CMSException, JsonProcessingException {
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

        List<DomainData> domainData = marshallDomainData(scheduleMessage);
        Map<String, List<ElementInfo>> groups = new HashMap<>();
        boolean immediate = validate(scheduleMessage, groups);
        Schedule schedule = validateAndAddScheduleRequest(scheduleMessage, domainData);
        if (immediate) {
            createChangeManagementImmediate(schedule, scheduleMessage, groups);

            // Create automatic approval
            ApprovalMessage am = new ApprovalMessage();
            am.setApprovalStatus(ApprovalStatusEnum.Accepted);
            am.setApprovalType(ApprovalTypesEnum.Tier2);
            am.setApprovalUserId(schedule.getUserId());
            processApproveScheduleRequest(schedule, am, domainData);

        } else {
            createChangeManagement(schedule, scheduleMessage, groups);
        }
    }

    /**
     * Returns whether this is an immediate request.
     */
    private boolean validate(OptimizedScheduleMessage scheduleMessage, Map<String, List<ElementInfo>> groups)
                    throws CMSException {
        SchedulingData info = scheduleMessage.getSchedulingData();
        boolean immediate = true;
        if (info == null) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.UNABLE_TO_PARSE_SCHEDULING_INFO);
        }

        if (info.getAdditionalDurationInSeconds() == null) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE,
                            "additionalDurationInSeconds");
        }
        if (info.getNormalDurationInSeconds() == null) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE,
                            "normalDurationInSeconds");
        }
        if (info.getAdditionalDurationInSeconds() < 0) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, "additionalDurationInSeconds",
                            info.getAdditionalDurationInSeconds().toString());
        }
        if (info.getNormalDurationInSeconds() < 1) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, "normalDurationInSeconds",
                            info.getNormalDurationInSeconds().toString());
        }
        if (info.getChangeWindows() != null && info.getChangeWindows().size() > 0) {
            for (ChangeWindow cw : info.getChangeWindows()) {
                immediate = false;
                if (cw.getStartTime() == null) {
                    throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "startTime");
                }
                if (cw.getEndTime() == null) {
                    throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "endTime");
                }
                Date start = cw.getStartTime();
                Date end = cw.getEndTime();
                if (!end.after(start)) {
                    throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_CHANGE_WINDOW, start.toString(),
                                    end.toString());
                }
            }
            if (info.getConcurrencyLimit() == null) {
                throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "concurrencyLimit");
            }
            if (info.getConcurrencyLimit() < 1) {
                throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, "concurrencyLimit",
                                info.getConcurrencyLimit().toString());
            }
        }

        if (info.getElements() == null || info.getElements().size() == 0) {
            throw new CMSException(Status.BAD_REQUEST, LogMessages.MISSING_REQUIRED_ATTRIBUTE, "elements");
        }

        for (ElementInfo element : info.getElements()) {
            if (element.getGroupId() == null || element.getGroupId().equals("")) {
                element.setElementId("default");
            }
            List<ElementInfo> groupedElements = groups.get(element.getGroupId());
            if (groupedElements == null) {
                groupedElements = new ArrayList<ElementInfo>();
                groups.put(element.getGroupId(), groupedElements);
            }

            groupedElements.add(element);
        }
        return immediate;
    }

    private void createChangeManagement(Schedule schedule, OptimizedScheduleMessage scheduleMessage,
                    Map<String, List<ElementInfo>> groups) throws CMSException {
        SchedulingData schedulingInfo = scheduleMessage.getSchedulingData();
        for (String groupId : groups.keySet()) {

            ChangeManagementGroup cmg = new ChangeManagementGroup();
            cmg.setUuid(UUID.randomUUID());
            cmg.setSchedulesUuid(schedule.getUuid());
            cmg.setGroupId(groupId);
            cmg.setPolicyId(schedulingInfo.getPolicies().toString());
            cmg.setNormalDurationInSecs(schedulingInfo.getNormalDurationInSeconds());
            cmg.setAdditionalDurationInSecs(schedulingInfo.getAdditionalDurationInSeconds());
            cmg.setConcurrencyLimit(schedulingInfo.getConcurrencyLimit());
            cmGroupDao.save(cmg);
            for (ChangeWindow cw : schedulingInfo.getChangeWindows()) {
                ChangeManagementChangeWindow cmcw = new ChangeManagementChangeWindow();
                cmcw.setUuid(UUID.randomUUID());
                cmcw.setChangeManagementGroupUuid(cmg.getUuid());
                cmcw.setStartTimeMillis(cw.getStartTime().getTime());
                cmcw.setFinishTimeMillis(cw.getEndTime().getTime());
                cmChangeWindowDao.save(cmcw);
            }

            for (ElementInfo element : groups.get(groupId)) {
                ChangeManagementSchedule cms = new ChangeManagementSchedule();
                cms.setUuid(UUID.randomUUID());
                cms.setChangeManagementGroupUuid(cmg.getUuid());
                cms.setVnfName(element.getElementId());
                cms.setStatus(CMSStatusEnum.PendingSchedule.toString());
                cms.setRequest(element.getRequest().toString());
                cmScheduleDao.save(cms);
                // Save elementData
                saveElementData(cms, element);
            }
        }
    }

    private void saveElementData(ChangeManagementSchedule cms, ElementInfo element) {
        List<NameValue> elementData = element.getElementData();
        if (elementData != null) {
            for (NameValue nv : elementData) {
                ElementData ed = new ElementData();
                ed.setUuid(UUID.randomUUID());
                ed.setChangeManagementSchedulesUuid(cms.getUuid());
                ed.setName(nv.getName());
                // TODO Save as JSON
                ed.setValue(nv.getValue().toString());
                elementDataDao.save(ed);
            }
        }

    }

    private void createChangeManagementImmediate(Schedule schedule, OptimizedScheduleMessage scheduleMessage,
                    Map<String, List<ElementInfo>> groups) throws CMSException, JsonProcessingException {
        SchedulingData schedulingInfo = scheduleMessage.getSchedulingData();
        for (String groupId : groups.keySet()) {
            ChangeManagementGroup cmg = new ChangeManagementGroup();
            cmg.setUuid(UUID.randomUUID());
            cmg.setSchedulesUuid(schedule.getUuid());
            cmg.setGroupId(groupId);
            int duration = schedulingInfo.getNormalDurationInSeconds();
            int backout = schedulingInfo.getAdditionalDurationInSeconds();
            cmg.setStartTimeMillis(System.currentTimeMillis());
            cmg.setFinishTimeMillis(System.currentTimeMillis() + ((duration * 1000) + (backout * 1000)));
            cmg.setNormalDurationInSecs(duration);
            cmg.setAdditionalDurationInSecs(backout);
            cmGroupDao.save(cmg);
            for (ElementInfo element : groups.get(groupId)) {
                ChangeManagementSchedule cms = new ChangeManagementSchedule();
                cms.setUuid(UUID.randomUUID());
                cms.setChangeManagementGroupUuid(cmg.getUuid());
                cms.setVnfName(element.getElementId());
                cms.setRequest(element.getRequest().toString());
                cms.setStatus(CMSStatusEnum.PendingApproval.toString());
                cmScheduleDao.save(cms);
            }
            schedule.setStatus(CMSStatusEnum.PendingApproval.toString());
            scheduleDao.save(schedule);
        }
    }

    protected void deleteChangeManagement(Schedule schedule) throws CMSException {
        List<ChangeManagementGroup> cmgs = cmGroupDao.findBySchedulesId(schedule.getUuid());

        for (ChangeManagementGroup cmg : cmgs) {
            List<ChangeManagementSchedule> schedules = cmScheduleDao.findByChangeManagementGroupId(cmg.getUuid());
            for (ChangeManagementSchedule s : schedules) {
                CMSStatusEnum currentState = CMSStatusEnum.Completed.fromString(s.getStatus());
                switch (currentState) {
                    case Scheduled:
                        if (s.getTmChangeId() != null && !s.getTmChangeId().equals("")) {
                            tmClient.cancelTicket(schedule, s, s.getTmChangeId());
                        }
                        s.setStatus(CMSStatusEnum.Cancelled.toString());
                        break;
                    case Triggered:
                        // Too late...
                        break;
                    default:
                        s.setStatus(CMSStatusEnum.Deleted.toString());
                }
                cmScheduleDao.save(s);
            }
        }

    }

    //
    // Marshall commonData into DB DomainData
    // No validation.
    //
    private List<DomainData> marshallDomainData(OptimizedScheduleMessage scheduleMessage) throws CMSException {
        List<NameValue> domainData = scheduleMessage.getCommonData();
        List<DomainData> domainDataList = new ArrayList<DomainData>();
        for (NameValue nameValue : domainData) {
            DomainData dd = new DomainData();
            dd.setName(nameValue.getName());
            Object obj = nameValue.getValue();
            String objString = obj.toString();
            // TODO: Store as json
            dd.setValue(objString);
            domainDataList.add(dd);
        }
        return domainDataList;
    }


    protected void processApproveScheduleRequest(Schedule sch, ApprovalMessage approval, List<DomainData> domainData)
                    throws CMSException {
        sch = scheduleDao.lockOne(sch.getUuid());
        String domain = DomainsEnum.ChangeManagement.toString();
        processApproval(sch, domain, approval);
        if (sch.getStatus().equals(CMSStatusEnum.Accepted.toString())) {
            openTickets(sch, domainData);
        }
        if (sch.getStatus().equals(CMSStatusEnum.Rejected.toString())) {
            updateChangeManagementSchedules(sch, CMSStatusEnum.ApprovalRejected);
        }
    }

    private void openTickets(Schedule sch, List<DomainData> domainData) throws CMSException {
        debug.debug("Entered openTickets scheduleId=" + sch.getScheduleId());

        Integer maxvnfsperticket = env.getProperty("tm.vnfs.per.ticket", Integer.class, 1);

        List<ChangeManagementGroup> groups = cmGroupDao.findBySchedulesId(sch.getUuid());
        for (ChangeManagementGroup group : groups) {

            List<ChangeManagementSchedule> schedules = cmScheduleDao.findByChangeManagementGroupId(group.getUuid());
            List<List<ChangeManagementSchedule>> ticketList = new ArrayList<List<ChangeManagementSchedule>>();
            List<ChangeManagementSchedule> current = null;
            for (ChangeManagementSchedule cms : schedules) {
                if (current == null || current.size() == maxvnfsperticket) {
                    current = new ArrayList<ChangeManagementSchedule>();
                    ticketList.add(current);
                }
                current.add(cms);
            }
            for (List<ChangeManagementSchedule> list : ticketList) {
                openTicketForList(sch, group, list, domainData);
            }
        }
        debug.debug("Exited openTickets scheduleId=" + sch.getScheduleId());
    }

    private void openTicketForList(Schedule schedule, ChangeManagementGroup group, List<ChangeManagementSchedule> list,
                    List<DomainData> domainData) throws CMSException {
        List<String> vnfNames = new ArrayList<>();
        for (ChangeManagementSchedule cms : list) {
            vnfNames.add(cms.getVnfName());
        }

        debug.debug("Calling createChangeTicket scheduleId=" + schedule.getScheduleId() + ", group="
                        + group.getGroupId(), ", vnfNames=" + vnfNames);
        String changeId = tmClient.createChangeTicket(schedule, group, vnfNames, domainData);

        // Pre-approve the ticket
        for (ChangeManagementSchedule cms : list) {
            cms.getTmApprovalStatus();
            cms.setTmChangeId(changeId);
            cms.setTmApprovalStatus(TmApprovalStatusEnum.Approved.toString());
            // cms.setStatus(CMSStatusEnum.PendingApproval.toString());
            if (cms.getStartTimeMillis() == null) {
                cms.setStatus(CMSStatusEnum.ScheduledImmediate.toString());
            }
            else {
                cms.setStatus(CMSStatusEnum.Scheduled.toString());
            }
            cmScheduleDao.save(cms);
        }
        schedule.setStatus(CMSStatusEnum.Scheduled.toString());
        scheduleDao.save(schedule);
    }

    private void updateChangeManagementSchedules(Schedule sch, CMSStatusEnum approvalrejected) {
        debug.debug("Entered updateChangeManagementSchedules");
        List<ChangeManagementGroup> groups = cmGroupDao.findBySchedulesId(sch.getUuid());
        for (ChangeManagementGroup group : groups) {
            List<ChangeManagementSchedule> schedules = cmScheduleDao.findByChangeManagementGroupId(group.getUuid());
            for (ChangeManagementSchedule schedule : schedules) {
                schedule.setStatus(approvalrejected.toString());
                cmScheduleDao.save(schedule);
            }
        }
        debug.debug("Exited updateChangeManagementSchedules");
    }

}
