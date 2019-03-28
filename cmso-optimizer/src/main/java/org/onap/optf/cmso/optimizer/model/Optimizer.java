/*
 * ============LICENSE_START==============================================
 * Copyright (c) 2019 AT&T Intellectual Property.
 * =======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * ============LICENSE_END=================================================
 *
 */

package org.onap.optf.cmso.optimizer.model;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the optimizer database table.
 *
 */
@Entity
@NamedQuery(name = "Optimizer.findAll", query = "SELECT o FROM Optimizer o")
public class Optimizer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private UUID uuid;

    @Column(name = "optimize_end")
    private Long optimizeEnd;

    @Column(name = "optimize_polling_interval")
    private Integer optimizePollingInterval;

    @Lob
    @Column(name = "optimize_response")
    private String optimizeResponse;

    @Column(name = "optimize_retries")
    private Integer optimizeRetries;

    @Column(name = "optimize_start")
    private Long optimizeStart;

    public Optimizer() {}

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getOptimizeEnd() {
        return this.optimizeEnd;
    }

    public void setOptimizeEnd(Long optimizeEnd) {
        this.optimizeEnd = optimizeEnd;
    }

    public Integer getOptimizePollingInterval() {
        return this.optimizePollingInterval;
    }

    public void setOptimizePollingInterval(Integer optimizePollingInterval) {
        this.optimizePollingInterval = optimizePollingInterval;
    }

    public String getOptimizeResponse() {
        return this.optimizeResponse;
    }

    public void setOptimizeResponse(String optimizeResponse) {
        this.optimizeResponse = optimizeResponse;
    }

    public Integer getOptimizeRetries() {
        return this.optimizeRetries;
    }

    public void setOptimizeRetries(Integer optimizeRetries) {
        this.optimizeRetries = optimizeRetries;
    }

    public Long getOptimizeStart() {
        return this.optimizeStart;
    }

    public void setOptimizeStart(Long optimizeStart) {
        this.optimizeStart = optimizeStart;
    }

}
