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

package org.onap.optf.cmso.optimizer.availability.policies;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.optf.cmso.optimizer.availability.policies.model.Policy;
import org.onap.optf.cmso.optimizer.availability.policies.model.TimeLimitAndVerticalTopology;
import org.springframework.core.env.Environment;

@RunWith(MockitoJUnitRunner.class)
public class PolicyManagerTest
{

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
    public void getPolicyByName() {
        String policyName = "Weekday_00_06";

        String result = "CMSO.Weekday_00_06,";
        List<Policy> policies = policyManager.getSupportedPolicies();
        StringBuilder sb = new StringBuilder();
        for (Policy pol : policies)
        {
            sb.append(pol.getPolicyName()).append("," );
        }
        System.out.println("        String result = \"" + sb.toString() + "\";");
        Assert.assertTrue(result.equals(sb.toString()));
        Policy policy = policyManager.getPolicyForName(policyName);
        Assert.assertTrue(policy != null);
        TimeLimitAndVerticalTopology top = policyManager.getTimeLimitAndVerticalTopologyByName(policyName);
        Assert.assertTrue(top != null);

    }
}