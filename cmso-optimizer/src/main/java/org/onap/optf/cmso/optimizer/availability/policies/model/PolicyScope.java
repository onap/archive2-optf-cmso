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

package org.onap.optf.cmso.optimizer.availability.policies.model;

import java.util.List;

/*
{
    "serviceType": ["networkOnDemand"],
    "aicZone": [
        " "
    ],
    "entityType": ["vnf"]
 */
public class PolicyScope {

    public enum ServiceType {
        networkOnDemand
    }

    public enum EntityType {
        vnf
    }

    private List<String> serviceType;
    private List<String> aicZone;
    private List<String> entityType;

    public List<String> getServiceType() {
        return serviceType;
    }

    public void setServiceType(List<String> serviceType) {
        this.serviceType = serviceType;
    }

    public List<String> getAicZone() {
        return aicZone;
    }

    public void setAicZone(List<String> aicZone) {
        this.aicZone = aicZone;
    }

    public List<String> getEntityType() {
        return entityType;
    }

    public void setEntityType(List<String> entityType) {
        this.entityType = entityType;
    }

}
