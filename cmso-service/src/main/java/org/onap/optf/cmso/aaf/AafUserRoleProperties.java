/*
 * Copyright (c) 2019 AT&T Intellectual Property.
 * Modifications Copyright © 2018 IBM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.onap.optf.cmso.aaf;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.onap.observations.Observation;
import org.onap.optf.cmso.SpringProfiles;
import org.onap.optf.cmso.common.LogMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The Class AafUserRoleProperties.
 */
@Component
@Profile(SpringProfiles.AAF_AUTHENTICATION)
public class AafUserRoleProperties {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    /** The env. */
    @Autowired
    Environment env;

    private List<AafUserRole> list = new ArrayList<>();

    /**
     * Initialize permissions.
     */
    @PostConstruct
    public void initializePermissions() {
        String userRolePropertiesName = env.getProperty(AafProperties.aafUserRoleProperties.toString(),
                        "opt/att/ajsc/config/AAFUserRoles.properties");
        try {
            List<String> lines = Files.readAllLines(Paths.get(userRolePropertiesName));
            for (String line : lines) {
                line = line.trim();
                if (!line.startsWith("#")) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        list.add(new AafUserRole(parts[0], env.resolvePlaceholders(parts[1])));
                    } else {
                        Observation.report(LogMessages.INVALID_ATTRIBUTE, line, userRolePropertiesName);
                    }
                }
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        debug.debug("AafUserRole.properties: " + list);
    }

    /**
     * Gets the for url method.
     *
     * @param url the url
     * @param method the method
     * @return the for url method
     */
    public List<AafUserRole> getForUrlMethod(String url, String method) {
        List<AafUserRole> userRoleList = new ArrayList<>();
        for (AafUserRole aur : list) {
            if (aur.matches(url, method)) {
                userRoleList.add(aur);
            }
        }
        return userRoleList;
    }
}
