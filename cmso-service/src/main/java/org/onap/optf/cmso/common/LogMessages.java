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

package org.onap.optf.cmso.common;

import com.att.eelf.configuration.EELFManager;
import com.att.eelf.i18n.EELFResourceManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.ws.rs.core.Response.Status;
import org.apache.logging.log4j.Level;
import org.onap.observations.ObservationInterface;

/**
 * The Enum LogMessages.
 */
public enum LogMessages implements ObservationInterface {

    SEARCH_SCHEDULE_REQUEST_DETAILS("Search Schedule Request Details {0} from {1}: {2}", Status.OK, Level.INFO),
    SEARCH_SCHEDULE_REQUEST("Search Schedule Request {0} from {1}: {2} : {3}", Status.OK, Level.INFO),
    CREATE_SCHEDULE_REQUEST("Create Schedule Request {0} from {1}: {2} : {3}", Status.OK, Level.INFO),
    DELETE_SCHEDULE_REQUEST("Delete Schedule Request {0} from {1}: {2} : {3}", Status.OK, Level.INFO),
    GET_SCHEDULE_REQUEST_INFO("Get Schedule Request Info {0} from {1}: {2} : {3}", Status.OK, Level.INFO),
    PROCESS_OPTIMIZER_CALLBACK("Change management optimizer callback {0} from {1}: {2} ", Status.OK, Level.INFO),
    APPROVE_SCHEDULE_REQUEST("Approve Schedule Request {0} from {1}: {2} : {3}", Status.OK, Level.INFO),
    SCHEDULE_ALREADY_EXISTS("Schedule already exists domain={0} schedule id={1}", Status.OK, Level.INFO),
    SCHEDULE_NOT_FOUND("Schedule not found domain={0} schedule id={1}", Status.BAD_REQUEST, Level.INFO),
    INVALID_ATTRIBUTE("Invalid attribute {0}={1}", Status.BAD_REQUEST, Level.INFO),
    MISSING_REQUIRED_ATTRIBUTE("Missing required attribute '{0}'", Status.BAD_REQUEST, Level.INFO),
    INVALID_REQUEST("The input data structure is incorrect", Status.BAD_REQUEST, Level.INFO),
    REQUEST_TIMED_OUT("Request timed out.", Status.INTERNAL_SERVER_ERROR, Level.ERROR),
    UNEXPECTED_EXCEPTION("Unexpected exception encountered during processing. Please contact support : {0}",
                    Status.INTERNAL_SERVER_ERROR, Level.ERROR),
    UNDEFINED_DOMAIN_DATA_ATTRIBUTE("Domain data attribute not defined domain={0} name={1} value={2}",
                    Status.BAD_REQUEST, Level.INFO),
    UNDEFINED_FILTER_ATTRIBUTE("Undefined filter attribute {0}", Status.BAD_REQUEST, Level.INFO),
    INVALID_DATE_FILTER("Invalid date filter provided {0}=(1}", Status.BAD_REQUEST, Level.INFO),
    OPTIMIZER_QUARTZ_JOB("Quartz scheduling of OptimizerQuartzJob: {0}", Status.OK, Level.INFO),
    OPTIMIZER_EXCEPTION("Exception making client call to optimizer {0}", Status.INTERNAL_SERVER_ERROR, Level.ERROR),
    OPTIMIZER_CALLBACK_STATE_ERROR("Optimizer callback on schedule in invalid state. Should be {0} but was {1}.",
                    Status.INTERNAL_SERVER_ERROR, Level.ERROR),
    CHANGE_MANAGEMENT_GROUP_NOT_FOUND(
                    "ChangeManagementGroup not found on optimizer callback scheduleId={0} groupId={1}",
                    Status.NOT_FOUND, Level.INFO),
    INCOMING_MESSAGE("Incoming message method={0} path={1}", Status.OK, Level.INFO, true, false),
    INCOMING_MESSAGE_RESPONSE("Message response method={0} path={1} status={2}", Status.OK, Level.INFO, true, false),
    OUTGOING_MESSAGE("Outgoing message method={0} path={1}", Status.OK, Level.INFO, true, false),
    OUTGOING_MESSAGE_RETURNED("Outgoing message returned method={0} path={1} status={2}", Status.OK, Level.INFO, true,
                    false),

    // TODO: Review the status and level of the remaining enums
    UNABLE_TO_ALLOCATE_VNF_TIMESLOTS(
                    "Unable to allocate VNF timeslots with Optimizer results startTime={0},"
                    + " latestStartTime={1}, totalDuration={2}, concurrency={3} nvfs={4}",
                    Status.OK, Level.INFO),
    UNABLE_TO_LOCATE_SCHEDULE_DETAIL(
                    "Unable to locate ChangeManagementSchedule for VNF. scheduleId={0}, groupId={1}, vnfName={2}",
                    Status.OK, Level.INFO),
    CM_JOB("Quartz scheduling of CmJob: {0}", Status.OK, Level.INFO),
    CM_QUARTZ_JOB("Quartz scheduling of CmQuartzJob: {0}", Status.OK, Level.INFO),
    NOT_PENDING_APPROVAL(
                    "Approval request received for schedule that is not in Pending Approval state:"
                    + " domain={0} scheduleId={1} state={3}",
                    Status.OK, Level.INFO),
    SCHEDULE_PAST_DUE("Attempt to dispatch an event that is Past due scheduleId={0}, vnf={1}, now={2}, startTime={3}",
                    Status.OK, Level.INFO),
    MSO_POLLING_MISSING_SCHEDULE("Attempt to poll MSO for request id {1} for missing ChangeManagementSchedule id={0}",
                    Status.OK, Level.INFO),
    MSO_STATUS_JOB("Polling MSO {0} for requestId={1} for id={2}", Status.OK, Level.INFO),
    UNEXPECTED_RESPONSE("Unexpected response from {0} HTTP Status={1} : {2}", Status.OK, Level.INFO),
    SCHEDULE_STATUS_JOB("Quartz scheduling of ScheduleStatusJob: {0}", Status.OK, Level.INFO),
    CM_TICKET_NOT_APPROVED(
                    "Attempt to dispatch a change management event that has no TM Ticket approved."
                    + "scheduleId={0} VNF Name={1} TM ChangeId={2} Status={3} Approval Status={4}",
                    Status.OK, Level.INFO),
    MULTIPLE_GROUPS_NOT_SUPPORTED("Multiple groups not supported on immediate requests", Status.OK, Level.INFO),
    TM_CREATE_CHANGE_RECORD("TM Create Change Record:{0} : Schedule ID: {1}", Status.OK, Level.INFO),
    TM_LIST("TM list:{0} : URL : {1}", Status.OK, Level.INFO),
    TM_API("TM API Call: URL : {0}", Status.OK, Level.INFO),
    UNABLE_TO_CREATE_CHANGE_TICKET("Unable to create change ticket in TM: Schedule ID: {0} : Reason : {1}", Status.OK,
                    Level.INFO),
    TM_UPDATE_CHECKLIST("TM Fetch Checklist:{0} : Schedule ID: {1} : Change Id : {2} : URL : {3}", Status.OK,
                    Level.INFO),
    OPTIMIZER_REQUEST("Optimi Request:{0} : Schedule ID: {1} : URL : {2}", Status.OK, Level.INFO),
    TM_CLOSE_CHANGE_RECORD("TM Close Change Record:{0} : Schedule ID: {1} : Change Id : {2}", Status.OK, Level.INFO),
    UNABLE_TO_CLOSE_CHANGE_TICKET(
                    "Unable to close change ticket in TM:  Schedule ID: {0} : changeid: {1} :  Reason: {2}", Status.OK,
                    Level.INFO),
    CANNOT_CANCEL_IN_PROGRESS("Cannot delete/cancel a schedule with events in progress.", Status.OK, Level.INFO),
    UNABLE_TO_PARSE_SCHEDULING_INFO("Cannot parse scheduling info.", Status.OK, Level.INFO),
    UNABLE_TO_LOCATE_CHANGE_RECORD(
                    "Unable to locate TM change record {2} to check status before displacth of {1} for schedulId={0}",
                    Status.OK, Level.INFO),
    INVALID_CHANGE_WINDOW("Change window end time {1} must be after start time {0}", Status.OK, Level.INFO),
    NODE_LIST_CONTAINS_EMTPY_NODE("vnfDetails node list contains at least one empty node.", Status.OK, Level.INFO),
    SO_API("SO Poll Request {0}", Status.OK, Level.INFO),
    EXPECTED_EXCEPTION("Expected exception encountered during processing. {0}", Status.OK, Level.INFO),
    TM_UPDATE_CHANGE_RECORD("TM Update Change Record:{0} : Schedule ID: {1} : Change Id : {2} : URL : {3}", Status.OK,
                    Level.INFO),
    UNABLE_TO_UPDATE_CHANGE_TICKET(
                    "Unable to update change ticket in TM: Schedule ID: {0} : changeid: {1} :  Reason: {2}", Status.OK,
                    Level.INFO),
    UNAUTHORIZED("Authorization failed.", Status.FORBIDDEN, Level.INFO),
    UNAUTHENTICATED("Authentication failed.", Status.UNAUTHORIZED, Level.INFO),
    UNRECOGNIZED_MSO_STATUS("Unrecognized status returned by MSO {0}", Status.INTERNAL_SERVER_ERROR, Level.ERROR),
    UNABLE_TO_PARSE_MSO_RESPONSE("Unable to parse status message from MSO {0} : {1}", Status.INTERNAL_SERVER_ERROR,
                    Level.ERROR),
    MISSING_VALID_GROUP_FOR_ELEMENT("Element {0} returned by optimizer has invalid group id",
                    Status.INTERNAL_SERVER_ERROR, Level.ERROR),;
    private final String defaultId;
    private final String defaultMessage;
    private final String defaultResolution;
    private final String defaultAction;

    private final Status status;
    private final Level level;
    private final Boolean audit;
    private final Boolean metric;


    private LogMessages(String message, Status code, Level lev) {
        defaultMessage         = message;
        level                  = lev;
        status                 = code;
        this.defaultId         = this.name();
        this.defaultResolution = "No resolution needed";
        this.defaultAction     = "No action is required";
        this.audit             = false;
        this.metric            = false;
    }

    private LogMessages(String message, Status code, Level lev, Boolean audit, Boolean metric) {
        defaultMessage         = message;
        level                  = lev;
        status                 = code;
        this.audit             = audit;
        this.metric            = metric;
        this.defaultId         = this.name();
        this.defaultResolution = "No resolution needed";
        this.defaultAction     = "No action is required";
    }

    private LogMessages(String message, Status code, Level lev, String id, String resolution, String action) {
        level                  = lev;
        status                 = code;
        defaultMessage         = message;
        this.defaultId         = id;
        this.defaultResolution = resolution;
        this.defaultAction     = action;
        this.audit             = false;
        this.metric            = false;
    }

    static {
        EELFResourceManager.loadMessageBundle("logmessages");
    }

    /**
     * Gen properties.
     *
     * @return the string
     */
    public String genProperties() {
        // Use this to regenerate properties file. The desire to change messages without updating code is
        // well understood, but the developer should be able to code the defaults without having to update 2
        // different files and
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


    /**
     * Gets the level.
     *
     * @return the level
     */
    // interface methods
    @Override
    public Level getLevel() {
        return level;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    @Override
    public String getMessage() {
        return defaultMessage;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    @Override
    public Enum<?> getValue() {
        return this;
    }

    /**
     * Gets the domain.
     *
     * @return the domain
     */
    @Override
    public String getDomain() {
        return this.getClass().getSimpleName();
    }

    /**
     * Gets the audit.
     *
     * @return the audit
     */
    @Override
    public Boolean getAudit() {
        return audit;
    }

    /**
     * Gets the metric.
     *
     * @return the metric
     */
    @Override
    public Boolean getMetric() {
        return metric;
    }

    /**
     * The main method.
     *
     * @param argv the arguments
     */
    public static void main(String[] argv) {
        System.out.println(LogMessages.UNEXPECTED_EXCEPTION.genProperties());
        try {
            Files.write(Paths.get("src/main/resources/logmessages.properties"),
                            LogMessages.UNEXPECTED_EXCEPTION.genProperties().getBytes());
        } catch (IOException e) {
            EELFManager.getInstance().getDebugLogger().debug("Failed to update properties file.", e);

        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><h1>Cell Site Selection Scheduler mS Observations</h1>\n<table border=\"1\">\n<tr>");
        sb.append("<td>Code</td> ");
        sb.append("<td>Log Level</td> ");
        sb.append("<td>Message</td> ");
        sb.append("</tr>\n");
        for (LogMessages m : LogMessages.values()) {
            if (m.level == Level.ERROR || m.level == Level.WARN || m.level == Level.FATAL) {
                sb.append("<tr>");
                sb.append("<td>").append(m.name()).append("</td> ");
                sb.append("<td>").append(m.level).append("</td> ");
                sb.append("<td>").append(m.defaultMessage).append("</td> ");
                sb.append("</tr>\n");
            }
        }
        try {
            Files.write(Paths.get("logmessages.html"), sb.toString().getBytes());
        } catch (IOException e) {
            EELFManager.getInstance().getDebugLogger().debug("Failed to update properties html file.", e);

        }

    }

}
