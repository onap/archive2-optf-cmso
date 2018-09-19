/*
 * Copyright © 2017-2018 AT&T Intellectual Property.
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

package org.onap.optf.cmso.filters;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.filters.MessageHeaders.HeadersEnum;
import org.onap.optf.cmso.service.rs.CMSOServiceImpl;
import org.springframework.stereotype.Component;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

@Priority(1)
@Provider
@Component
public class CMSOContainerFilters implements ContainerRequestFilter, ContainerResponseFilter {
    private static EELFLogger log = EELFManager.getInstance().getLogger(CMSOServiceImpl.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        try {
            log.info("SchedulerContainerFilters.filter(r,r)");
            MultivaluedMap<String, String> reqHeaders = requestContext.getHeaders();
            MultivaluedMap<String, Object> respHeaders = responseContext.getHeaders();
            String minorVersion = (String) reqHeaders.getFirst(HeadersEnum.MinorVersion.toString());
            respHeaders.add(HeadersEnum.MinorVersion.toString(), minorVersion);
            respHeaders.add(HeadersEnum.LatestVersion.toString(), MessageHeaders.latestVersion);
            respHeaders.add(HeadersEnum.PatchVersion.toString(), MessageHeaders.patchVersion);

        } catch (Exception e) {
            if (e instanceof WebApplicationException) {
                log.info(LogMessages.EXPECTED_EXCEPTION, e.getMessage());
            } else {
                log.info(LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            // On the way in
            log.info("SchedulerContainerFilters.filter(r) path={} ", requestContext.getUriInfo().getPath().toString());

            String majorVersion = requestContext.getUriInfo().getPath();
            if (majorVersion != null) {

                if (majorVersion.startsWith("dispatch/"))
                    return;
                majorVersion = majorVersion.replaceAll("/.*$", "");
            }
            if (!MessageHeaders.validateMajorVersion(majorVersion)) {
                ResponseBuilder builder = null;
                String response = "Unsupported Major version";
                builder = Response.status(Response.Status.NOT_FOUND).entity(response);
                throw new WebApplicationException(builder.build());
            }
            MultivaluedMap<String, String> headers = requestContext.getHeaders();
            String transactionId = (String) headers.getFirst(HeadersEnum.TransactionID.toString());
            if (transactionId == null) {
                transactionId = UUID.randomUUID().toString();
                headers.add(HeadersEnum.TransactionID.toString(), transactionId);
            }
            String minorVersion = (String) headers.getFirst(HeadersEnum.MinorVersion.toString());
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
                log.info(LogMessages.EXPECTED_EXCEPTION, e.getMessage());
                throw e;
            } else {
                log.info(LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
            }
        }

    }

}
