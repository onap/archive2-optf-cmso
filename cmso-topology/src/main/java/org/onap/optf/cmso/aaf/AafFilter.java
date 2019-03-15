/**
 * ============LICENSE_START=======================================================
 * org.onap.optf.cmso
 * ================================================================================
 * Copyright © 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.optf.cmso.aaf;


import java.io.IOException;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.aaf.cadi.PropAccess;
import org.onap.aaf.cadi.filter.CadiFilter;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.exceptions.CMSException;
import org.onap.optf.cmso.topology.Application;
import org.onap.optf.cmso.topology.SpringProfiles;
import org.onap.optf.cmso.topology.common.LogMessages;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * AAF authentication filter
 */

@Component
@Profile(SpringProfiles.AAF_AUTHENTICATION)
public class AafFilter extends OrderedRequestContextFilter {

    private final CadiFilter cadiFilter;

    public AafFilter() throws IOException, ServletException {
        Properties cadiProperties = new Properties();
        cadiProperties.load(Application.class.getClassLoader().getResourceAsStream("cadi.properties"));
        cadiFilter = new CadiFilter(new PropAccess(cadiProperties));
        this.setOrder(FilterPriority.AAF_AUTHENTICATION.getPriority());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        cadiFilter.doFilter(request, response, filterChain);
        if(response.getStatus() ==401){
        	Observation.report(LogMessages.UNAUTHENTICATED);
            ResponseFormatter.errorResponse(request, response, 
            		new CMSException(LogMessages.UNAUTHENTICATED.getStatus(), 
            		LogMessages.UNAUTHENTICATED, ""));
        }
    }


}
