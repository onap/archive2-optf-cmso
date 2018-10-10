/*
 * Copyright � 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright � 2018 IBM.
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

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.joda.time.format.ISODateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

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
    @Column(name = "schedules_id")
    private Integer schedulesId;

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
            value = "The name of the schedule optimization policy used by the change management schedule optimizer.")
    @Column(name = "policy_id")
    private String policyId;

    @ApiModelProperty(value = "The list of VNF workflows scheduled.")
    @JsonProperty
    @Transient
    private List<ChangeManagementSchedule> changeManagementSchedules;

    public ChangeManagementGroup() {}

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFinishTime() {
        if (finishTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(this.finishTimeMillis);
        return null;
    }

    public void setFinishTime(String finishTime) {}

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getLastInstanceStartTime() {
        if (lastInstanceStartTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(this.lastInstanceStartTimeMillis);
        return null;
    }

    public void setLastInstanceStartTime(String lastInstanceStartTime) {}

    public String getStartTime() {
        if (startTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(this.startTimeMillis);
        return null;
    }

    public void setStartTime(String startTime) {}

    public Integer getSchedulesId() {
        return schedulesId;
    }

    public void setSchedulesId(Integer schedulesId) {
        this.schedulesId = schedulesId;
    }

    public Integer getAdditionalDurationInSecs() {
        return additionalDurationInSecs;
    }

    public void setAdditionalDurationInSecs(Integer additionalDurationInSecs) {
        this.additionalDurationInSecs = additionalDurationInSecs;
    }

    public Integer getConcurrencyLimit() {
        return concurrencyLimit;
    }

    public void setConcurrencyLimit(Integer concurrencyLimit) {
        this.concurrencyLimit = concurrencyLimit;
    }

    public Integer getNormalDurationInSecs() {
        return normalDurationInSecs;
    }

    public void setNormalDurationInSecs(Integer normalDurationInSecs) {
        this.normalDurationInSecs = normalDurationInSecs;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
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

    public Long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(Long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public List<ChangeManagementSchedule> getChangeManagementSchedules() {
        return changeManagementSchedules;
    }

    public void setChangeManagementSchedules(List<ChangeManagementSchedule> changeManagementSchedules) {
        this.changeManagementSchedules = changeManagementSchedules;
    }

}
