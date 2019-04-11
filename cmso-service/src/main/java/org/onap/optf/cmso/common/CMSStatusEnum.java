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

/**
 * The Enum CMSStatusEnum.
 */
public enum CMSStatusEnum {
    PendingSchedule("Pending Schedule",
                    "Schedule request as been accepted. Pending determination of recommended schedule."),
    SchedulingFailed("Scheduling Failed", "Failed to determine recommended schedule."),
    ScheduleFailed("Schedule Failed", "Determination of recommended schedule failed."),
    OptimizationInProgress("Optimization In Progress", "Determination of recommended schedule is in progress."),
    PendingApproval("Pending Approval", "Pending approval of the recommended schedule."),
    OptimizationFailed("Optimization Failed", "Unable to determine recommended schedule."),
    Accepted("Accepted", "Recommended schedule has been accepted."),
    Scheduled("Scheduled", "All approvals received. Recommended schedule is pending execution."),
    ScheduledImmediate("Scheduled Immediate", "All approvals received. Event is scheduled for immediate execution."),
    Triggered("Triggered", "Scheduled event has been triggered."),
    ApprovalRejected("Approval Rejected", "Recommended schedule has been rejected."),
    PastDue("Past due", "Scheduled event time has passed. Queued event was not dispatched."),
    Error("Error", "Attempt to displatch event failed."),
    Failed("Failed", "Triggered event reported a failure."),
    Rejected("Rejected", "Recommended schedule has been rejected."),
    NotificationsInitiated("Notifications Initiated", "Notifications of scheduled events has been initiated."),
    Completed("Completed", "Notification of all scheduled events have been sent."),
    CompletedWithError("Completed with error(s)", "All scheduled events have completed, some with errors."),
    Deleted("Deleted", "Schedule deleted prior to acceptance or after execution."),
    Cancelled("Cancelled", "Scheduled event cancelled before execution."),
    NotScheduled("Not scheduled by optimizer", "Element not included in the schedule by optimizer."),;

    private final String text;
    private final String description;

    private CMSStatusEnum(String text, String description) {
        this.text        = text;
        this.description = description;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * From string.
     *
     * @param text the text
     * @return the CMS status enum
     */
    public CMSStatusEnum fromString(String text) {
        for (CMSStatusEnum e : CMSStatusEnum.values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }

    /**
     * The main method.
     *
     * @param argv the arguments
     */
    // To include in the AID.
    public static void main(String[] argv) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><table border=\"1\">\n");
        for (CMSStatusEnum v : CMSStatusEnum.values()) {
            sb.append("<tr><td>").append(v.text).append("</td><td>").append(v.description)
            .append("</td></tr>\n");
        }
        sb.append("</table></body></html>\n");
        System.out.println(sb.toString());
    }
}
