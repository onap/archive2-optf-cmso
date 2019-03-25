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
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.joda.time.format.ISODateTimeFormat;

/**
 * The persistent class for the change_management_groups database table.
 *
 */
@Entity
@Table(name = "CHANGE_MANAGEMENT_GROUPS")
@NamedQuery(name = "ChangeManagementGroup.findAll", query = "SELECT c FROM ChangeManagementGroup c")
@ApiModel(value = "Change Management Group", description = "Scheduling critirea for a group of VNFs")
public class ChangeManagementGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Id
    private UUID uuid;

    @JsonIgnore
    @Column(name = "finish_time")
    private Long finishTimeMillis;

    @ApiModelProperty(value = "Date/time by which all of the workflows should be completed.")
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

    @JsonIgnore
    @Column(name = "start_time")
    private Long startTimeMillis;

    @ApiModelProperty(value = "The date/time when workflows are to be started.")
    @JsonProperty
    @Transient
    private String startTime;

    @JsonIgnore
    @Column(name = "schedules_uuid")
    private UUID schedulesUuid;

    @Column(name = "additional_duration_in_secs")
    @ApiModelProperty(value = "Time added to the workflow interval to allow for rollback in case of failure.")
    private Integer additionalDurationInSecs;

    @ApiModelProperty(value = "The maximum number of workflows that should be started simultaneiously.")
    @Column(name = "concurrency_limit")
    private Integer concurrencyLimit;

    @ApiModelProperty(value = "Expected duration of a successful workflow execution.")
    @Column(name = "normal_duration_in_secs")
    private Integer normalDurationInSecs;

    @ApiModelProperty(
                    value = "The name of the schedule optimization policy "
                                    + "used by the change management schedule optimizer.")
    @Column(name = "policy_id")
    private String policyId;

    @ApiModelProperty(value = "The list of VNF workflows scheduled.")
    @JsonProperty
    @Transient
    private List<ChangeManagementSchedule> changeManagementSchedules;

    /**
     * Instantiates a new change management group.
     */
    public ChangeManagementGroup() {}


    /**
     * Gets the finish time.
     *
     * @return the finish time
     */
    public String getFinishTime() {
        if (finishTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(this.finishTimeMillis);
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
     * Gets the group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return this.groupId;
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
            return ISODateTimeFormat.dateTimeNoMillis().print(this.lastInstanceStartTimeMillis);
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
     * Gets the start time.
     *
     * @return the start time
     */
    public String getStartTime() {
        if (startTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(this.startTimeMillis);
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
     * Gets the additional duration in secs.
     *
     * @return the additional duration in secs
     */
    public Integer getAdditionalDurationInSecs() {
        return additionalDurationInSecs;
    }

    /**
     * Sets the additional duration in secs.
     *
     * @param additionalDurationInSecs the new additional duration in secs
     */
    public void setAdditionalDurationInSecs(Integer additionalDurationInSecs) {
        this.additionalDurationInSecs = additionalDurationInSecs;
    }

    /**
     * Gets the concurrency limit.
     *
     * @return the concurrency limit
     */
    public Integer getConcurrencyLimit() {
        return concurrencyLimit;
    }

    /**
     * Sets the concurrency limit.
     *
     * @param concurrencyLimit the new concurrency limit
     */
    public void setConcurrencyLimit(Integer concurrencyLimit) {
        this.concurrencyLimit = concurrencyLimit;
    }

    /**
     * Gets the normal duration in secs.
     *
     * @return the normal duration in secs
     */
    public Integer getNormalDurationInSecs() {
        return normalDurationInSecs;
    }

    /**
     * Sets the normal duration in secs.
     *
     * @param normalDurationInSecs the new normal duration in secs
     */
    public void setNormalDurationInSecs(Integer normalDurationInSecs) {
        this.normalDurationInSecs = normalDurationInSecs;
    }

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
     * Gets the change management schedules.
     *
     * @return the change management schedules
     */
    public List<ChangeManagementSchedule> getChangeManagementSchedules() {
        return changeManagementSchedules;
    }

    /**
     * Sets the change management schedules.
     *
     * @param changeManagementSchedules the new change management schedules
     */
    public void setChangeManagementSchedules(List<ChangeManagementSchedule> changeManagementSchedules) {
        this.changeManagementSchedules = changeManagementSchedules;
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

}
