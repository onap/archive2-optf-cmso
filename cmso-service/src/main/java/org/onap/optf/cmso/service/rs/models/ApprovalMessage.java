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
import java.sql.Timestamp;
import org.onap.optf.cmso.common.ApprovalStatusEnum;
import org.onap.optf.cmso.common.ApprovalTypesEnum;

/**
 * The persistent class for the approval_types database table.
 *
 */
@ApiModel(value = "Schedule Approval Request", description = "Request to accept or reject an optimized time slot.")
public class ApprovalMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(ApprovalMessage.class);

    @ApiModelProperty(value = "ATTUID of the user accepting/rejecting the time slot.")
    private String approvalUserId;

    @ApiModelProperty(value = "Approval status.")
    private ApprovalStatusEnum approvalStatus;

    @ApiModelProperty(value = "Type of approval.", allowableValues = "Tier 2")
    private ApprovalTypesEnum approvalType;

    private Timestamp approvalDateTime;

    /**
     * Gets the approval user id.
     *
     * @return the approval user id
     */
    public String getApprovalUserId() {
        return approvalUserId;
    }

    /**
     * Sets the approval user id.
     *
     * @param approvalUserId the new approval user id
     */
    public void setApprovalUserId(String approvalUserId) {
        this.approvalUserId = approvalUserId;
    }

    /**
     * Gets the approval status.
     *
     * @return the approval status
     */
    public ApprovalStatusEnum getApprovalStatus() {
        return approvalStatus;
    }

    /**
     * Sets the approval status.
     *
     * @param approvalStatus the new approval status
     */
    public void setApprovalStatus(ApprovalStatusEnum approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    /**
     * Gets the approval type.
     *
     * @return the approval type
     */
    public ApprovalTypesEnum getApprovalType() {
        return approvalType;
    }

    /**
     * Sets the approval type.
     *
     * @param approvalType the new approval type
     */
    public void setApprovalType(ApprovalTypesEnum approvalType) {
        this.approvalType = approvalType;
    }

    /**
     * Gets the approval date time.
     *
     * @return the approval date time
     */
    public Timestamp getApprovalDateTime() {
        return approvalDateTime;
    }

    /**
     * Sets the approval date time.
     *
     * @param approvalDateTime the new approval date time
     */
    public void setApprovalDateTime(Timestamp approvalDateTime) {
        this.approvalDateTime = approvalDateTime;
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
