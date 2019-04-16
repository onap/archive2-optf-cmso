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

package org.onap.optf.cmso.optimizer.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Response from schedule optimizer",
        description = "Asynchronous response to schedule oprimizer request.")
public class CmsoOptimizerResponse {

    /*
     * 
     * { "transactionId": "dummy-transaction-id",
     * "scheduleId":"CM-<__SCHEDULE ID__>", "requestState": "complete", "status":
     * "Optimal", // diagnostic code "description": "Optimal solution found", //
     * diagnostic code "schedule": [ { "groupId": "grp06", "startTime":
     * "2016-10-01T00:30:00+05:00", // starting time for this group (In 1707,
     * seconds will always be zero but there is no reason to hardcode that decision)
     * "finishTime": "2016-10-01T00:40:00+05:00", // endtime for this group
     * (including failover) "latestInstanceStartTime": "2016-10-01T00:38:00Z", //
     * latest time when an instance of this group can be started "node": [ "up01",
     * "up03", "up09" ] // list of instances for this group. } ] }
     * 
     */

    @ApiModelProperty(value = "Unique id of optimization request.")
    private String transactionId;

    @ApiModelProperty(value = "Schedule id for which the optimization request was executed.")
    private String scheduleId;

    @ApiModelProperty(value = "State of the request as reported by the optimizer.")
    private String requestState;

    @ApiModelProperty(value = "Status of the request.")
    private String status;

    @ApiModelProperty(value = "Description of the request status.")
    private String description;

    @ApiModelProperty(value = "List of schedules returned, one per group. Only 1 group supported at this time.")
    private CmsoSchedule[] schedule;

    public CmsoOptimizerResponse() {}

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getRequestState() {
        return requestState;
    }

    public void setRequestState(String requestState) {
        this.requestState = requestState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CmsoSchedule[] getSchedule() {
        return schedule;
    }

    public void setSchedule(CmsoSchedule[] schedule) {
        this.schedule = schedule;
    }

}
