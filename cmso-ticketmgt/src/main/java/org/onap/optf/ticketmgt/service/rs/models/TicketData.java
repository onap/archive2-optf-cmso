/*******************************************************************************
 * 
 *  Copyright © 2019 AT&T Intellectual Property.
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *          http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  
 *  Unless otherwise specified, all documentation contained herein is licensed
 *  under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 *  you may not use this documentation except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *          https://creativecommons.org/licenses/by/4.0/
 *  
 *  Unless required by applicable law or agreed to in writing, documentation
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.onap.optf.ticketmgt.service.rs.models;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.onap.optf.ticketmgt.common.Availability;
import org.springframework.format.annotation.DateTimeFormat;

@ApiModel(value = "Ticket Data", description = "Change Management Ticket Information.")
public class TicketData implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(TicketData.class);

    @ApiModelProperty(value = "Unique ticket identifier")
    private String id;

    @ApiModelProperty(value = "Scheduled start time of change.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date startTime;

    @ApiModelProperty(value = "Scheduled end time of change.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date endTime;

    @ApiModelProperty(value = "Availability of element(s) during change window")
    private Availability availability;

    @ApiModelProperty(
                    value = "List elementIds  of elements being changed. At least one maps to elementId in the request")
    private List<String> elementIds = new ArrayList<>();

    @ApiModelProperty(value = "Details of the change.")
    private String changeDetails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public List<String> getElementIds() {
        return elementIds;
    }

    public void setElementIds(List<String> elementIds) {
        this.elementIds = elementIds;
    }

    public String getChangeDetails() {
        return changeDetails;
    }

    public void setChangeDetails(String changeDetails) {
        this.changeDetails = changeDetails;
    }

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
