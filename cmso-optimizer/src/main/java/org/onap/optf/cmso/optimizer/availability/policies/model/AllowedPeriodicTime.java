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
    "day": "weekday",
    "timeRange": [
        {
            "start_time": "00:00:00+00:00",
            "end_time": "06:00:00+00:00"
        }
    ]
}

 */
public class AllowedPeriodicTime {

    public enum Day
    {
        weekday("RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR"),
        weekend("RRULE:FREQ=WEEKLY;BYDAY=SA,SU"),
        ;
        private String rrule;
        private Day(String rrule) {this.rrule = rrule;}
        public String getRrule() {return rrule;}

    }

    private Day day;
    private List<TimeRange> timeRange;
    public Day getDay() {
        return day;
    }
    public void setDay(Day day) {
        this.day = day;
    }
    public List<TimeRange> getTimeRange() {
        return timeRange;
    }
    public void setTimeRange(List<TimeRange> timeRange) {
        this.timeRange = timeRange;
    }

}
