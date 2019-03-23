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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.joda.time.format.ISODateTimeFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * The persistent class for the change_management_schedules database table.
 *
 */
@Entity
@Table(name = "CHANGE_MANAGEMENT_SCHEDULES")
@NamedQuery(name = "ChangeManagementSchedule.findAll", query = "SELECT c FROM ChangeManagementSchedule c")

@ApiModel(value = "Change Management Schedule", description = "VNF details for Change Management Schedule")
public class ChangeManagementSchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Id
    private UUID uuid;

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
    @Column(name = "finish_time")
    private Long finishTimeMillis;

    @JsonProperty
    @Transient
    @ApiModelProperty(value = "Anticipated time of completion based upon start time and duration")
    private String finishTime;

    @JsonIgnore
    @Column(name = "start_time")
    private Long startTimeMillis;

    @ApiModelProperty(
            value = "Start time of this VNF workflow assigned by Scheduler based upon the group start time returned by the optimizer and concurrency.")
    @JsonProperty
    @Transient
    private String startTime;

    @ApiModelProperty(value = "Status of the VNF.", allowableValues = "See CMSStatusEnum")
    @Column(name = "status")
    private String status;

    @JsonIgnore
    @Column(name = "vnf_id")
    private String vnfId;

    @ApiModelProperty(value = "Name of the VNF.")
    @Column(name = "vnf_name")
    private String vnfName;

    @Column(name = "change_management_group_uuid")
    @JsonIgnore
    private UUID changeManagementGroupUuid;

    @JsonIgnore
    @Column(name = "dispatch_time")
    private Long dispatchTimeMillis;

    @ApiModelProperty(value = "Actual time the VNF workflow was dispatched.")
    @JsonProperty
    @Transient
    private String dispatchTime;

    @JsonIgnore
    @Column(name = "dispatcher_instance")
    private String dispatcherInstance;

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

    @ApiModelProperty(value = "Change equest.")
    @Lob
    @Column(name = "request")
    private String request;

    public ChangeManagementSchedule() {}

    public String getFinishTime() {
        if (finishTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(finishTimeMillis);
        return null;
    }

    public void setFinishTime(String finishTime) {}

    public String getStartTime() {
        if (startTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(startTimeMillis);
        return null;
    }

    public void setStartTime(String startTime) {}

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVnfId() {
        return this.vnfId;
    }

    public void setVnfId(String vnfId) {
        this.vnfId = vnfId;
    }

    public String getVnfName() {
        return this.vnfName;
    }

    public void setVnfName(String vnfName) {
        this.vnfName = vnfName;
    }

    public String getTmChangeId() {
        return tmChangeId;
    }

    public void setTmChangeId(String tmChangeId) {
        this.tmChangeId = tmChangeId;
    }


    public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}


	public UUID getChangeManagementGroupUuid() {
		return changeManagementGroupUuid;
	}

	public void setChangeManagementGroupUuid(UUID changeManagementGroupUuid) {
		this.changeManagementGroupUuid = changeManagementGroupUuid;
	}

	public String getDispatchTime() {
        if (dispatchTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(dispatchTimeMillis);
        return null;
    }

    public void setDispatchTime(String dispatchTime) {}

    public String getDispatcherInstance() {
        return dispatcherInstance;
    }

    public void setDispatcherInstance(String dispatcherInstance) {
        this.dispatcherInstance = dispatcherInstance;
    }

    public String getExecutionCompletedTime() {
        if (executionCompletedTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(executionCompletedTimeMillis);
        return null;
    }

    public void setExecutionCompletedTime(String executionCompletedTime) {}

    public String getMsoRequestId() {
        return msoRequestId;
    }

    public void setMsoRequestId(String msoRequestId) {
        this.msoRequestId = msoRequestId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Long getFinishTimeMillis() {
        return finishTimeMillis;
    }

    public void setFinishTimeMillis(Long finishTimeMillis) {
        this.finishTimeMillis = finishTimeMillis;
    }

    public Long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(Long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public Long getDispatchTimeMillis() {
        return dispatchTimeMillis;
    }

    public void setDispatchTimeMillis(Long dispatchTimeMillis) {
        this.dispatchTimeMillis = dispatchTimeMillis;
    }

    public Long getExecutionCompletedTimeMillis() {
        return executionCompletedTimeMillis;
    }

    public void setExecutionCompletedTimeMillis(Long executionCompletedTimeMillis) {
        this.executionCompletedTimeMillis = executionCompletedTimeMillis;
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

    protected void setMsoTime(String msoTime) {}

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

    public String getRequest() {
    return request;}

    public void setRequest(String request) {
    this.request = request;}

}
