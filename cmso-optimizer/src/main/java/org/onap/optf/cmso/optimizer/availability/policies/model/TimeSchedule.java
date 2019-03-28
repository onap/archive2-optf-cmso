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
}
 */
public class TimeSchedule {

    private List<AllowedPeriodicTime> allowedPeriodicTime;

    public List<AllowedPeriodicTime> getAllowedPeriodicTime() {
        return allowedPeriodicTime;
    }

    public void setAllowedPeriodicTime(List<AllowedPeriodicTime> allowedPeriodicTime) {
        this.allowedPeriodicTime = allowedPeriodicTime;
    }

}
