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
    "serviceType": "networkOnDemand",
    "identity": "vnf_upgrade_policy",
    "policyScope": {
        "serviceType": ["networkOnDemand"],
        "aicZone": [
            " "
        ],
        "entityType": ["vnf"]
    },
    "timeSchedule": {
        "allowedPeriodicTime": [
            {
                "day": "weekday",
                "timeRange": [
                    {
                        "start_time": "00:00:00+00:00",
                        "end_time": "06:00:00+00:00"
                    }
                ]
            }
        ]
    },
    "nodeType": ["vnf"],
    "type": "timeLimitAndVerticalTopology",
    "conflictScope": "vnf_pserver"
}
 */

public class TimeLimitAndVerticalTopology
{

    public enum ConflictScope {
        timeLimitAndVerticalTopology,
    }
    public enum Type {
        vnf_pserver,
    }

    private String serviceType;
    private String identity;
    private PolicyScope policyScope;
    private TimeSchedule timeSchedule;
    private List<String> nodeType;
    private String type;
    private String conflictScope;

    public String getServiceType() {
        return serviceType;
    }
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    public String getIdentity() {
        return identity;
    }
    public void setIdentity(String identity) {
        this.identity = identity;
    }
    public PolicyScope getPolicyScope() {
        return policyScope;
    }
    public void setPolicyScope(PolicyScope policyScope) {
        this.policyScope = policyScope;
    }
    public TimeSchedule getTimeSchedule() {
        return timeSchedule;
    }
    public void setTimeSchedule(TimeSchedule timeSchedule) {
        this.timeSchedule = timeSchedule;
    }
    public List<String> getNodeType() {
        return nodeType;
    }
    public void setNodeType(List<String> nodeType) {
        this.nodeType = nodeType;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getConflictScope() {
        return conflictScope;
    }
    public void setConflictScope(String conflictScope) {
        this.conflictScope = conflictScope;
    }

}
