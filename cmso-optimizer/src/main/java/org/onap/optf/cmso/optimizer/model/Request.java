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
 * The persistent class for the request database table.
 *
 */
@Entity
@NamedQuery(name = "Request.findAll", query = "SELECT r FROM Request r")
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String uuid;

    @Column(name = "created_time")
    private BigInteger createdTime;

    @Lob
    private String request;

    @Column(name = "request_end")
    private BigInteger requestEnd;

    @Column(name = "request_start")
    private BigInteger requestStart;

    private String status;

    public Request() {}

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigInteger getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(BigInteger createdTime) {
        this.createdTime = createdTime;
    }

    public String getRequest() {
        return this.request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public BigInteger getRequestEnd() {
        return this.requestEnd;
    }

    public void setRequestEnd(BigInteger requestEnd) {
        this.requestEnd = requestEnd;
    }

    public BigInteger getRequestStart() {
        return this.requestStart;
    }

    public void setRequestStart(BigInteger requestStart) {
        this.requestStart = requestStart;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
