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
 * The persistent class for the response database table.
 *
 */
@Entity
@NamedQuery(name = "Response.findAll", query = "SELECT r FROM Response r")
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String uuid;

    @Column(name = "delivered_time")
    private BigInteger deliveredTime;

    @Lob
    private String repsonse;

    public Response() {}

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigInteger getDeliveredTime() {
        return this.deliveredTime;
    }

    public void setDeliveredTime(BigInteger deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public String getRepsonse() {
        return this.repsonse;
    }

    public void setRepsonse(String repsonse) {
        this.repsonse = repsonse;
    }

}
