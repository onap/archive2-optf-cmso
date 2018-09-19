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

package org.onap.optf.cmso.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.att.eelf.configuration.EELFManager;
import com.att.eelf.i18n.EELFResolvableErrorEnum;
import com.att.eelf.i18n.EELFResourceManager;

public enum LogMessages implements EELFResolvableErrorEnum {

    // Let developer provide the initial properties here.
    // We can merge them to logmessages.properties when we need to.
    SEARCH_SCHEDULE_REQUEST_DETAILS("Search Schedule Request Details {0} from {1}: {2}"), SEARCH_SCHEDULE_REQUEST(
            "Search Schedule Request {0} from {1}: {2} : {3}"), CREATE_SCHEDULE_REQUEST(
                    "Create Schedule Request {0} from {1}: {2} : {3}"), DELETE_SCHEDULE_REQUEST(
                            "Delete Schedule Request {0} from {1}: {2} : {3}"), GET_SCHEDULE_REQUEST_INFO(
                                    "Get Schedule Request Info {0} from {1}: {2} : {3}"), PROCESS_OPTIMIZER_CALLBACK(
                                            "Change management optimizer callback {0} from {1}: {2} "), APPROVE_SCHEDULE_REQUEST(
                                                    "Approve Schedule Request {0} from {1}: {2} : {3}"), SCHEDULE_ALREADY_EXISTS(
                                                            "Schedule already exists domain={0} schedule id={1}"), SCHEDULE_NOT_FOUND(
                                                                    "Schedule not found domain={0} schedule id={1}"), INVALID_ATTRIBUTE(
                                                                            "Invalid attribute {0}={1}"), MISSING_REQUIRED_ATTRIBUTE(
                                                                                    "Missing required attribute '{0}'"), INVALID_REQUEST(
                                                                                            "The input data structure is incorrect"), REQUEST_TIMED_OUT(
                                                                                                    "Request timed out."), UNEXPECTED_EXCEPTION(
                                                                                                            "Unexpected exception encountered during processing. Please contact support : {0}"), AUTHORIZATION_FAILED(
                                                                                                                    "Authorization Failed"), UNDEFINED_DOMAIN_DATA_ATTRIBUTE(
                                                                                                                            "Domain data attribute not defined domain={0} name={1} value={2}"), UNDEFINED_FILTER_ATTRIBUTE(
                                                                                                                                    "Undefined filter attribute {0}"), INVALID_DATE_FILTER(
                                                                                                                                            "Invalid date filter provided {0}=(1}"), OPTIMIZER_QUARTZ_JOB(
                                                                                                                                                    "Quartz scheduling of OptimizerQuartzJob: {0}"), OPTIMIZER_EXCEPTION(
                                                                                                                                                            "Exception making client call to optimizer {0}"), OPTIMIZER_CALLBACK_STATE_ERROR(
                                                                                                                                                                    "Optimizer callback on schedule in invalid state. Should be {0} but was {1}."), CHANGE_MANAGEMENT_GROUP_NOT_FOUND(
                                                                                                                                                                            "ChangeManagementGroup not found on optimizer callback scheduleId={0} groupId={1}"), UNABLE_TO_ALLOCATE_VNF_TIMESLOTS(
                                                                                                                                                                                    "Unable to allocate VNF timeslots with Optimizer results startTime={0}, latestStartTime={1}, totalDuration={2}, concurrency={3} nvfs={4}"), UNABLE_TO_LOCATE_SCHEDULE_DETAIL(
                                                                                                                                                                                            "Unable to locate ChangeManagementSchedule for VNF. scheduleId={0}, groupId={1}, vnfName={2}"), CM_JOB(
                                                                                                                                                                                                    "Quartz scheduling of CmJob: {0}"), CM_QUARTZ_JOB(
                                                                                                                                                                                                            "Quartz scheduling of CmQuartzJob: {0}"), NOT_PENDING_APPROVAL(
                                                                                                                                                                                                                    "Approval request received for schedule that is not in Pending Approval state: domain={0} scheduleId={1} state={3}"), SCHEDULE_PAST_DUE(
                                                                                                                                                                                                                            "Attempt to dispatch an event that is Past due scheduleId={0}, vnf={1}, now={2}, startTime={3}"), MSO_POLLING_MISSING_SCHEDULE(
                                                                                                                                                                                                                                    "Attempt to poll MSO for request id {1} for missing ChangeManagementSchedule id={0}"), MSO_STATUS_JOB(
                                                                                                                                                                                                                                            "Polling MSO {0} for requestId={1} for id={2}"), UNEXPECTED_RESPONSE(
                                                                                                                                                                                                                                                    "Unexpected response from {0} HTTP Status={1} : {2}"), SCHEDULE_STATUS_JOB(
                                                                                                                                                                                                                                                            "Quartz scheduling of ScheduleStatusJob: {0}"), CM_TICKET_NOT_APPROVED(
                                                                                                                                                                                                                                                                    "Attempt to dispatch a change management event that has no TM Ticket approved. scheduleId={0} VNF Name={1} TM ChangeId={2} Status={3} Approval Status={4}"), MULTIPLE_GROUPS_NOT_SUPPORTED(
                                                                                                                                                                                                                                                                            "Multiple groups not supported on immediate requests"), TM_CREATE_CHANGE_RECORD(
                                                                                                                                                                                                                                                                                    "TM Create Change Record:{0} : Schedule ID: {1}"), TM_LIST(
                                                                                                                                                                                                                                                                                            "TM list:{0} : URL : {1}"), TM_API(
                                                                                                                                                                                                                                                                                                    "TM API Call: URL : {0}"), UNABLE_TO_CREATE_CHANGE_TICKET(
                                                                                                                                                                                                                                                                                                            "Unable to create change ticket in TM: Schedule ID: {0} : Reason : {1}"), TM_UPDATE_CHECKLIST(
                                                                                                                                                                                                                                                                                                                    "TM Fetch Checklist:{0} : Schedule ID: {1} : Change Id : {2} : URL : {3}"), OPTIMIZER_REQUEST(
                                                                                                                                                                                                                                                                                                                            "Optimi Request:{0} : Schedule ID: {1} : URL : {2}"), TM_CLOSE_CHANGE_RECORD(
                                                                                                                                                                                                                                                                                                                                    "TM Close Change Record:{0} : Schedule ID: {1} : Change Id : {2}"), UNABLE_TO_CLOSE_CHANGE_TICKET(
                                                                                                                                                                                                                                                                                                                                            "Unable to close change ticket in TM:  Schedule ID: {0} : changeid: {1} :  Reason: {2}"), CANNOT_CANCEL_IN_PROGRESS(
                                                                                                                                                                                                                                                                                                                                                    "Cannot delete/cancel a schedule with events in progress."), UNABLE_TO_PARSE_SCHEDULING_INFO(
                                                                                                                                                                                                                                                                                                                                                            "Cannot parse scheduling info."), UNABLE_TO_LOCATE_CHANGE_RECORD(
                                                                                                                                                                                                                                                                                                                                                                    "Unable to locate TM change record {2} to check status before displacth of {1} for schedulId={0}"), INVALID_CHANGE_WINDOW(
                                                                                                                                                                                                                                                                                                                                                                            "Change window end time {1} must be after start time {0}"), NODE_LIST_CONTAINS_EMTPY_NODE(
                                                                                                                                                                                                                                                                                                                                                                                    "vnfDetails node list contains at least one empty node."), SO_API(
                                                                                                                                                                                                                                                                                                                                                                                            "SO Poll Request {0}"), EXPECTED_EXCEPTION(
                                                                                                                                                                                                                                                                                                                                                                                                    "Expected exception encountered during processing. Make Sonar happy: {0}"), TM_UPDATE_CHANGE_RECORD(
                                                                                                                                                                                                                                                                                                                                                                                                            "TM Update Change Record:{0} : Schedule ID: {1} : Change Id : {2} : URL : {3}"), UNABLE_TO_UPDATE_CHANGE_TICKET(
                                                                                                                                                                                                                                                                                                                                                                                                                    "Unable to update change ticket in TM: Schedule ID: {0} : changeid: {1} :  Reason: {2}"),;

    private final String defaultId;
    private final String defaultMessage;
    private final String defaultResolution;
    private final String defaultAction;

    private LogMessages(String message) {
        defaultMessage = message;
        this.defaultId = this.name();
        this.defaultResolution = "No resolution needed";
        this.defaultAction = "No action is required";
    }

    private LogMessages(String message, String id, String resolution, String action) {
        defaultMessage = message;
        this.defaultId = id;
        this.defaultResolution = resolution;
        this.defaultAction = action;
    }

    static {
        EELFResourceManager.loadMessageBundle("logmessages");
    }

    public String genProperties() {
        // Use this to regenerate properties file. The desire to change messages without
        // updating code is
        // well understood, but the developer should be able to code the defaults
        // without having to update 2 different files and
        // get it wrong.
        StringBuilder sb = new StringBuilder();
        sb.append("# Generated from ").append(this.getClass().getName()).append("\n");
        for (LogMessages lm : values()) {
            sb.append(lm.name());
            sb.append(" ").append(lm.defaultId);
            sb.append("|").append(lm.defaultMessage);
            sb.append("|").append(lm.defaultResolution);
            sb.append("|").append(lm.defaultAction);
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String argv[]) {
        System.out.println(LogMessages.CREATE_SCHEDULE_REQUEST.genProperties());
        try {
            Files.write(Paths.get("src/main/resources/logmessages.properties"),
                    LogMessages.CREATE_SCHEDULE_REQUEST.genProperties().getBytes());
        } catch (IOException e) {
            EELFManager.getInstance().getDebugLogger().debug("Failed to update properties file.", e);

        }

    }

}
