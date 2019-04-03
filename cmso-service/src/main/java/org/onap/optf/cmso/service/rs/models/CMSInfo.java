/*
 * Copyright © 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright © 2018 IBM.
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

/**
 * The persistent class for the approval_types database table.
 *
 */
@ApiModel(value = "Change Management Scheduling Info", description = "Details of schedule being requested")
public class CMSInfo implements Serializable {
    private static EELFLogger log = EELFManager.getInstance().getLogger(CMSInfo.class);

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Expected duration (in seconds) of a successful execution of a single VNF change.")
    private Integer normalDurationInSeconds;

    @ApiModelProperty(
            value = "Additional duration (in seconds) to be added to support backout of an unsuccessful VNF change.")
    private Integer additionalDurationInSeconds;

    @ApiModelProperty(value = "Maximum number of VNF changes to schedule concurrently")
    private Integer concurrencyLimit;

    @ApiModelProperty(
            value = "Name of schedule optimization policy used by the"
                            + " change management cmso optimizer to determine available time slot")
    private String policyId;

    @ApiModelProperty(value = "Lists of the VNFs to be changed and the desired change windows")
    private List<VnfDetailsMessage> vnfDetails;

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

    public Integer getNormalDurationInSeconds() {
        return normalDurationInSeconds;
    }

    public void setNormalDurationInSeconds(Integer normalDurationInSeconds) {
        this.normalDurationInSeconds = normalDurationInSeconds;
    }

    public Integer getAdditionalDurationInSeconds() {
        return additionalDurationInSeconds;
    }

    public void setAdditionalDurationInSeconds(Integer additionalDurationInSeconds) {
        this.additionalDurationInSeconds = additionalDurationInSeconds;
    }

    public Integer getConcurrencyLimit() {
        return concurrencyLimit;
    }

    public void setConcurrencyLimit(Integer concurrencyLimit) {
        this.concurrencyLimit = concurrencyLimit;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public List<VnfDetailsMessage> getVnfDetails() {
        return vnfDetails;
    }

    public void setVnfDetails(List<VnfDetailsMessage> vnfDetails) {
        this.vnfDetails = vnfDetails;
    }
}
