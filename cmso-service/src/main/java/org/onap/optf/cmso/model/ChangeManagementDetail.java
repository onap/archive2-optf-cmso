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

package org.onap.optf.cmso.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity

// @NamedNativeQueries({
// @NamedNativeQuery(
// name = "searchScheduleDetails",
// query = "SELECT id, firstName, lastName, email, department.id, department.name " +
// "FROM employee, department",
// resultClass=ChangeManagementDetail.class
// ),
// })
@ApiModel(value = "Change Management Details",
        description = "VNF information  returned for Change Management Schedule Search request")
public class ChangeManagementDetail {
    @Id
    @JsonIgnore
    private UUID uuid;

    @ApiModelProperty(value = "Name of the VNF.")
    @Column(name = "vnf_name")
    private String vnfName;

    @JsonIgnore
    @Column(name = "vnf_id")
    private String vnfId;

    @ApiModelProperty(value = "Status of the VNF.", allowableValues = "See CMSStatusEnum")
    @Column(name = "status")
    private String status;

    @ApiModelProperty(value = "TM Change Id")
    @Column(name = "tm_change_id")
    private String tmChangeId;

    @ApiModelProperty(value = "TM ticket status", allowableValues = "<null>,Closed")
    @Column(name = "tm_status")
    private String tmStatus;

    @ApiModelProperty(value = "TM ticket approval status", allowableValues = "<null>,Approved")
    @Column(name = "tm_approval_status")
    private String tmApprovalStatus;

    @JsonIgnore
    @Column(name = "start_time")
    private Long startTimeMillis;

    @ApiModelProperty(
            value = "Start time of this VNF workflow assigned by Scheduler based upon the group start time returned by the optimizer and concurrency.")
    @JsonProperty
    @Transient
    private String startTime;

    @JsonIgnore
    @Column(name = "finish_time")
    private Long finishTimeMillis;

    @ApiModelProperty(value = "Anticipated time of completion based upon start time and duration")
    @JsonProperty
    @Transient
    private String finishTime;

    @ApiModelProperty(value = "Name of the group of VNFs to be scheduled")
    @Column(name = "group_id")
    private String groupId;

    @JsonIgnore
    @Column(name = "last_instance_start_time")
    private Long lastInstanceStartTimeMillis;

    @ApiModelProperty(value = "The latest date/time by which a workflow is to be started.")
    @JsonProperty
    @Transient
    private String lastInstanceStartTime;

    @ApiModelProperty(value = "Time of last poll for MSO status.")
    @Column(name = "policy_id")
    private String policyId;

    @JsonIgnore
    @Column(name = "dispatch_time")
    private Long dispatchTimeMillis;

    @ApiModelProperty(value = "Actual time the VNF workflow was dispatched.")
    @JsonProperty
    @Transient
    private String dispatchTime;

    @JsonIgnore
    @Column(name = "execution_completed_time")
    private Long executionCompletedTimeMillis;

    @ApiModelProperty(value = "Actual time the VNF workflow execution was completed as reported by MSO.")
    @JsonProperty
    @Transient
    private String executionCompletedTime;

    @ApiModelProperty(value = "MSO Request ID of the workflow returned at dispatch time.")
    @Column(name = "mso_request_id")
    private String msoRequestId;

    @ApiModelProperty(value = "Final MSO status.", allowableValues = "COMPLETED,FAILED")
    @Column(name = "mso_status")
    private String msoStatus;

    @ApiModelProperty(value = "MSO final status message.")
    @Lob
    @Column(name = "mso_message")
    private String msoMessage;

    @ApiModelProperty(value = "Scheduler status message.")
    @Lob
    @Column(name = "status_message")
    private String statusMessage;

    @JsonIgnore
    @Column(name = "mso_time")
    private Long msoTimeMillis;

    @ApiModelProperty(value = "Time of last poll for MSO status.")
    @JsonProperty
    @Transient
    private String msoTime;

    @JsonIgnore
    @Column(name = "schedules_uuid")
    private UUID schedulesUuid;

    public String getVnfName() {
        return vnfName;
    }

    public void setVnfName(String vnfName) {
        this.vnfName = vnfName;
    }

    public String getVnfId() {
        return vnfId;
    }

    public void setVnfId(String vnfId) {
        this.vnfId = vnfId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTmChangeId() {
        return tmChangeId;
    }

    public void setTmChangeId(String tmChangeId) {
        this.tmChangeId = tmChangeId;
    }

    public String getStartTime() {
        if (startTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(startTimeMillis);
        return null;
    }

    public void setStartTime(String startTime) {}

    public String getFinishTime() {
        if (finishTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(finishTimeMillis);
        return null;
    }

    public void setFinishTime(String finishTime) {}

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getLastInstanceStartTime() {
        if (lastInstanceStartTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(lastInstanceStartTimeMillis);
        return null;
    }

    public void setLastInstanceStartTime(String lastInstanceStartTime) {}

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }


    public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}




	public UUID getSchedulesUuid() {
		return schedulesUuid;
	}

	public void setSchedulesUuid(UUID schedulesUuid) {
		this.schedulesUuid = schedulesUuid;
	}

	public Long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(Long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public Long getFinishTimeMillis() {
        return finishTimeMillis;
    }

    public void setFinishTimeMillis(Long finishTimeMillis) {
        this.finishTimeMillis = finishTimeMillis;
    }

    public Long getLastInstanceStartTimeMillis() {
        return lastInstanceStartTimeMillis;
    }

    public void setLastInstanceStartTimeMillis(Long lastInstanceStartTimeMillis) {
        this.lastInstanceStartTimeMillis = lastInstanceStartTimeMillis;
    }

    public String getTmStatus() {
        return tmStatus;
    }

    public void setTmStatus(String tmStatus) {
        this.tmStatus = tmStatus;
    }

    public String getTmApprovalStatus() {
        return tmApprovalStatus;
    }

    public void setTmApprovalStatus(String tmApprovalStatus) {
        this.tmApprovalStatus = tmApprovalStatus;
    }

    public String getMsoRequestId() {
        return msoRequestId;
    }

    public void setMsoRequestId(String msoRequestId) {
        this.msoRequestId = msoRequestId;
    }

    public String getMsoStatus() {
        return msoStatus;
    }

    public void setMsoStatus(String msoStatus) {
        this.msoStatus = msoStatus;
    }

    public String getMsoMessage() {
        return msoMessage;
    }

    public void setMsoMessage(String msoMessage) {
        this.msoMessage = msoMessage;
    }

    public Long getDispatchTimeMillis() {
        return dispatchTimeMillis;
    }

    public void setDispatchTimeMillis(Long dispatchTimeMillis) {
        this.dispatchTimeMillis = dispatchTimeMillis;
    }

    public String getDispatchTime() {
        if (dispatchTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(dispatchTimeMillis);
        return null;
    }

    public void setDispatchTime(String dispatchTime) {}

    public Long getExecutionCompletedTimeMillis() {
        return executionCompletedTimeMillis;
    }

    public void setExecutionCompletedTimeMillis(Long executionCompletedTimeMillis) {
        this.executionCompletedTimeMillis = executionCompletedTimeMillis;
    }

    public String getExecutionCompletedTime() {
        if (executionCompletedTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(executionCompletedTimeMillis);
        return null;
    }

    public void setExecutionCompletedTime(String executionCompletedTime) {}

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Long getMsoTimeMillis() {
        return msoTimeMillis;
    }

    public void setMsoTimeMillis(Long msoTimeMillis) {
        this.msoTimeMillis = msoTimeMillis;
    }

    public String getMsoTime() {
        if (msoTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(msoTimeMillis);
        return null;
    }

    public void setMsoTime(String msoTime) {}

}
