/*
 * Copyright © 2017-2018 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * Unless otherwise specified, all documentation contained herein is licensed under the Creative
 * Commons License, Attribution 4.0 Intl. (the "License"); you may not use this documentation except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.optf.cmso.optimizer.service.rs.models;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * The Class ChangeWindow.
 */
@ApiModel(value = "Change Window", description = "Time window for which tickets are to returned")
public class ChangeWindow implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(ChangeWindow.class);

    @ApiModelProperty(value = "Earliest time for which changes may begin.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date startTime;

    @ApiModelProperty(value = "Latest time by which all changes must be completed.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date endTime;

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time.
     *
     * @param startTime the new start time
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time.
     *
     * @return the end time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time.
     *
     * @param endTime the new end time
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Overlaps test instance.b
     *
     * @param test the test window
     * @return true, if successful
     */
    public boolean overlaps(ChangeWindow test) {
        int start = startTime.compareTo(test.getStartTime());
        int end = endTime.compareTo(test.getEndTime());
        int startend = startTime.compareTo(test.getEndTime());
        int endstart = endTime.compareTo(test.getStartTime());
        // at least one of the ends match up
        if (start == 0 || end == 0 || startend == 0 || endstart == 0) {
            return true;
        }
        // end is before start or start is before end, cannot overlap
        if (endstart == -1 || startend == 1) {
            return false;
        }
        return true;
    }

    /**
     * Test if this window contains the passed window.
     *
     * @param test the test
     * @return true, if this change window contains the passed change window
     */
    public boolean contains(ChangeWindow test) {
        if (!test.getStartTime().before(getStartTime()) && !test.getEndTime().after(getEndTime())) {
            return true;
        }
        return false;
    }

    /**
     * Passed slot time (test) is within this change window adjusted for the time zone of the element.
     * This is used to interpret global relative availability (maintenance) windows as opposed to
     * absolute UTC times provided in tickets which should already be adjusted for time zone.
     *
     * @param test the test
     * @param timeZoneOffset the time zone offset
     * @return true, if successful
     */
    public boolean containsInTimeZone(ChangeWindow test, Integer startTimeZoneOffset, Integer endTimeZoneOffset) {
        Instant startInstant = startTime.toInstant();
        Instant endInstant = endTime.toInstant();
        Instant testStart = test.getStartTime().toInstant().plusMillis(startTimeZoneOffset);;
        Instant testEnd = test.getEndTime().toInstant().plusMillis(startTimeZoneOffset);;
        if (!testStart.isBefore(startInstant)
                        && !testEnd.isAfter(endInstant)) {
            return true;
        }
        return false;
    }

    public boolean containsInTimeZone(ChangeWindow test, String timeZone) {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        Integer startTimeZoneOffset = tz.getOffset(startTime.toInstant().truncatedTo(ChronoUnit.DAYS).toEpochMilli());
        Integer endTimeZoneOffset = tz.getOffset(endTime.toInstant().truncatedTo(ChronoUnit.DAYS).toEpochMilli());
        return containsInTimeZone(test, startTimeZoneOffset, endTimeZoneOffset);
    }

    /**
     * Absorb if overlapping window.
     *
     * @param test the test window
     * @return true, if successful
     */
    public boolean absorbIfOverlapping(ChangeWindow test) {
        if (overlaps(test)) {
            if (test.getStartTime().before(getStartTime())) {
                setStartTime(test.getStartTime());
            }
            if (test.getEndTime().after(getEndTime())) {
                setEndTime(test.getEndTime());
            }
            return true;
        }
        return false;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.debug("Error in toString()", e);
        }
        return "";
    }


}
