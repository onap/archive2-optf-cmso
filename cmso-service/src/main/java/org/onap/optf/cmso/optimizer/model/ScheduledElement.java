/*
 *
 * Copyright © 2019 AT&T Intellectual Property.
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
 ******************************************************************************/

package org.onap.optf.cmso.optimizer.model;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

@ApiModel(value = "Scheduled Element", description = "Scheduled element returned by the optimizer.")
public class ScheduledElement implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(ScheduledElement.class);

    public enum ScheduleType {
        UNKNOWN, GROUP_DISPATCH, INDIVIDUAL,
    }

    @ApiModelProperty(value = "Element identifier")
    private String elementId;

    @ApiModelProperty(value = "Group identifier")
    private String groupId;

    private ScheduleType scheduleType = ScheduleType.UNKNOWN;

    @ApiModelProperty(value = "Earliest time for which changes may begin.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date startTime;

    @ApiModelProperty(value = "Latest time by which all changes must be completed.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date endTime;

    @ApiModelProperty(value = "Expected duration of change in seconds.")
    private Long durationSeconds;

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.debug("Error in toString()", e);
        }
        return "";
    }
}
