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
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.format.ISODateTimeFormat;

/**
 * The persistent class for the schedules database table.
 *
 */
@Entity
@Table(name = "SCHEDULES")
@NamedQuery(name = "Schedule.findAll", query = "SELECT s FROM Schedule s")
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Id
    private UUID uuid;

    @JsonIgnore
    @Column(name = "create_date_time")
    private Long createDateTimeMillis;

    @ApiModelProperty(value = "Date/time schedule was created.")
    @JsonProperty
    @Transient
    private String createDateTime;

    @ApiModelProperty(value = "Date/time optimizer was invoked.")
    @JsonIgnore
    @Column(name = "optimizer_date_time")
    private Long optimizerDateTimeMillis;

    @JsonProperty
    @Transient
    private String optimizerDateTime;

    @Lob
    @Column(name = "optimizer_message")
    private String optimizerMessage;

    @Column(name = "optimizer_status")
    private String optimizerStatus;

    @JsonIgnore
    @Column(name = "optimizer_attempts_to_schedule")
    private Integer optimizerAttemptsToSchedule;

    @JsonIgnore
    @Column(name = "optimizer_return_date_time")
    private Long optimizerReturnDateTimeMillis;

    @JsonProperty
    @Transient
    private String optimizerReturnDateTime;

    @Column(name = "optimizer_transaction_id")
    private String optimizerTransactionId;

    @Lob
    @Column(name = "schedule")
    private String schedule;

    @JsonIgnore
    @Column(name = "schedule_id")
    private String scheduleId;

    @Column(name = "schedule_name")
    private String scheduleName;

    @Lob
    @Column(name = "schedule_info")
    private String scheduleInfo;

    @Column(name = "status")
    private String status;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "domain")
    private String domain;

    @JsonIgnore
    @Column(name = "delete_date_time")
    private Long deleteDateTimeMillis;

    @JsonProperty
    @Transient
    private String deleteDateTime;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "schedule")
    @LazyCollection(value = LazyCollectionOption.FALSE)
    // @JoinColumn(name="schedules_id")
    private List<DomainData> domainData;
    //
    // //uni-directional many-to-one association to ScheduleApproval
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "schedule")
    @LazyCollection(value = LazyCollectionOption.FALSE)
    private List<ScheduleApproval> scheduleApprovals;
    //
    // //bi-directional many-to-one association to Domain
    // @ManyToOne
    // @JoinColumn(name="domain")
    // private Domain domainBean;

    @JsonProperty
    @Transient
    List<ChangeManagementGroup> groups;

    /**
     * Instantiates a new schedule.
     */
    public Schedule() {}

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
     * Sets the domain data.
     *
     * @param domainData the new domain data
     */
    public void setDomainData(List<DomainData> domainData) {
        this.domainData = domainData;
    }

    /**
     * Sets the schedule approvals.
     *
     * @param scheduleApprovals the new schedule approvals
     */
    public void setScheduleApprovals(List<ScheduleApproval> scheduleApprovals) {
        this.scheduleApprovals = scheduleApprovals;
    }

    /**
     * Gets the creates the date time.
     *
     * @return the creates the date time
     */
    public String getCreateDateTime() {
        if (createDateTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(this.createDateTimeMillis);
        }
        return null;
    }

    /**
     * Sets the creates the date time.
     *
     * @param datetime the new creates the date time
     */
    public void setCreateDateTime(String datetime) {
        // only set time via setCreateDateTimeMillis
    }

    /**
     * Sets the creates the date time millis.
     *
     * @param millis the new creates the date time millis
     */
    public void setCreateDateTimeMillis(Long millis) {
        this.createDateTimeMillis = millis;
    }

    /**
     * Gets the creates the date time millis.
     *
     * @return the creates the date time millis
     */
    public long getCreateDateTimeMillis() {
        return this.createDateTimeMillis;
    }

    /**
     * Gets the delete date time millis.
     *
     * @return the delete date time millis
     */
    public Long getDeleteDateTimeMillis() {
        return deleteDateTimeMillis;
    }

    /**
     * Sets the delete date time millis.
     *
     * @param deleteDateTimeMillis the new delete date time millis
     */
    public void setDeleteDateTimeMillis(Long deleteDateTimeMillis) {
        this.deleteDateTimeMillis = deleteDateTimeMillis;
    }

    /**
     * Gets the delete date time.
     *
     * @return the delete date time
     */
    public String getDeleteDateTime() {
        if (deleteDateTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(this.deleteDateTimeMillis);
        }
        return null;
    }

    /**
     * Sets the delete date time.
     *
     * @param deleteDateTime the new delete date time
     */
    public void setDeleteDateTime(String deleteDateTime) {}

    /**
     * Gets the optimizer date time.
     *
     * @return the optimizer date time
     */
    public String getOptimizerDateTime() {
        if (optimizerDateTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(this.optimizerDateTimeMillis);
        }
        return null;
    }

    /**
     * Sets the optimizer date time.
     *
     * @param optimizerDateTime the new optimizer date time
     */
    public void setOptimizerDateTime(String optimizerDateTime) {}

    /**
     * Gets the optimizer message.
     *
     * @return the optimizer message
     */
    public String getOptimizerMessage() {
        return this.optimizerMessage;
    }

    /**
     * Sets the optimizer message.
     *
     * @param optimizerMessage the new optimizer message
     */
    public void setOptimizerMessage(String optimizerMessage) {
        this.optimizerMessage = optimizerMessage;
    }

    /**
     * Gets the optimizer status.
     *
     * @return the optimizer status
     */
    public String getOptimizerStatus() {
        return this.optimizerStatus;
    }

    /**
     * Sets the optimizer status.
     *
     * @param optimizerStatus the new optimizer status
     */
    public void setOptimizerStatus(String optimizerStatus) {
        this.optimizerStatus = optimizerStatus;
    }

    /**
     * Gets the schedule.
     *
     * @return the schedule
     */
    public String getSchedule() {
        return this.schedule;
    }

    /**
     * Sets the schedule.
     *
     * @param schedule the new schedule
     */
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    /**
     * Gets the schedule id.
     *
     * @return the schedule id
     */
    public String getScheduleId() {
        return this.scheduleId;
    }

    /**
     * Sets the schedule id.
     *
     * @param scheduleId the new schedule id
     */
    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    /**
     * Gets the schedule name.
     *
     * @return the schedule name
     */
    public String getScheduleName() {
        return this.scheduleName;
    }

    /**
     * Sets the schedule name.
     *
     * @param scheduleName the new schedule name
     */
    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    /**
     * Gets the schedule info.
     *
     * @return the schedule info
     */
    public String getScheduleInfo() {
        return this.scheduleInfo;
    }

    /**
     * Sets the schedule info.
     *
     * @param scheduleInfo the new schedule info
     */
    public void setScheduleInfo(String scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }

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
     * Gets the user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the new user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the domain.
     *
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the domain.
     *
     * @param domain the new domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Gets the domain data.
     *
     * @return the domain data
     */
    public List<DomainData> getDomainData() {
        return domainData;
    }

    /**
     * Gets the schedule approvals.
     *
     * @return the schedule approvals
     */
    public List<ScheduleApproval> getScheduleApprovals() {
        return scheduleApprovals;
    }

    /**
     * Adds the domain data.
     *
     * @param domainData the domain data
     * @return the domain data
     */
    public DomainData addDomainData(DomainData domainData) {
        List<DomainData> list = getDomainData();
        if (list == null) {
            list = new ArrayList<DomainData>();
        }
        list.add(domainData);
        domainData.setSchedule(this);
        return domainData;
    }

    /**
     * Adds the schedule approval.
     *
     * @param sa the sa
     * @return the schedule approval
     */
    public ScheduleApproval addScheduleApproval(ScheduleApproval sa) {
        List<ScheduleApproval> list = getScheduleApprovals();
        if (list == null) {
            list = new ArrayList<ScheduleApproval>();
        }
        list.add(sa);
        sa.setSchedule(this);
        return sa;
    }

    /**
     * Gets the optimizer attempts to schedule.
     *
     * @return the optimizer attempts to schedule
     */
    public Integer getOptimizerAttemptsToSchedule() {
        return optimizerAttemptsToSchedule;
    }

    /**
     * Sets the optimizer attempts to schedule.
     *
     * @param optimizerAttemptsToSchedule the new optimizer attempts to schedule
     */
    public void setOptimizerAttemptsToSchedule(Integer optimizerAttemptsToSchedule) {
        this.optimizerAttemptsToSchedule = optimizerAttemptsToSchedule;
    }

    /**
     * Gets the optimizer return date time.
     *
     * @return the optimizer return date time
     */
    public String getOptimizerReturnDateTime() {
        if (optimizerReturnDateTimeMillis != null) {
            return ISODateTimeFormat.dateTimeNoMillis().print(this.optimizerReturnDateTimeMillis);
        }
        return null;
    }

    /**
     * Sets the optimizer return date time.
     *
     * @param optimizerReturnDateTime the new optimizer return date time
     */
    public void setOptimizerReturnDateTime(String optimizerReturnDateTime) {

    }

    /**
     * Gets the optimizer transaction id.
     *
     * @return the optimizer transaction id
     */
    public String getOptimizerTransactionId() {
        return optimizerTransactionId;
    }

    /**
     * Sets the optimizer transaction id.
     *
     * @param optimizerTransactionId the new optimizer transaction id
     */
    public void setOptimizerTransactionId(String optimizerTransactionId) {
        this.optimizerTransactionId = optimizerTransactionId;
    }

    /**
     * Gets the optimizer date time millis.
     *
     * @return the optimizer date time millis
     */
    public Long getOptimizerDateTimeMillis() {
        return optimizerDateTimeMillis;
    }

    /**
     * Sets the optimizer date time millis.
     *
     * @param optimizerDateTimeMillis the new optimizer date time millis
     */
    public void setOptimizerDateTimeMillis(Long optimizerDateTimeMillis) {
        this.optimizerDateTimeMillis = optimizerDateTimeMillis;
    }

    /**
     * Gets the optimizer return date time millis.
     *
     * @return the optimizer return date time millis
     */
    public Long getOptimizerReturnDateTimeMillis() {
        return optimizerReturnDateTimeMillis;
    }

    /**
     * Sets the optimizer return date time millis.
     *
     * @param optimizerReturnDateTimeMillis the new optimizer return date time millis
     */
    public void setOptimizerReturnDateTimeMillis(Long optimizerReturnDateTimeMillis) {
        this.optimizerReturnDateTimeMillis = optimizerReturnDateTimeMillis;
    }

    /**
     * Gets the groups.
     *
     * @return the groups
     */
    public List<ChangeManagementGroup> getGroups() {
        return groups;
    }

    /**
     * Sets the groups.
     *
     * @param groups the new groups
     */
    public void setGroups(List<ChangeManagementGroup> groups) {
        this.groups = groups;
    }

}
