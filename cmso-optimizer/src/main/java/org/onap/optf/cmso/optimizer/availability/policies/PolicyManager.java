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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.onap.optf.cmso.optimizer.availability.policies.model.Policy;
import org.onap.optf.cmso.optimizer.availability.policies.model.TimeLimitAndVerticalTopology;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.observations.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PolicyManager {

    @Autowired
    Environment env;

    public TimeLimitAndVerticalTopology getTimeLimitAndVerticalTopologyByName(String name) {
        Policy policy = getPolicyForName(name);
        TimeLimitAndVerticalTopology returnPolicy = null;
        if (policy != null) {
            ObjectMapper om = new ObjectMapper();
            try {
                returnPolicy = om.convertValue(policy.getContent(), TimeLimitAndVerticalTopology.class);
            } catch (Exception e) {
                Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            }
        }
        return returnPolicy;
    }


    public Policy getPolicyForName(String name) {
        Policy policy = null;
        try {
            return getLocalPolicyForName(name);
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return policy;
    }

    public Policy getLocalPolicyForName(String name) {
        String policyFolder = env.getProperty("cmso.local.policy.folder", "data/policies");
        Policy policy = null;
        try {
            if (!name.endsWith(".json")) {
                name += ".json";
            }
            Path path = Paths.get(policyFolder, name);
            ObjectMapper om = new ObjectMapper();
            policy = om.readValue(path.toFile(), Policy.class);
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return policy;
    }

    public List<Policy> getSupportedPolicies() {
        List<Policy> policies = new ArrayList<>();
        try {
            return getLocalSupportedPolicies();
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return policies;
    }

    public List<Policy> getLocalSupportedPolicies() {
        String policyFolder = env.getProperty("cmso.local.policy.folder", "data/policies");
        List<Policy> policies = new ArrayList<>();
        try {
            Path path = Paths.get(policyFolder);
            for (File file : path.toFile().listFiles()) {
                if (file.isFile()) {
                    Policy policy = getLocalPolicyForName(file.getName());
                    if (policy != null) {
                        policies.add(policy);
                    }
                }

            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return policies;
    }

}
