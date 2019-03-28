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
 * The persistent class for the topology database table.
 *
 */
@Entity
@NamedQuery(name = "Topology.findAll", query = "SELECT t FROM Topology t")
public class Topology implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private UUID uuid;

    @Lob
    private String topology;

    @Column(name = "topology_end")
    private Long topologyEnd;

    @Column(name = "topology_polling_interval")
    private Integer topologyPollingInterval;

    @Column(name = "topology_retries")
    private Integer topologyRetries;

    @Column(name = "topology_start")
    private Long topologyStart;

    public Topology() {}

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getTopology() {
        return this.topology;
    }

    public void setTopology(String topology) {
        this.topology = topology;
    }

    public Long getTopologyEnd() {
        return this.topologyEnd;
    }

    public void setTopologyEnd(Long topologyEnd) {
        this.topologyEnd = topologyEnd;
    }

    public Integer getTopologyPollingInterval() {
        return this.topologyPollingInterval;
    }

    public void setTopologyPollingInterval(Integer topologyPollingInterval) {
        this.topologyPollingInterval = topologyPollingInterval;
    }

    public Integer getTopologyRetries() {
        return this.topologyRetries;
    }

    public void setTopologyRetries(Integer topologyRetries) {
        this.topologyRetries = topologyRetries;
    }

    public Long getTopologyStart() {
        return this.topologyStart;
    }

    public void setTopologyStart(Long topologyStart) {
        this.topologyStart = topologyStart;
    }

}
