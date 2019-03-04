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
package org.onap.optf.ticketmgt.aaf;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.observations.Observation;
import org.onap.optf.cmso.common.exceptions.CMSException;
import org.onap.optf.ticketmgt.SpringProfiles;
import org.onap.optf.ticketmgt.common.LogMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * AAF authorization filter
 */

@Component
@Profile(SpringProfiles.AAF_AUTHENTICATION)
@PropertySource("file:${server.local.startpath}/aaf/permissions.properties")
public class AafAuthorizationFilter extends  OrderedRequestContextFilter {

    @Value("${permission.type}")
    String type;

    @Value("${permission.instance}")
    String instance;

    public AafAuthorizationFilter() {
        this.setOrder(FilterPriority.AAF_AUTHORIZATION.getPriority());

    	
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String permission = String.format("%s|%s|%s", type, instance, request.getMethod().toLowerCase());
        if(request.getRequestURI().matches("^.*/util/echo$")){
            filterChain.doFilter(request, response);
        }
        if(!request.isUserInRole(permission)){
        	Observation.report(LogMessages.UNAUTHORIZED);
            ResponseFormatter.errorResponse(request, response, 
            		new CMSException(LogMessages.UNAUTHORIZED.getStatus(), 
            		LogMessages.UNAUTHORIZED, ""));
        }else{
            filterChain.doFilter(request,response);
        }
    }
}
