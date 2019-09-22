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

package org.onap.optf.ticketmgt.aaf;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.onap.observations.Mdc;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.BasicAuthenticatorFilter;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.common.exceptions.CmsoException;
import org.onap.optf.ticketmgt.SpringProfiles;
import org.onap.optf.ticketmgt.aaf.AafEndpoints.Endpoint;
import org.onap.optf.ticketmgt.common.LogMessages;
import org.onap.optf.ticketmgt.filters.CmsoClientFilters;
import org.onap.optf.ticketmgt.service.rs.models.HealthCheckComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Profile(SpringProfiles.AAF_AUTHENTICATION)
public class AafClient {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    @Autowired
    PropertiesManagement pm;

    @Autowired
    AafEndpoints aafEndpoints;

    /**
     * Gets the authz.
     *
     * @param auth the auth
     * @return the authz
     * @throws CmsoException the cmso exception
     */
    public Response getAuthz(Map<String, String> auth) throws CmsoException {
        Response response = null;
        List<String> endpoints = new ArrayList<>();
        String url = aafEndpoints.getEndpoint(Endpoint.AUTHZ, endpoints);
        String user = auth.get("user");
        if (!user.contains("@")) {
            user += env.getProperty(AafProperties.aafDefaultUserDomain.toString(), "@csp.att.com");
        }
        String pass = auth.get("password");
        while (url != null) {
            try {
                // Cannot provide changeId. Interesting.
                // This should be replaced by fetch
                // For now, make a best effort to get the passed changeId
                if (!url.endsWith("/")) {
                    url += "/";
                }
                url += user;
                response = get(url, user, pass);
                return response;
            } catch (ProcessingException e) {
                Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.toString());
                url = aafEndpoints.getNextEndpoint(Endpoint.AUTHZ, endpoints);
                if (url == null || !tryNextUrl(e)) {
                    throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.UNEXPECTED_EXCEPTION, user,
                                    e.getMessage());
                }
            } catch (Exception e) {
                Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.toString());
                throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.UNEXPECTED_EXCEPTION, user,
                                e.getMessage());
            }
        }
        return response;
    }

    /**
     * Gets the.
     *
     * @param url the url
     * @param user the user
     * @param pass the pass
     * @return the response
     */
    public Response get(String url, String user, String pass) {
        Client client = ClientBuilder.newClient();
        client.register(new BasicAuthenticatorFilter(user, pass));
        client.register(new CmsoClientFilters());
        WebTarget target = client.target(url);
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        debug.debug("AAF URL = " + url);
        Response response = invocationBuilder.get();
        debug.debug("AAF URL = " + url + " user=" + user + ":" + response.getStatusInfo().toString());
        return response;
    }

    private boolean tryNextUrl(ProcessingException exc) {
        if (exc.getCause() instanceof UnknownHostException) {
            return true;
        }
        return true;
    }

    /**
     * Health check.
     *
     * @return the health check component
     */
    public HealthCheckComponent healthCheck() {
        Map<String, String> mdcSave = Mdc.save();
        HealthCheckComponent hcc = new HealthCheckComponent();
        hcc.setName("AAF");
        hcc.setHealthy(false);
        List<String> endpoints = new ArrayList<>();
        try {
            String url = aafEndpoints.getEndpoint(AafEndpoints.Endpoint.HEALTHCHECK, endpoints);
            String user = "";
            String pass = "";

            while (url != null) {
                try {
                    hcc.setUrl(url);
                    Response response = get(url, user, pass);
                    hcc.setHealthy(true);
                    hcc.setStatus(response.getStatusInfo().toString());
                } catch (ProcessingException e) {
                    Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.toString());
                    url = aafEndpoints.getNextEndpoint(AafEndpoints.Endpoint.HEALTHCHECK, endpoints);
                    if (url == null || !tryNextUrl(e)) {
                        hcc.setStatus(e.getMessage());
                    }
                } catch (Exception e) {
                    Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.toString());
                    hcc.setStatus(e.getMessage());
                }
            }
        } finally {
            Mdc.restore(mdcSave);
        }
        return hcc;
    }
}
