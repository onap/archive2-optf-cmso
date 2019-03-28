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

package org.onap.optf.cmso.optimizer.availability.timewindows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.onap.observations.Observation;
import org.onap.optf.cmso.optimizer.availability.policies.model.AllowedPeriodicTime;
import org.onap.optf.cmso.optimizer.availability.policies.model.TimeLimitAndVerticalTopology;
import org.onap.optf.cmso.optimizer.availability.policies.model.TimeRange;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.service.rs.models.ChangeWindow;

public class RecurringWindows {

    public static List<ChangeWindow> getAvailabilityWindowsForPolicies(List<TimeLimitAndVerticalTopology> policies,
                    ChangeWindow changeWindow) {
        List<ChangeWindow> availableList = new ArrayList<>();
        for (TimeLimitAndVerticalTopology policy : policies) {
            if (policy.getTimeSchedule() != null && policy.getTimeSchedule().getAllowedPeriodicTime() != null) {
                for (AllowedPeriodicTime available : policy.getTimeSchedule().getAllowedPeriodicTime()) {
                    getAvailableWindowsForApt(available, changeWindow, availableList);
                }
            }
        }
        return availableList;

    }


    // "RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR",
    private static void getAvailableWindowsForApt(AllowedPeriodicTime available, ChangeWindow changeWindow,
                    List<ChangeWindow> availableList) {

        if (available.getDay() != null)
        {
            switch (available.getDay())
            {
                case weekday:
                case weekend:
                    getAvailableWindowsForAptDay(available, changeWindow, availableList);
                    return;
                default:

            }
        }
        Observation.report(LogMessages.UNSUPPORTED_PERIODIC_TIME, available.toString());

    }
    // "RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR",
    private static void getAvailableWindowsForAptDay(AllowedPeriodicTime available, ChangeWindow changeWindow,
                    List<ChangeWindow> availableList) {
        try
        {
            List<TimeRange> ranges = available.getTimeRange();
            if (ranges.size() == 0)
            {
                TimeRange range = new TimeRange();
                range.setStart_time("00:00:00+00:00");
                range.setStart_time("23:59:59+00:00");
                ranges.add(range);
            }
            String rrule = available.getDay().getRrule();
            for (TimeRange range : ranges)
            {

                Date cwStartDate =changeWindow.getStartTime();
                Date cwEndDate =changeWindow.getEndTime();

                Instant cwStartInstant = Instant.ofEpochMilli(cwStartDate.getTime());
                Instant cwEndInstant = Instant.ofEpochMilli(cwEndDate.getTime());
                Instant startInstant = Instant.parse(range.getStart_time());
                Instant endInstant = Instant.parse(range.getEnd_time());
                if (cwStartInstant.isAfter(startInstant))
                {
                    // We expect this since startInstant has no  date (1/1/1970)
                    //
                }


            }
        }
        catch (Exception e)
        {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
    }

}
