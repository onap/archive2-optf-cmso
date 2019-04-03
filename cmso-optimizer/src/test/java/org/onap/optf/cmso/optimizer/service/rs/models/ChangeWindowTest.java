package org.onap.optf.cmso.optimizer.service.rs.models;

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

import java.time.Instant;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChangeWindowTest {


    @Test
    public void chagneWindowTest() {
        ChangeWindow window = new ChangeWindow();
        window.setStartTime(Date.from(Instant.parse("2019-03-08T00:00:00.00Z")));
        window.setEndTime(Date.from(Instant.parse("2019-03-12T00:00:00.00Z")));
        testContains(window, "2019-03-08T00:00:00.00Z", "2019-03-12T00:00:00.00Z", true);
        testContains(window, "2019-03-07T23:59:59Z", "2019-03-12T00:00:00.00Z", false);
        testContains(window, "2019-03-09T23:59:59Z", "2019-03-11T00:00:00.00Z", true);
        testContains(window, "2019-03-06T23:59:59Z", "2019-03-06T23:59:59Z", false);

        String tz = "US/Eastern";
        window.setStartTime(Date.from(Instant.parse("2019-03-08T00:00:00.00Z")));
        window.setEndTime(Date.from(Instant.parse("2019-03-08T06:00:00.00Z")));
        testContainsTz(window, "2019-03-08T06:00:00Z", "2019-03-08T07:00:00.00Z", tz, true);
        testContainsTz(window, "2019-03-08T00:00:00Z", "2019-03-08T01:00:00.00Z", tz, false);

    }

    private void testContains(ChangeWindow window, String from, String to, boolean contains) {
        ChangeWindow test = new ChangeWindow();
        test.setStartTime(Date.from(Instant.parse(from)));
        test.setEndTime(Date.from(Instant.parse(to)));
        Assert.assertTrue(window.contains(test) == contains);
    }

    private void testContainsTz(ChangeWindow window, String from, String to, String tz,  boolean contains) {
        ChangeWindow test = new ChangeWindow();
        test.setStartTime(Date.from(Instant.parse(from)));
        test.setEndTime(Date.from(Instant.parse(to)));
        Assert.assertTrue(window.containsInTimeZone(test, tz) == contains);
    }

}
