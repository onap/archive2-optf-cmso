/*
 * Copyright © 2017-2018 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
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

package org.onap.optf.cmso.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
                    value = "Start time of this VNF workflow assigned by "
                                    + "Scheduler based upon the group start"
                                    + " time returned by the optimizer and concurrency.")
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

    /**
     * Instantiates a new change management schedule.
     */
    public ChangeManagementSchedule() {}

    /**
     * Gets the finish time.
     *
     * @return the finish time
     */
    public String getFinishTime() {
        if (finishTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(finishTimeMillis);
        }
        return null;
    }

    /**
     * Sets the finish time.
     *
     * @param finishTime the new finish time
     */
    public void setFinishTime(String finishTime) {}

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public String getStartTime() {
        if (startTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(startTimeMillis);
        }
        return null;
    }

    /**
     * Sets the start time.
     *
     * @param startTime the new start time
     */
    public void setStartTime(String startTime) {}

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the vnf id.
     *
     * @return the vnf id
     */
    public String getVnfId() {
        return this.vnfId;
    }

    /**
     * Sets the vnf id.
     *
     * @param vnfId the new vnf id
     */
    public void setVnfId(String vnfId) {
        this.vnfId = vnfId;
    }

    /**
     * Gets the vnf name.
     *
     * @return the vnf name
     */
    public String getVnfName() {
        return this.vnfName;
    }

    /**
     * Sets the vnf name.
     *
     * @param vnfName the new vnf name
     */
    public void setVnfName(String vnfName) {
        this.vnfName = vnfName;
    }

    /**
     * Gets the tm change id.
     *
     * @return the tm change id
     */
    public String getTmChangeId() {
        return tmChangeId;
    }

    /**
     * Sets the tm change id.
     *
     * @param tmChangeId the new tm change id
     */
    public void setTmChangeId(String tmChangeId) {
        this.tmChangeId = tmChangeId;
    }


    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid.
     *
     * @param uuid the new uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    /**
     * Gets the change management group uuid.
     *
     * @return the change management group uuid
     */
    public UUID getChangeManagementGroupUuid() {
        return changeManagementGroupUuid;
    }

    /**
     * Sets the change management group uuid.
     *
     * @param changeManagementGroupUuid the new change management group uuid
     */
    public void setChangeManagementGroupUuid(UUID changeManagementGroupUuid) {
        this.changeManagementGroupUuid = changeManagementGroupUuid;
    }

    /**
     * Gets the dispatch time.
     *
     * @return the dispatch time
     */
    public String getDispatchTime() {
        if (dispatchTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(dispatchTimeMillis);
        }
        return null;
    }

    /**
     * Sets the dispatch time.
     *
     * @param dispatchTime the new dispatch time
     */
    public void setDispatchTime(String dispatchTime) {}

    /**
     * Gets the dispatcher instance.
     *
     * @return the dispatcher instance
     */
    public String getDispatcherInstance() {
        return dispatcherInstance;
    }

    /**
     * Sets the dispatcher instance.
     *
     * @param dispatcherInstance the new dispatcher instance
     */
    public void setDispatcherInstance(String dispatcherInstance) {
        this.dispatcherInstance = dispatcherInstance;
    }

    /**
     * Gets the execution completed time.
     *
     * @return the execution completed time
     */
    public String getExecutionCompletedTime() {
        if (executionCompletedTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(executionCompletedTimeMillis);
        }
        return null;
    }

    /**
     * Sets the execution completed time.
     *
     * @param executionCompletedTime the new execution completed time
     */
    public void setExecutionCompletedTime(String executionCompletedTime) {}

    /**
     * Gets the mso request id.
     *
     * @return the mso request id
     */
    public String getMsoRequestId() {
        return msoRequestId;
    }

    /**
     * Sets the mso request id.
     *
     * @param msoRequestId the new mso request id
     */
    public void setMsoRequestId(String msoRequestId) {
        this.msoRequestId = msoRequestId;
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statusMessage the new status message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Gets the finish time millis.
     *
     * @return the finish time millis
     */
    public Long getFinishTimeMillis() {
        return finishTimeMillis;
    }

    /**
     * Sets the finish time millis.
     *
     * @param finishTimeMillis the new finish time millis
     */
    public void setFinishTimeMillis(Long finishTimeMillis) {
        this.finishTimeMillis = finishTimeMillis;
    }

    /**
     * Gets the start time millis.
     *
     * @return the start time millis
     */
    public Long getStartTimeMillis() {
        return startTimeMillis;
    }

    /**
     * Sets the start time millis.
     *
     * @param startTimeMillis the new start time millis
     */
    public void setStartTimeMillis(Long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    /**
     * Gets the dispatch time millis.
     *
     * @return the dispatch time millis
     */
    public Long getDispatchTimeMillis() {
        return dispatchTimeMillis;
    }

    /**
     * Sets the dispatch time millis.
     *
     * @param dispatchTimeMillis the new dispatch time millis
     */
    public void setDispatchTimeMillis(Long dispatchTimeMillis) {
        this.dispatchTimeMillis = dispatchTimeMillis;
    }

    /**
     * Gets the execution completed time millis.
     *
     * @return the execution completed time millis
     */
    public Long getExecutionCompletedTimeMillis() {
        return executionCompletedTimeMillis;
    }

    /**
     * Sets the execution completed time millis.
     *
     * @param executionCompletedTimeMillis the new execution completed time millis
     */
    public void setExecutionCompletedTimeMillis(Long executionCompletedTimeMillis) {
        this.executionCompletedTimeMillis = executionCompletedTimeMillis;
    }

    /**
     * Gets the mso status.
     *
     * @return the mso status
     */
    public String getMsoStatus() {
        return msoStatus;
    }

    /**
     * Sets the mso status.
     *
     * @param msoStatus the new mso status
     */
    public void setMsoStatus(String msoStatus) {
        this.msoStatus = msoStatus;
    }

    /**
     * Gets the mso message.
     *
     * @return the mso message
     */
    public String getMsoMessage() {
        return msoMessage;
    }

    /**
     * Sets the mso message.
     *
     * @param msoMessage the new mso message
     */
    public void setMsoMessage(String msoMessage) {
        this.msoMessage = msoMessage;
    }

    /**
     * Gets the mso time millis.
     *
     * @return the mso time millis
     */
    public Long getMsoTimeMillis() {
        return msoTimeMillis;
    }

    /**
     * Sets the mso time millis.
     *
     * @param msoTimeMillis the new mso time millis
     */
    public void setMsoTimeMillis(Long msoTimeMillis) {
        this.msoTimeMillis = msoTimeMillis;
    }

    /**
     * Gets the mso time.
     *
     * @return the mso time
     */
    public String getMsoTime() {
        if (msoTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(msoTimeMillis);
        }
        return null;
    }

    protected void setMsoTime(String msoTime) {}

    /**
     * Gets the tm status.
     *
     * @return the tm status
     */
    public String getTmStatus() {
        return tmStatus;
    }

    /**
     * Sets the tm status.
     *
     * @param tmStatus the new tm status
     */
    public void setTmStatus(String tmStatus) {
        this.tmStatus = tmStatus;
    }

    /**
     * Gets the tm approval status.
     *
     * @return the tm approval status
     */
    public String getTmApprovalStatus() {
        return tmApprovalStatus;
    }

    /**
     * Sets the tm approval status.
     *
     * @param tmApprovalStatus the new tm approval status
     */
    public void setTmApprovalStatus(String tmApprovalStatus) {
        this.tmApprovalStatus = tmApprovalStatus;
    }

    /**
     * Gets the request.
     *
     * @return the request
     */
    public String getRequest() {
        return request;
    }

    /**
     * Sets the request.
     *
     * @param request the new request
     */
    public void setRequest(String request) {
        this.request = request;
    }

}
