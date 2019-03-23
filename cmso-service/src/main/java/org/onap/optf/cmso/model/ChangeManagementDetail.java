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
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import org.joda.time.format.ISODateTimeFormat;

/**
 * The Class ChangeManagementDetail.
 */
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
                    value = "Start time of this VNF workflow"
                                    + " assigned by Scheduler based upon the"
                                    + " group start time returned by the optimizer and concurrency.")
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

    /**
     * Gets the vnf name.
     *
     * @return the vnf name
     */
    public String getVnfName() {
        return vnfName;
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
     * Gets the vnf id.
     *
     * @return the vnf id
     */
    public String getVnfId() {
        return vnfId;
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
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
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
     * Gets the start time.
     *
     * @return the start time
     */
    public String getStartTime() {
        if (startTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(startTimeMillis);
        return null;
    }

    /**
     * Sets the start time.
     *
     * @param startTime the new start time
     */
    public void setStartTime(String startTime) {}

    /**
     * Gets the finish time.
     *
     * @return the finish time
     */
    public String getFinishTime() {
        if (finishTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(finishTimeMillis);
        return null;
    }

    /**
     * Sets the finish time.
     *
     * @param finishTime the new finish time
     */
    public void setFinishTime(String finishTime) {}

    /**
     * Gets the group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the group id.
     *
     * @param groupId the new group id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the last instance start time.
     *
     * @return the last instance start time
     */
    public String getLastInstanceStartTime() {
        if (lastInstanceStartTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(lastInstanceStartTimeMillis);
        }
        return null;
    }

    /**
     * Sets the last instance start time.
     *
     * @param lastInstanceStartTime the new last instance start time
     */
    public void setLastInstanceStartTime(String lastInstanceStartTime) {}

    /**
     * Gets the policy id.
     *
     * @return the policy id
     */
    public String getPolicyId() {
        return policyId;
    }

    /**
     * Sets the policy id.
     *
     * @param policyId the new policy id
     */
    public void setPolicyId(String policyId) {
        this.policyId = policyId;
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
     * Gets the schedules uuid.
     *
     * @return the schedules uuid
     */
    public UUID getSchedulesUuid() {
        return schedulesUuid;
    }

    /**
     * Sets the schedules uuid.
     *
     * @param schedulesUuid the new schedules uuid
     */
    public void setSchedulesUuid(UUID schedulesUuid) {
        this.schedulesUuid = schedulesUuid;
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
     * Gets the last instance start time millis.
     *
     * @return the last instance start time millis
     */
    public Long getLastInstanceStartTimeMillis() {
        return lastInstanceStartTimeMillis;
    }

    /**
     * Sets the last instance start time millis.
     *
     * @param lastInstanceStartTimeMillis the new last instance start time millis
     */
    public void setLastInstanceStartTimeMillis(Long lastInstanceStartTimeMillis) {
        this.lastInstanceStartTimeMillis = lastInstanceStartTimeMillis;
    }

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

    /**
     * Sets the mso time.
     *
     * @param msoTime the new mso time
     */
    public void setMsoTime(String msoTime) {}

}
