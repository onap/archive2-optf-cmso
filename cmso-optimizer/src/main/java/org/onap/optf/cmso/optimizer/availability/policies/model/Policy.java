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

/*
{
    "service": "TimeLimitAndVerticalTopology",
    "policyName": "CMSO.Weekday_00_06",
    "description": "dev instance",
    "templateVersion": "Dublin",
    "version": "0001",
    "priority": "4",
    "riskType": "test",
    "riskLevel": "3",
    "guard": "False",
    "content": {
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
}
 */
public class Policy {

    private String service;
    private String policyName;
    private String description;
    private String templateVersion;
    private String version;
    private String priority;
    private String riskType;
    private String riskLevel;
    private String guard;
    private Object content;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

}
