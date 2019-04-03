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
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.optf.cmso.optimizer.availability.policies.PolicyManager;
import org.onap.optf.cmso.optimizer.availability.policies.model.TimeLimitAndVerticalTopology;
import org.onap.optf.cmso.optimizer.service.rs.models.ChangeWindow;
import org.springframework.core.env.Environment;

@RunWith(MockitoJUnitRunner.class)
public class RecurringWindowsTest {


    @InjectMocks
    private PolicyManager policyManager;

    @Mock
    public Environment env;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(env.getProperty("cmso.local.policy.folder", "data/policies")).thenReturn("data/policies");
    }

    @Test
    public void getAvailabilityWindowsForPolicies() {
        getAvailabilityWindowsForPolicy("AllDayEveryDay", "2019-03-08T00:00:00.00Z", "2019-03-12T00:00:00.00Z", 2);
        getAvailabilityWindowsForPolicy("Weekday_00_06", "2019-03-08T00:00:00.00Z", "2019-03-12T00:00:00.00Z", 2);
        getAvailabilityWindowsForPolicy("EveryDay_00_06", "2019-03-08T00:00:00.00Z", "2019-03-12T00:00:00.00Z", 4);
        getAvailabilityWindowsForPolicy("Weekend_00_06", "2019-03-08T00:00:00.00Z", "2019-03-12T00:00:00.00Z", 3);
    }

    private void getAvailabilityWindowsForPolicy(String policyName, String startStr, String endStr, int size) {
        TimeLimitAndVerticalTopology top = policyManager.getTimeLimitAndVerticalTopologyByName(policyName);
        Assert.assertTrue(top != null);
        List<TimeLimitAndVerticalTopology> topList = new ArrayList<>();
        topList.add(top);
        ChangeWindow changeWindow = new ChangeWindow();
        Instant start = Instant.parse(startStr);
        Instant end = Instant.parse(endStr);
        changeWindow.setStartTime(Date.from(start));
        changeWindow.setEndTime(Date.from(end));
        List<ChangeWindow> windows = RecurringWindows.getAvailabilityWindowsForPolicies(topList, changeWindow);
        Assert.assertTrue(windows != null);
        Assert.assertTrue(windows.size() == size);

    }
}
