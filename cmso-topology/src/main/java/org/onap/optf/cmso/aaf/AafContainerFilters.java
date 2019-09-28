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

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;
import org.onap.observations.Observation;
import org.onap.optf.cmso.aaf.AafClientCache.AuthorizationResult;
import org.onap.optf.cmso.topology.SpringProfiles;
import org.onap.optf.cmso.topology.common.LogMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Priority(1)
@Provider
@Component
@Profile(SpringProfiles.AAF_AUTHENTICATION)
public class AafContainerFilters implements ContainerRequestFilter {

    @Autowired
    AafClientCache aafClientCache;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        ResponseBuilder builder = null;
        AuthorizationResult status = null;
        try {
            status = aafClientCache.authorize(requestContext);
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            status = AuthorizationResult.AuthenticationFailure;
        }
        switch (status) {
            case AuthenticationFailure:
                builder = Response.status(Response.Status.UNAUTHORIZED).entity("");
                builder.header("WWW-Authenticate", "Basic realm=\"Realm\"");
                throw new WebApplicationException(builder.build());
            case AuthorizationFailure:
                builder = Response.status(Response.Status.FORBIDDEN).entity("");
                throw new WebApplicationException(builder.build());
            case Authorized:
            case Authenticated:
            default:
        }
    }
}
