/*******************************************************************************
 * Copyright © 2019 AT&T Intellectual Property.
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
 ******************************************************************************/

package org.onap.optf.cmso.aaf;

import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onap.aaf.cadi.CadiWrap;
import org.onap.aaf.cadi.Permission;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.exceptions.CmsoException;
import org.onap.optf.cmso.optimizer.SpringProfiles;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * AAF authorization filter.
 */

@Component
@Profile(SpringProfiles.AAF_AUTHENTICATION)
public class AafAuthorizationFilter extends OrderedRequestContextFilter {

    @Autowired
    AafUserRoleProperties userRoleProperties;

    /**
     * Instantiates a new aaf authorization filter.
     */
    public AafAuthorizationFilter() {
        this.setOrder(FilterPriority.AAF_AUTHORIZATION.getPriority());


    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws IOException, ServletException {
        try {
            if (request instanceof CadiWrap) {
                CadiWrap cw = (CadiWrap) request;
                List<Permission> perms = cw.getPermissions(cw.getUserPrincipal());
                if (userRoleProperties.processPermissions(request, perms)) {
                    filterChain.doFilter(request, response);
                } else {
                    Observation.report(LogMessages.UNAUTHORIZED);
                    ResponseFormatter.errorResponse(request, response, new CmsoException(
                                    LogMessages.UNAUTHORIZED.getStatus(), LogMessages.UNAUTHORIZED, ""));
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            ResponseFormatter.errorResponse(request, response,
                            new CmsoException(LogMessages.UNAUTHORIZED.getStatus(), LogMessages.UNAUTHORIZED, ""));
        }
    }
}
