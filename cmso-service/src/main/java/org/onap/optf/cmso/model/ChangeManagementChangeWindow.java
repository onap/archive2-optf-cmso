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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.joda.time.format.ISODateTimeFormat;

/**
 * The persistent class for the change_management_change_windows database table.
 *
 */
@Entity
@Table(name = "CHANGE_MANAGEMENT_CHANGE_WINDOWS")
@NamedQuery(name = "ChangeManagementChangeWindow.findAll", query = "SELECT c FROM ChangeManagementChangeWindow c")
@ApiModel(value = "Change Window", description = "Desired window within which the VNF changes are to be execututed.")
public class ChangeManagementChangeWindow implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Id
    private UUID uuid;

    @JsonIgnore
    @Column(name = "finish_time")
    private Long finishTimeMillis;

    @ApiModelProperty(value = "Time by which all changes must be completed")
    @JsonProperty
    @Transient
    private String finishTime;

    @JsonIgnore
    @Column(name = "start_time")
    private Long startTimeMillis;

    @ApiModelProperty(value = "Earliest date/time to initiate changes")
    @JsonProperty
    @Transient
    private String startTime;

    @JsonIgnore
    @Column(name = "change_management_group_uuid")
    private UUID changeManagementGroupUuid;

    /**
     * Instantiates a new change management change window.
     */
    public ChangeManagementChangeWindow() {}


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

}
