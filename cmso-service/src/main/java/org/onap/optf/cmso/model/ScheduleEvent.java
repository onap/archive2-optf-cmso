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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.joda.time.format.ISODateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The persistent class for the schedule_events database table.
 * 
 */
@Entity
@Table(name = "SCHEDULE_EVENTS")
@NamedQuery(name = "ScheduleEvent.findAll", query = "SELECT s FROM ScheduleEvent s")
public class ScheduleEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String domain;

    @Lob
    @Column(name = "event_text")
    private String eventText;

    @JsonIgnore
    @Column(name = "event_time")
    private Long eventTimeMillis;

    @JsonProperty
    @Transient
    private String eventTime;

    @JsonIgnore
    @Column(name = "reminder_time")
    private Long reminderTimeMillis;

    @JsonProperty
    @Transient
    private String reminderTime;

    @Column(name = "schedules_id")
    private Integer schedulesId;

    private String status;

    public ScheduleEvent() {}

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getEventText() {
        return this.eventText;
    }

    public void setEventText(String eventText) {
        this.eventText = eventText;
    }

    public Integer getSchedulesId() {
        return this.schedulesId;
    }

    public void setSchedulesId(Integer schedulesId) {
        this.schedulesId = schedulesId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEventTimeMillis() {
        return eventTimeMillis;
    }

    public void setEventTimeMillis(Long eventTimeMillis) {
        this.eventTimeMillis = eventTimeMillis;
    }

    public String getEventTime() {
        if (eventTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(this.eventTimeMillis);
        return null;
    }

    public void setEventTime(String eventTime) {}

    public Long getReminderTimeMillis() {
        return reminderTimeMillis;
    }

    public void setReminderTimeMillis(Long reminderTimeMillis) {
        this.reminderTimeMillis = reminderTimeMillis;
    }

    public String getReminderTime() {
        if (reminderTimeMillis != null)
            return ISODateTimeFormat.dateTimeNoMillis().print(this.reminderTimeMillis);
        return null;
    }

    public void setReminderTime(String reminderTime) {}

}
