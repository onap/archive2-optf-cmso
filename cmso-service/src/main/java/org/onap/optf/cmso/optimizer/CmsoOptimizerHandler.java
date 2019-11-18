/*
 * Copyright © 2019 AT&T Intellectual Property.
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
 ******************************************************************************/

package org.onap.optf.cmso.optimizer;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response.Status;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.CmsoStatusEnum;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.exceptions.CmsoException;
import org.onap.optf.cmso.model.ChangeManagementGroup;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.model.dao.ChangeManagementChangeWindowDao;
import org.onap.optf.cmso.model.dao.ChangeManagementDetailDao;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDao;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDao;
import org.onap.optf.cmso.model.dao.ScheduleDao;
import org.onap.optf.cmso.optimizer.model.OptimizerResponse;
import org.onap.optf.cmso.optimizer.model.OptimizerScheduleInfo;
import org.onap.optf.cmso.optimizer.model.ScheduledElement;
import org.onap.optf.cmso.optimizer.model.UnScheduledElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The Class CmsoOptimizerHandler.
 */
@Component
public class CmsoOptimizerHandler {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    @Autowired
    ChangeManagementScheduleDao cmScheduleDao;

    @Autowired
    ScheduleDao scheduleDao;

    @Autowired
    ChangeManagementGroupDao cmGroupDao;

    @Autowired
    ChangeManagementChangeWindowDao cmChangeWindowDao;

    @Autowired
    ChangeManagementDetailDao cmDetailsDao;

    /**
     * Handle optimizer response.
     *
     * @param response the response
     * @param schedule the schedule
     */
    public void handleOptimizerResponse(OptimizerResponse response, Schedule schedule) {
        try {
            // Note that transaction ID and schedule ID are currently the same value.

            String id = response.getRequestId();
            CmsoStatusEnum status = CmsoStatusEnum.PendingApproval.fromString(schedule.getStatus());
            debug.debug("Status at time of optimizer status is " + status.toString() + " for " + id);
            switch (status) {
                // PendingSchedule may be a valid status in the cases where SNIRO async call
                // returns before
                // We have committed the OptimizationInProgress status
                // The dispatch logic ensures that we only every dispatch once.
                case OptimizationInProgress:
                    processResponse(response, schedule);
                    scheduleDao.save(schedule);
                    break;
                default:
                    throw new CmsoException(Status.PRECONDITION_FAILED, LogMessages.OPTIMIZER_CALLBACK_STATE_ERROR,
                                    CmsoStatusEnum.OptimizationInProgress.toString(), schedule.getStatus().toString());
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
    }

    private void processResponse(OptimizerResponse response, Schedule schedule) {
        try {
            schedule.setOptimizerReturnDateTimeMillis(System.currentTimeMillis());
            schedule.setOptimizerStatus(response.getStatus().toString());
            schedule.setOptimizerMessage(response.getErrorMessage());
            switch (response.getStatus()) {
                case COMPLETED:
                    saveSchedules(response, schedule);
                    break;
                case FAILED:
                    schedule.setStatus(CmsoStatusEnum.OptimizationFailed.toString());
                    break;
                case PENDING_OPTIMIZER:
                case PENDING_TICKETS:
                case PENDING_TOPOLOGY:
                    // Leave status as In progress
                    break;
                default:
                    break;
            }
            scheduleDao.save(schedule);
        } catch (CmsoException e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            schedule.setStatus(CmsoStatusEnum.OptimizationFailed.toString());
            schedule.setOptimizerStatus(e.getStatus().toString());
            schedule.setOptimizerMessage(e.getLocalizedMessage());
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            schedule.setStatus(CmsoStatusEnum.OptimizationFailed.toString());
            schedule.setOptimizerStatus("Exception");
            schedule.setOptimizerMessage(e.getLocalizedMessage());
        }
    }

    private void saveSchedules(OptimizerResponse response, Schedule schedule) {

        // TODO: Persist the list of schedules in the DB

        // For Dublin we choose the best schedule.
        // and only request Accept on that one.
        // FOr the future, the user will get to choose one of the persisted schedules
        List<OptimizerScheduleInfo> schedules = response.getSchedules();

        OptimizerScheduleInfo osi = chooseSchedule(schedules);
        if (osi == null) {
            schedule.setStatus(CmsoStatusEnum.OptimizationFailed.toString());
            schedule.setOptimizerMessage("No schedules returned for COMPLETED status");
            return;
        }
        if (osi.getScheduledElements().size() == 0) {
            schedule.setStatus(CmsoStatusEnum.OptimizationFailed.toString());
            schedule.setOptimizerMessage("No elements scheduled for COMPLETED status");
            return;
        }

        List<ChangeManagementGroup> groups = cmGroupDao.findBySchedulesId(schedule.getUuid());
        Map<String, ChangeManagementGroup> updatedGroups = new HashMap<>();

        for (ScheduledElement element : osi.getScheduledElements()) {
            updateGroup(element, groups, updatedGroups);
            String groupId = element.getGroupId();
            String vnfName = element.getElementId();
            ChangeManagementSchedule cms =
                            cmScheduleDao.findOneByScheduleUuidGroupIdAndVnfName(schedule.getUuid(), groupId, vnfName);
            cms.setStartTimeMillis(element.getStartTime().getTime());
            cms.setFinishTimeMillis(element.getEndTime().getTime());
            cms.setStatus(CmsoStatusEnum.PendingApproval.toString());
            cmScheduleDao.save(cms);
        }
        if (osi.getUnScheduledElements() != null) {
            for (UnScheduledElement element : osi.getUnScheduledElements()) {
                String groupId = element.getGroupId();
                String vnfName = element.getElementId();
                ChangeManagementSchedule cms = cmScheduleDao.findOneByScheduleUuidGroupIdAndVnfName(schedule.getUuid(),
                                groupId, vnfName);
                cms.setStatus(CmsoStatusEnum.NotScheduled.toString());
                cmScheduleDao.save(cms);

            }
        }

        // Save any changes to the groups
        for (ChangeManagementGroup cmg : updatedGroups.values()) {
            cmGroupDao.save(cmg);
        }
        schedule.setStatus(CmsoStatusEnum.PendingApproval.toString());
    }

    private void updateGroup(ScheduledElement element, List<ChangeManagementGroup> groups,
                    Map<String, ChangeManagementGroup> updatedGroups) {

        // For Dublin the contents of CMG are not functional
        // since were are doing individual scheduling.
        // We log the not found exception, but do not
        // throw it at this time.
        try {
            ChangeManagementGroup cmg = updatedGroups.get(element.getGroupId());
            if (cmg == null) {
                for (ChangeManagementGroup group : groups) {
                    if (group.getGroupId().equals(element.getGroupId())) {
                        cmg = group;
                        break;
                    }
                }
            }
            if (cmg == null) {
                throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.MISSING_VALID_GROUP_FOR_ELEMENT,
                                element.getElementId());
            }
            Long elementStartTime = element.getStartTime().getTime();
            Long elementFinishTime = element.getEndTime().getTime();
            if (cmg.getStartTimeMillis() == null || cmg.getStartTimeMillis() > elementStartTime) {
                cmg.setStartTimeMillis(elementStartTime);
                updatedGroups.put(cmg.getGroupId(), cmg);
            }
            if (cmg.getFinishTime() == null || cmg.getFinishTimeMillis() < elementFinishTime) {
                cmg.setFinishTimeMillis(elementFinishTime);
                updatedGroups.put(cmg.getGroupId(), cmg);
            }
            if (cmg.getLastInstanceStartTimeMillis() == null
                            || cmg.getLastInstanceStartTimeMillis() < elementStartTime) {
                cmg.setLastInstanceStartTimeMillis(elementStartTime);
                updatedGroups.put(cmg.getGroupId(), cmg);
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
    }

    private OptimizerScheduleInfo chooseSchedule(List<OptimizerScheduleInfo> schedules) {
        // The most scheduled elements is the priority
        //
        OptimizerScheduleInfo chosenOne = null;
        for (OptimizerScheduleInfo osi : schedules) {
            if (chosenOne == null || osi.getScheduledElements().size() > chosenOne.getScheduledElements().size()) {
                chosenOne = osi;
            } else {
                // Same number of scheduled elements.
                // What is the policy.
                if (betterSchedule(osi, chosenOne)) {
                    chosenOne = osi;
                }
            }
        }
        return chosenOne;
    }

    private boolean betterSchedule(OptimizerScheduleInfo osi, OptimizerScheduleInfo chosenOne) {
        // TODO Create a more sophisticated choosing process -
        return true;
    }
}
