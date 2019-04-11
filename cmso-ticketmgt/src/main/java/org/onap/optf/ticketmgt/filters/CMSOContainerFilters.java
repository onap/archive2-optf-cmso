/*
 * Copyright © 2017-2019 AT&T Intellectual Property.
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

package org.onap.optf.ticketmgt.filters;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;
import org.onap.observations.Mdc;
import org.onap.observations.MessageHeaders;
import org.onap.observations.MessageHeaders.HeadersEnum;
import org.onap.observations.Observation;
import org.onap.optf.ticketmgt.common.LogMessages;
import org.springframework.stereotype.Component;

@Priority(1)
@Provider
@Component
public class CMSOContainerFilters implements ContainerRequestFilter, ContainerResponseFilter {


    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
                    throws IOException {
        try {
            Mdc.auditEnd(requestContext, responseContext);
            Observation.report(LogMessages.INCOMING_MESSAGE_RESPONSE, requestContext.getMethod(),
                            requestContext.getUriInfo().getPath().toString(),
                            responseContext.getStatusInfo().toString());
            MultivaluedMap<String, String> reqHeaders = requestContext.getHeaders();
            MultivaluedMap<String, Object> respHeaders = responseContext.getHeaders();
            String minorVersion = reqHeaders.getFirst(HeadersEnum.MinorVersion.toString());
            respHeaders.add(HeadersEnum.MinorVersion.toString(), minorVersion);
            respHeaders.add(HeadersEnum.LatestVersion.toString(), MessageHeaders.latestVersion);
            respHeaders.add(HeadersEnum.PatchVersion.toString(), MessageHeaders.patchVersion);

        } catch (Exception e) {
            if (e instanceof WebApplicationException) {
                Observation.report(LogMessages.EXPECTED_EXCEPTION, e.getMessage());
            } else {
                Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            // On the way in
            Mdc.auditStart(requestContext, servletRequest);
            Observation.report(LogMessages.INCOMING_MESSAGE, requestContext.getMethod(),
                            requestContext.getUriInfo().getPath().toString());

            String majorVersion = requestContext.getUriInfo().getPath();
            if (majorVersion != null) {

                if (majorVersion.startsWith("dispatch/")) {
                    return;
                }
                majorVersion = majorVersion.replaceAll("/.*$", "");
            }
            if (!MessageHeaders.validateMajorVersion(majorVersion)) {
                ResponseBuilder builder = null;
                String response = "Unsupported Major version";
                builder = Response.status(Response.Status.NOT_FOUND).entity(response);
                throw new WebApplicationException(builder.build());
            }
            MultivaluedMap<String, String> headers = requestContext.getHeaders();
            String transactionId = headers.getFirst(HeadersEnum.TransactionID.toString());
            if (transactionId == null) {
                transactionId = UUID.randomUUID().toString();
                headers.add(HeadersEnum.TransactionID.toString(), transactionId);
            }
            String minorVersion = headers.getFirst(HeadersEnum.MinorVersion.toString());
            if (minorVersion == null) {
                minorVersion = MessageHeaders.supportedMajorVersions.get(majorVersion);
                headers.add(HeadersEnum.MinorVersion.toString(), minorVersion);
            }
            if (!MessageHeaders.validateMajorMinorVersion(majorVersion, minorVersion)) {
                ResponseBuilder builder = null;
                String response = "Unsupported API version";
                builder = Response.status(Response.Status.NOT_FOUND).entity(response);
                throw new WebApplicationException(builder.build());

            }
        } catch (Exception e) {
            if (e instanceof WebApplicationException) {
                Observation.report(LogMessages.EXPECTED_EXCEPTION, e.getMessage());
                throw e;
            } else {
                Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
            }
        }

    }

}
