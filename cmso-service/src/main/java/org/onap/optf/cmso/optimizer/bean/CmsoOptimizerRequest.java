/*
 * Copyright � 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright � 2018 IBM.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         https://creativecommons.org/licenses/by/4.0/
 * 
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.onap.optf.cmso.optimizer.bean;

public class CmsoOptimizerRequest {

    /*
     * 
     * { "schedulingInfo": { "scheduleId": "CM-<__SCHEDULE ID__>", "startTime":
     * "2017-02-15T00:00:00+05:00", "endTime": "2017-02-18T23:59:00+05:00",
     * "normalDurationInSecs": 60, "additionalDurationInSecs": 0, // for backout
     * "concurrencyLimit": 10, "policyId": ["SNIRO.TimeLimitAndVerticalTopology"],
     * "vnfDetails": [{ “node�?: "satmo415vbc", “groupId�?: “group1�?//optional }, {
     * “node�?: "satmo415vbc", “groupId�?: “group1�?//optional }] }, "requestInfo": {
     * “transactionId�?: �?__TRANSACTIONID__�?, //logging "requestId":
     * "CM-<__SCHEDULE ID__>", "sourceId": "cm-portal", “optimizer�?: [“scheduling�?],
     * "callbackUrl": "http://callbackurl.onap.org:8080/callback" } }
     * 
     */

    private CmsoSchedulingInfo schedulingInfo;
    private CmsoRequestInfo requestInfo;

    public CmsoOptimizerRequest() {
        schedulingInfo = new CmsoSchedulingInfo();
        requestInfo = new CmsoRequestInfo();
    }

    public CmsoSchedulingInfo getSchedulingInfo() {
        return schedulingInfo;
    }

    public void setSchedulingInfo(CmsoSchedulingInfo schedulingInfo) {
        this.schedulingInfo = schedulingInfo;
    }

    public CmsoRequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(CmsoRequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

}
