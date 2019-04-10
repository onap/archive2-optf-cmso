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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/*

{
    "start_time": "00:00:00+00:00",
    "end_time": "06:00:00+00:00"
}

 */
public class TimeRange {
    private String startTime;
    private String endTime;

    @JsonGetter("start_time")
    public String getStartTime() {
        return startTime;
    }

    @JsonSetter("start_time")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @JsonGetter("end_time")
    public String getEndTime() {
        return endTime;
    }

    @JsonSetter("end_time")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


}
