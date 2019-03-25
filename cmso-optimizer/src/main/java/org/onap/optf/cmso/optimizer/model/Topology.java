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
import java.math.BigInteger;
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
    private String uuid;

    @Lob
    private String topology;

    @Column(name = "topology_end")
    private BigInteger topologyEnd;

    @Column(name = "topology_polling_interval")
    private int topologyPollingInterval;

    @Column(name = "topology_retries")
    private int topologyRetries;

    @Column(name = "topology_start")
    private BigInteger topologyStart;

    public Topology() {}

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTopology() {
        return this.topology;
    }

    public void setTopology(String topology) {
        this.topology = topology;
    }

    public BigInteger getTopologyEnd() {
        return this.topologyEnd;
    }

    public void setTopologyEnd(BigInteger topologyEnd) {
        this.topologyEnd = topologyEnd;
    }

    public int getTopologyPollingInterval() {
        return this.topologyPollingInterval;
    }

    public void setTopologyPollingInterval(int topologyPollingInterval) {
        this.topologyPollingInterval = topologyPollingInterval;
    }

    public int getTopologyRetries() {
        return this.topologyRetries;
    }

    public void setTopologyRetries(int topologyRetries) {
        this.topologyRetries = topologyRetries;
    }

    public BigInteger getTopologyStart() {
        return this.topologyStart;
    }

    public void setTopologyStart(BigInteger topologyStart) {
        this.topologyStart = topologyStart;
    }

}
