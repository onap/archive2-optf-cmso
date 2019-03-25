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

package org.onap.optf.cmso.service.rs.models;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The persistent class for the approval_types database table.
 *
 */
@ApiModel(value = "Schedule Request", description = "Request to schedule VNF change management workflow(s).")
public abstract class ScheduleMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(ScheduleMessage.class);

    /**
     * Gets the scheduling info.
     *
     * @return the scheduling info
     */
    public abstract Object getSchedulingInfo();

    /**
     * Sets the scheduling info.
     *
     * @param info the new scheduling info
     */
    public abstract void setSchedulingInfo(Object info);

    // public abstract void setSchedulingInfo(Object schedulingInfo);

    @ApiModelProperty(value = "Schedule domain : ChangeManagement")
    private String domain;

    @ApiModelProperty(value = "Schedule id that must be unique within the domain. Use of UUID is highly recommended.")
    private String scheduleId;

    @ApiModelProperty(value = "User provided name of the schedule (deaults to scheduleId")
    private String scheduleName;

    @ApiModelProperty(value = "ATTUID of the user requesting the schedule.")
    private String userId;

    @ApiModelProperty(value = "Domain data as name value/pairs. (i.e. CallbackUrl, CallbackData, WorkflowName)")
    private List<Map<String, String>> domainData;

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
     * Gets the schedule id.
     *
     * @return the schedule id
     */
    public String getScheduleId() {
        return scheduleId;
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
        return scheduleName;
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
     * Gets the user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
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
     * Gets the domain data.
     *
     * @return the domain data
     */
    public List<Map<String, String>> getDomainData() {
        return domainData;
    }

    /**
     * Sets the domain data.
     *
     * @param domainData the domain data
     */
    public void setDomainData(List<Map<String, String>> domainData) {
        this.domainData = domainData;
    }

    /**
     * To string.
     *
     * @return the string
     */
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
