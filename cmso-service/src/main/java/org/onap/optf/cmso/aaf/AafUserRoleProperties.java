/*
 * Copyright © 2019 AT&T Intellectual Property.
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
 ******************************************************************************/

package org.onap.optf.cmso.aaf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.onap.aaf.cadi.Permission;
import org.onap.aaf.cadi.aaf.AAFPermission;
import org.onap.observations.Observation;
import org.onap.optf.cmso.SpringProfiles;
import org.onap.optf.cmso.common.LogMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This class uses a properties file to map URL patterns/method to AAF Permissions (AafPerm).
 *
 * @author jf9860
 *
 */
@Component
@Profile(SpringProfiles.AAF_AUTHENTICATION)
public class AafUserRoleProperties {
    @Autowired
    Environment env;

    private List<AafUserRole> list = new ArrayList<>();

    /**
     * Initialize permissions.
     */
    @PostConstruct
    public void initializePermissions() {
        String userRolePropertiesName =
                        env.getProperty("aaf.user.roles", "src/main/resources/aaf/AAFUserRoles.properties");
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File(userRolePropertiesName)));
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        for (Object url : props.keySet()) {
            Object value = props.get(url);
            list.add(new AafUserRole((String) url, (String) value));
        }
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

    /**
     * Process permissions.
     *
     * @param request the request
     * @param userPerms the user perms
     * @return true, if successful
     */
    public boolean processPermissions(HttpServletRequest request, List<Permission> userPerms) {
        try {
            // Get list of perms that match incoming URL. May be more than 1...
            // Users perms must match all that match URL
            List<AafUserRole> perms = getForUrlMethod(request.getRequestURI(), request.getMethod());
            int tested = 0;
            int passed = 0;
            for (AafUserRole perm : perms) {
                for (AafPerm test : perm.getAafPerms()) {
                    tested++;
                    for (Permission userPerm : userPerms) {

                        if (test.matches((AAFPermission) userPerm)) {
                            passed++;
                            break;
                        }
                    }
                }
            }
            // All permissions must be OK
            if (tested > 0 && tested == passed) {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return false;
    }
}
