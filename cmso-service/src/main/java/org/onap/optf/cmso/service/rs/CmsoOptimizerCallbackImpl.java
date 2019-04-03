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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.onap.optf.cmso.common.CMSStatusEnum;
import org.onap.optf.cmso.common.DomainsEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.exceptions.CMSException;
import org.onap.optf.cmso.common.exceptions.CMSNotFoundException;
import org.onap.optf.cmso.model.ChangeManagementGroup;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ChangeManagementChangeWindowDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementDetailDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.onap.optf.cmso.optimizer.bean.CMOptimizerResponse;
import org.onap.optf.cmso.optimizer.bean.CMSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * The Class CmsoOptimizerCallbackImpl.
 */
@Controller
public class CmsoOptimizerCallbackImpl extends BaseSchedulerServiceImpl implements CmsoOptimizerCallback {
    private static EELFLogger log = EELFManager.getInstance().getLogger(CmsoOptimizerCallbackImpl.class);
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();

    @Context
    UriInfo uri;

    @Context
    HttpServletRequest request;


    @Autowired
    ChangeManagementScheduleDAO cmScheduleDao;

    @Autowired
    ChangeManagementGroupDAO cmGroupDao;

    @Autowired
    ChangeManagementChangeWindowDAO cmChangeWindowDao;

    @Autowired
    ChangeManagementDetailDAO cmDetailsDaoO;

    /**
     * Sniro callback.
     *
     * @param apiVersion the api version
     * @param sniroResponse the sniro response
     * @return the response
     */
    @Override
    @Transactional
    public Response sniroCallback(String apiVersion, CMOptimizerResponse sniroResponse) {
        Response response = null;
        log.info(LogMessages.PROCESS_OPTIMIZER_CALLBACK, "Received", request.getRemoteAddr(), "");
        log.info(LogMessages.OPTIMIZER_REQUEST, "Callback received", sniroResponse.getTransactionId(),
                uri.getAbsolutePath().toString());
        try {
            // Note that transaction ID and schedule ID are currently the same value.

            String transactionId = sniroResponse.getTransactionId();

            // Synchronize this with transaction that scheduled the SNIRO optimization
            // to ensure status updates are properly ordered.
            // This is necessary only in the race condition where SNIRO callback comes
            // before the SNIRO response is processed and the scheduling transaction is
            // still in flight.
            // Note that this may happen in loopback mode, but is not likely to happen with
            // real SNIRO unless SNIRO changes to be synchronous and the callback comes before
            // the response.
            // If this lock times out, the schedule will remain in 'Optimization In
            // Progress' and never complete.
            Schedule schedule = scheduleDao.lockOneByTransactionId(transactionId);

            if (schedule == null) {
                throw new CMSNotFoundException(DomainsEnum.ChangeManagement.toString(),
                        "(OptimizerTransactionID=" + transactionId + ")");

            }
            CMSStatusEnum status = CMSStatusEnum.PendingApproval.fromString(schedule.getStatus());
            debug.debug("Status at time of SNIRO callback is " + status.toString());
            switch (status) {
                // PendingSchedule may be a valid status in the cases where SNIRO async call
                // returns before
                // We have committed the OptimizationInProgress status
                // The dispatch logic ensures that we only every dispatch once.
                case OptimizationInProgress:
                    processSniroResponse(sniroResponse, schedule);
                    scheduleDao.save(schedule);
                    response = Response.ok().build();
                    break;
                default:
                    throw new CMSException(Status.PRECONDITION_FAILED, LogMessages.OPTIMIZER_CALLBACK_STATE_ERROR,
                            CMSStatusEnum.OptimizationInProgress.toString(), schedule.getStatus().toString());
            }
        } catch (CMSException e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.serverError().entity(e.getMessage()).build();
        }
        return response;
    }

    private void processSniroResponse(CMOptimizerResponse sniroResponse, Schedule schedule) {
        try {
            schedule.setOptimizerReturnDateTimeMillis(System.currentTimeMillis());
            schedule.setOptimizerStatus(sniroResponse.getRequestState());
            schedule.setOptimizerMessage(sniroResponse.getDescription());
            String scheduleId = sniroResponse.getScheduleId();
            ObjectMapper om = new ObjectMapper();
            CMSchedule[] scheduleArray = sniroResponse.getSchedule();
            if (scheduleArray != null && scheduleArray.length > 0) {
                String scheduleString = om.writeValueAsString(scheduleArray);
                schedule.setSchedule(scheduleString);
                log.debug("scheduleId={0} schedule={1}", scheduleId, scheduleString);
                for (CMSchedule sniroSchedule : sniroResponse.getSchedule()) {
                    String groupId = sniroSchedule.getGroupId();
                    DateTime finishTime = convertDate(sniroSchedule.getFinishTime(), "finishTime");
                    DateTime startTime = convertDate(sniroSchedule.getStartTime(), "startTime");
                    ChangeManagementGroup group = cmGroupDao.findOneBySchedulesIdGroupId(schedule.getUuid(), groupId);
                    if (group == null) {
                        throw new CMSException(Status.PRECONDITION_FAILED,
                                LogMessages.CHANGE_MANAGEMENT_GROUP_NOT_FOUND, schedule.getScheduleId(), groupId);
                    }
                    group.setStartTimeMillis(startTime.getMillis());
                    group.setFinishTimeMillis(finishTime.getMillis());
                    DateTime latestInstanceStartTime =
                                    convertDate(sniroSchedule.getLatestInstanceStartTime(), "latestInstanceStartTime");
                    group.setLastInstanceStartTimeMillis(latestInstanceStartTime.getMillis());
                    cmGroupDao.save(group);
                    long totalDuration =
                            (group.getAdditionalDurationInSecs() + group.getNormalDurationInSecs()) * 1000L;
                    Map<String, Map<String, Long>> startAndFinishTimeMap = new HashMap<String, Map<String, Long>>();
                    makeMap(startTime.getMillis(), latestInstanceStartTime.getMillis(), group.getConcurrencyLimit(),
                            totalDuration, sniroSchedule.getNode(), startAndFinishTimeMap);
                    for (String node : sniroSchedule.getNode()) {
                        processNode(schedule, group, node, startAndFinishTimeMap);
                    }
                }
                schedule.setStatus(CMSStatusEnum.PendingApproval.toString());
            } else {
                debug.debug("scheduleId={0} schedule=null status={1} ", scheduleId, schedule.getOptimizerStatus());
                schedule.setStatus(CMSStatusEnum.OptimizationFailed.toString());
            }
        } catch (CMSException e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            schedule.setStatus(CMSStatusEnum.OptimizationFailed.toString());
            schedule.setOptimizerStatus(e.getStatus().toString());
            schedule.setOptimizerMessage(e.getLocalizedMessage());
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            schedule.setStatus(CMSStatusEnum.OptimizationFailed.toString());
            schedule.setOptimizerStatus("Exception");
            schedule.setOptimizerMessage(e.getLocalizedMessage());
        }
    }

    /**
     * Make map.
     *
     * @param startTime the start time
     * @param latestInstanceStartTime the latest instance start time
     * @param concurrencyLimit the concurrency limit
     * @param totalDuration the total duration
     * @param nodeList the node list
     * @param startAndFinishTimeMap the start and finish time map
     * @throws CMSException the CMS exception
     */
    public static void makeMap(Long startTime, Long latestInstanceStartTime, int concurrencyLimit, long totalDuration,
            List<String> nodeList, Map<String, Map<String, Long>> startAndFinishTimeMap) throws CMSException {
        Long nextStartTime = null;
        Long nextFinishTime = null;
        for (int nodeNumber = 0; nodeNumber < nodeList.size(); nodeNumber++) {
            if (nodeNumber % concurrencyLimit == 0) {
                if (nodeNumber == 0) {
                    nextStartTime = startTime;
                }
                else {
                    nextStartTime = nextStartTime + totalDuration;
                }
                if (nextStartTime > latestInstanceStartTime) {
                    throw new CMSException(Status.BAD_REQUEST, LogMessages.UNABLE_TO_ALLOCATE_VNF_TIMESLOTS,
                            startTime.toString(), latestInstanceStartTime.toString(), String.valueOf(totalDuration),
                            String.valueOf(concurrencyLimit), String.valueOf(nodeList.size()));
                }
                nextFinishTime = nextStartTime + totalDuration;
            }
            Map<String, Long> map = new HashMap<String, Long>();
            map.put("startTime", nextStartTime);
            map.put("finishTime", nextFinishTime);
            String node = nodeList.get(nodeNumber);
            startAndFinishTimeMap.put(node, map);
        }

    }

    private void processNode(Schedule schedule, ChangeManagementGroup group, String node,
            Map<String, Map<String, Long>> startAndFinishTimeMap) throws CMSException {
        Map<String, Long> map = startAndFinishTimeMap.get(node);
        ChangeManagementSchedule detail = cmScheduleDao.findOneByGroupUuidAndVnfName(group.getUuid(), node);
        if (detail == null) {
            throw new CMSException(Status.NOT_FOUND, LogMessages.UNABLE_TO_LOCATE_SCHEDULE_DETAIL,
                    schedule.getScheduleId(), group.getGroupId(), node);
        }
        detail.setStartTimeMillis(map.get("startTime"));
        detail.setFinishTimeMillis(map.get("finishTime"));
        detail.setVnfId("");
        detail.setStatus(CMSStatusEnum.PendingApproval.toString());
        cmScheduleDao.save(detail);
    }

    /**
     * Convert date.
     *
     * @param utcDate the utc date
     * @param attrName the attr name
     * @return the date time
     * @throws CMSException the CMS exception
     */
    public static DateTime convertDate(String utcDate, String attrName) throws CMSException {
        try {
            DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC().parseDateTime(utcDate);
            if (dateTime != null) {
                return dateTime;
            }
        } catch (Exception e) {
            debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, attrName, utcDate);
    }

    /**
     * Convert ISO date.
     *
     * @param utcDate the utc date
     * @param attrName the attr name
     * @return the date time
     * @throws CMSException the CMS exception
     */
    public static DateTime convertIsoDate(String utcDate, String attrName) throws CMSException {
        try {
            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(utcDate);
            if (dateTime != null) {
                return dateTime;
            }
        } catch (Exception e) {
            debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        throw new CMSException(Status.BAD_REQUEST, LogMessages.INVALID_ATTRIBUTE, attrName, utcDate);
    }

}
