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

    public Schedule() {}

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setDomainData(List<DomainData> domainData) {
        this.domainData = domainData;
    }

    public void setScheduleApprovals(List<ScheduleApproval> scheduleApprovals) {
        this.scheduleApprovals = scheduleApprovals;
    }

    public String getCreateDateTime() {
        if (createDateTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(this.createDateTimeMillis);
        return null;
    }

    public void setCreateDateTime(String datetime) {
        // only set time via setCreateDateTimeMillis
    }

    public void setCreateDateTimeMillis(Long millis) {
        this.createDateTimeMillis = millis;
    }

    public long getCreateDateTimeMillis() {
        return this.createDateTimeMillis;
    }

    public Long getDeleteDateTimeMillis() {
        return deleteDateTimeMillis;
    }

    public void setDeleteDateTimeMillis(Long deleteDateTimeMillis) {
        this.deleteDateTimeMillis = deleteDateTimeMillis;
    }

    public String getDeleteDateTime() {
        if (deleteDateTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(this.deleteDateTimeMillis);
        return null;
    }

    public void setDeleteDateTime(String deleteDateTime) {}

    public String getOptimizerDateTime() {
        if (optimizerDateTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(this.optimizerDateTimeMillis);
        return null;
    }

    public void setOptimizerDateTime(String optimizerDateTime) {}

    public String getOptimizerMessage() {
        return this.optimizerMessage;
    }

    public void setOptimizerMessage(String optimizerMessage) {
        this.optimizerMessage = optimizerMessage;
    }

    public String getOptimizerStatus() {
        return this.optimizerStatus;
    }

    public void setOptimizerStatus(String optimizerStatus) {
        this.optimizerStatus = optimizerStatus;
    }

    public String getSchedule() {
        return this.schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getScheduleId() {
        return this.scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleName() {
        return this.scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getScheduleInfo() {
        return this.scheduleInfo;
    }

    public void setScheduleInfo(String scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }

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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<DomainData> getDomainData() {
        return domainData;
    }

    public List<ScheduleApproval> getScheduleApprovals() {
        return scheduleApprovals;
    }

    public DomainData addDomainData(DomainData domainData) {
        List<DomainData> list = getDomainData();
        if (list == null)
            list = new ArrayList<DomainData>();
        list.add(domainData);
        domainData.setSchedule(this);
        return domainData;
    }

    public ScheduleApproval addScheduleApproval(ScheduleApproval sa) {
        List<ScheduleApproval> list = getScheduleApprovals();
        if (list == null)
            list = new ArrayList<ScheduleApproval>();
        list.add(sa);
        sa.setSchedule(this);
        return sa;
    }

    public Integer getOptimizerAttemptsToSchedule() {
        return optimizerAttemptsToSchedule;
    }

    public void setOptimizerAttemptsToSchedule(Integer optimizerAttemptsToSchedule) {
        this.optimizerAttemptsToSchedule = optimizerAttemptsToSchedule;
    }

    public String getOptimizerReturnDateTime() {
        if (optimizerReturnDateTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(this.optimizerReturnDateTimeMillis);
        return null;
    }

    public void setOptimizerReturnDateTime(String optimizerReturnDateTime) {

    }

    public String getOptimizerTransactionId() {
        return optimizerTransactionId;
    }

    public void setOptimizerTransactionId(String optimizerTransactionId) {
        this.optimizerTransactionId = optimizerTransactionId;
    }

    public Long getOptimizerDateTimeMillis() {
        return optimizerDateTimeMillis;
    }

    public void setOptimizerDateTimeMillis(Long optimizerDateTimeMillis) {
        this.optimizerDateTimeMillis = optimizerDateTimeMillis;
    }

    public Long getOptimizerReturnDateTimeMillis() {
        return optimizerReturnDateTimeMillis;
    }

    public void setOptimizerReturnDateTimeMillis(Long optimizerReturnDateTimeMillis) {
        this.optimizerReturnDateTimeMillis = optimizerReturnDateTimeMillis;
    }

    public List<ChangeManagementGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ChangeManagementGroup> groups) {
        this.groups = groups;
    }

}
