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

import com.google.ical.compat.jodatime.DateTimeIterator;
import com.google.ical.compat.jodatime.DateTimeIteratorFactory;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.onap.observations.Observation;
import org.onap.optf.cmso.optimizer.availability.policies.model.AllowedPeriodicTime;
import org.onap.optf.cmso.optimizer.availability.policies.model.TimeLimitAndVerticalTopology;
import org.onap.optf.cmso.optimizer.availability.policies.model.TimeRange;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.service.rs.models.ChangeWindow;

/**
 * The Class RecurringWindows.
 */
public class RecurringWindows {

    /**
     * Gets the availability windows for policies.
     *
     * @param policies the policies
     * @param changeWindow the change window
     * @return the availability windows for policies
     */
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
        // Collapse all duplicate and overlapping availabity windows into minimum
        // number of windows
        availableList = collapseWindows(availableList);
        return availableList;

    }


    private static List<ChangeWindow> collapseWindows(List<ChangeWindow> availableList) {
        List<ChangeWindow> collapsed = new ArrayList<>();
        Set<ChangeWindow> consumed = new HashSet<>();
        for (ChangeWindow win : availableList) {
            if (!consumed.contains(win)) {
                // Find all windows that can collapse into this one
                consumed.add(win);
                boolean allUnique = false;
                while (!allUnique) {
                    allUnique = true;
                    for (ChangeWindow test : availableList) {
                        // if availability windows overlap
                        if (!consumed.contains(test)) {
                            if (test.overlaps(win)) {
                                if (test.getStartTime().before(win.getStartTime())) {
                                    win.setStartTime(test.getStartTime());
                                }
                                if (test.getEndTime().after(win.getEndTime())) {
                                    win.setEndTime(test.getEndTime());
                                }
                                consumed.add(test);
                                allUnique = false;
                            }
                        }
                    }
                }
                collapsed.add(win);
            }
        }
        return collapsed;
    }

    // "RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR",
    private static void getAvailableWindowsForApt(AllowedPeriodicTime available, ChangeWindow changeWindow,
                    List<ChangeWindow> availableList) {

        if (available.getDay() != null) {
            switch (available.getDay()) {
                case weekday:
                case weekend:
                    getAvailableWindowsForAptDay(available, changeWindow, availableList);
                    return;
                default:

            }
        }
        availableList.add(changeWindow);
        Observation.report(LogMessages.UNSUPPORTED_PERIODIC_TIME, available.toString());

    }

    // "RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR",

    private static void getAvailableWindowsForAptDay(AllowedPeriodicTime available, ChangeWindow changeWindow,
                    List<ChangeWindow> availableList) {
        try {
            List<TimeRange> ranges = available.getTimeRange();
            if (ranges.size() == 0) {
                TimeRange range = new TimeRange();
                range.setStart_time("00:00:00+00:00");
                range.setStart_time("23:59:59+00:00");
                ranges.add(range);
            }
            StringBuilder rdata = new StringBuilder();
            rdata.append(available.getDay().getRrule()).append("\n");
            for (TimeRange range : ranges) {
                processRange(range, changeWindow, availableList, rdata);
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
    }


    private static void processRange(TimeRange range, ChangeWindow changeWindow, List<ChangeWindow> availableList,
                    StringBuilder rdata) throws ParseException {
        Date cwStartDate = changeWindow.getStartTime();
        Date cwEndDate = changeWindow.getEndTime();
        Instant cwStartInstant = Instant.ofEpochMilli(cwStartDate.getTime());
        Instant cwEndInstant = Instant.ofEpochMilli(cwEndDate.getTime());

        List<DateTime> startList = getRecurringList(range.getStart_time(), cwStartInstant, rdata, cwEndInstant);
        List<DateTime> endList = getRecurringList(range.getEnd_time(), cwStartInstant, rdata, cwEndInstant);
        // Pair them up to make change windows
        // Everything should be UTC time
        for (int i = 0; i < startList.size(); i++) {
            DateTime startDt = startList.get(i);
            if (i < endList.size()) {
                DateTime endDt = endList.get(i);
                if (endDt.isAfter(startDt)) {
                    ChangeWindow cw = new ChangeWindow();
                    cw.setStartTime(startDt.toDate());
                    cw.setEndTime(endDt.toDate());
                    availableList.add(cw);
                }

            }
        }

    }


    private static List<DateTime> getRecurringList(String rangeTime, Instant cwStartInstant, StringBuilder rdata,
                    Instant cwEndInstant) throws ParseException {

        Instant startInstant = getInstanceFromTime(rangeTime, cwStartInstant);
        DateTime start = new DateTime(startInstant.toEpochMilli());
        DateTimeIterator recur =
                        DateTimeIteratorFactory.createDateTimeIterator(rdata.toString(), start, DateTimeZone.UTC, true);
        List<DateTime> list = new ArrayList<>();
        while (recur.hasNext()) {
            DateTime next = recur.next();
            System.out.println(next.toString());
            if (next.isAfter(cwEndInstant.toEpochMilli())) {
                break;
            }
            list.add(next);
        }
        return list;
    }


    //
    // The policies with 'Day' enumeration only have time with no day so we add the
    // date portion of the change window to the dtstart
    //
    private static Instant getInstanceFromTime(String timeIn, Instant cwStartInstant) {
        Instant instant = null;
        Instant date = cwStartInstant.truncatedTo(ChronoUnit.DAYS);
        LocalDate epoch = LocalDate.ofEpochDay(0);
        try {
            OffsetTime offset = OffsetTime.parse(timeIn);
            OffsetDateTime odt = offset.atDate(epoch);
            ZonedDateTime startTime = odt.atZoneSameInstant(ZoneOffset.UTC.normalized());
            instant = Instant.from(startTime);
        } catch (Exception e) {
            LocalTime local = LocalTime.parse(timeIn);
            LocalDateTime ldt = local.atDate(epoch);
            ZonedDateTime startTime = ldt.atZone(ZoneOffset.UTC.normalized());
            instant = Instant.from(startTime);
        }
        return instant.plus(date.toEpochMilli(), ChronoUnit.MILLIS);
    }


}
