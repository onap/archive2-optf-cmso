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
import javax.persistence.Table;


/**
 * The persistent class for the tickets database table.
 *
 */
@Entity
@Table(name = "tickets")
@NamedQuery(name = "Ticket.findAll", query = "SELECT t FROM Ticket t")
public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private UUID uuid;

    @Lob
    private String tickets;

    @Column(name = "tickets_end")
    private Long ticketsEnd;

    @Column(name = "tickets_retries")
    private Integer ticketsRetries;

    @Column(name = "tickets_start")
    private Long ticketsStart;

    @Column(name = "topology_polling_interval")
    private Integer topologyPollingInterval;

    public Ticket() {}

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getTickets() {
        return this.tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }

    public Long getTicketsEnd() {
        return this.ticketsEnd;
    }

    public void setTicketsEnd(Long ticketsEnd) {
        this.ticketsEnd = ticketsEnd;
    }

    public Integer getTicketsRetries() {
        return this.ticketsRetries;
    }

    public void setTicketsRetries(Integer ticketsRetries) {
        this.ticketsRetries = ticketsRetries;
    }

    public Long getTicketsStart() {
        return this.ticketsStart;
    }

    public void setTicketsStart(Long ticketsStart) {
        this.ticketsStart = ticketsStart;
    }

    public Integer getTopologyPollingInterval() {
        return this.topologyPollingInterval;
    }

    public void setTopologyPollingInterval(Integer topologyPollingInterval) {
        this.topologyPollingInterval = topologyPollingInterval;
    }

}
