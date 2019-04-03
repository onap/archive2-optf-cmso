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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.joda.time.format.ISODateTimeFormat;

/**
 * The persistent class for the schedule_approvals database table.
 *
 */
@Entity
@Table(name = "SCHEDULE_APPROVALS")
@NamedQuery(name = "ScheduleApproval.findAll", query = "SELECT s FROM ScheduleApproval s")
@ApiModel(value = "Schedule Approval", description = "Details of a schedule approval/rejection.")
public class ScheduleApproval implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @JsonIgnore
    private UUID uuid;

    @JsonIgnore
    @Column(name = "approval_date_time")
    private Long approvalDateTimeMillis;

    @ApiModelProperty(value = "Date/time schedule time slot was accepted/rejected.")
    @JsonProperty
    @Transient
    private String approvalDateTime;

    @ApiModelProperty(value = "Approval status.", allowableValues = "Accepted,Rejected")
    @Column(name = "status")
    private String status;

    @ApiModelProperty(value = "ATTUID of the user accepting/rejecting the time slot.")
    @Column(name = "user_id")
    private String userId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "schedules_uuid", nullable = false, updatable = false)
    @JsonIgnore
    private Schedule schedule;

    @JsonIgnore
    @Column(name = "approval_types_uuid")
    private UUID approvalTypesUuid;

    public ScheduleApproval() {}


    /**
     * Gets the approval date time.
     *
     * @return the approval date time
     */
    public String getApprovalDateTime() {
        if (approvalDateTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(this.approvalDateTimeMillis);
        }
        return null;
    }

    public void setApprovalDateTime(String approvalDateTime) {}

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Long getApprovalDateTimeMillis() {
        return approvalDateTimeMillis;
    }

    public void setApprovalDateTimeMillis(Long approvalDateTimeMillis) {
        this.approvalDateTimeMillis = approvalDateTimeMillis;
    }


    public UUID getUuid() {
        return uuid;
    }


    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public UUID getApprovalTypesUuid() {
        return approvalTypesUuid;
    }


    public void setApprovalTypesUuid(UUID approvalTypesUuid) {
        this.approvalTypesUuid = approvalTypesUuid;
    }



}
