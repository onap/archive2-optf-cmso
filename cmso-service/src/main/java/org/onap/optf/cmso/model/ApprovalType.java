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

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the approval_types database table.
 *
 */
@Entity
@Table(name = "APPROVAL_TYPES")
@NamedQuery(name = "ApprovalType.findAll", query = "SELECT a FROM ApprovalType a")
public class ApprovalType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private UUID uuid;

    @Column(name = "approval_count")
    private Integer approvalCount;

    @Column(name = "approval_type")
    private String approvalType;

    // bi-directional many-to-one association to Domain
    @Column(name = "domain")
    private String domain;

    /**
     * Instantiates a new approval type.
     */
    public ApprovalType() {}


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
     * Gets the approval count.
     *
     * @return the approval count
     */
    public Integer getApprovalCount() {
        return this.approvalCount;
    }

    /**
     * Sets the approval count.
     *
     * @param approvalCount the new approval count
     */
    public void setApprovalCount(Integer approvalCount) {
        this.approvalCount = approvalCount;
    }

    /**
     * Gets the approval type.
     *
     * @return the approval type
     */
    public String getApprovalType() {
        return this.approvalType;
    }

    /**
     * Sets the approval type.
     *
     * @param approvalType the new approval type
     */
    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
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

}
