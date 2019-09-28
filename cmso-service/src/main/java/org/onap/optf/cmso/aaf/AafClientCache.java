/*
 * Copyright (c) 2019 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
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
 */

package org.onap.optf.cmso.aaf;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import org.onap.observations.Observation;
import org.onap.optf.cmso.SpringProfiles;
import org.onap.optf.cmso.common.LogMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * The Class AafClientCache.
 */
@Component
@Profile(SpringProfiles.AAF_AUTHENTICATION)
public class AafClientCache {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    @Autowired
    AafClient aafClient;

    @Autowired
    AafUserRoleProperties aafUserRoleProperties;

    public enum AuthorizationResult {

        Authorized(0), AuthenticationFailure(401), AuthorizationFailure(403), Authenticated(0),;
        private final int status;

        AuthorizationResult(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    private Map<String, String> cache = new HashMap<>();
    private Long cacheAge = 0L;

    /**
     * Authorize.
     *
     * @param requestContext the request context
     * @return the authorization result
     */
    public AuthorizationResult authorize(ContainerRequestContext requestContext) {
        if (!env.getProperty(AafProperties.aafEnabled.toString(), Boolean.class, true)) {
            return AuthorizationResult.Authorized;
        }
        Map<String, String> auth = getUserPasssword(requestContext);
        String permissions = getPermissions(auth);
        if (permissions == null) {
            return AuthorizationResult.AuthenticationFailure;
        }
        return processPermissions(auth, permissions);
    }

    /**
     * Authenticate.
     *
     * @param user the user
     * @param password the password
     * @param sessionId the session id
     * @return the authorization result
     */
    public AuthorizationResult authenticate(String user, String password, String sessionId) {
        Map<String, String> auth = new HashMap<>();
        auth.put("user", user);
        auth.put("password", password);
        if (sessionId != null) {
            auth.put("sessionId", sessionId);
        }
        if (getPermissions(auth) == null) {
            return AuthorizationResult.AuthenticationFailure;
        }
        return AuthorizationResult.Authenticated;
    }


    private String getPermissions(Map<String, String> auth) {
        long now = System.currentTimeMillis();
        Long timeout = env.getProperty(AafProperties.aafCacheTimeout.toString(), Long.class, 300L);
        String permissions = null;
        // Do caching logic
        // Serializes calls to AAF
        // We will not cache authentication failures...
        synchronized (cache) {
            debug.debug("AAF cache now=" + now + ", cacheAge=" + cacheAge + " timeout=" + timeout);
            if (cacheAge != 0 && now > (cacheAge + (timeout * 1000))) {
                debug.debug("Clearing the AAF cache now=" + now + ", cacheAge=" + cacheAge + " timeout=" + timeout);
                cache.clear();
                cacheAge = now;
            }
            if (cacheAge == 0) {
                cacheAge = now;
            }
            permissions = cache.get(getCacheKey(auth));
            if (permissions == null) {
                if (!auth.get("password").equals("")) {
                    permissions = getPermissionsFromAaf(auth);
                    if (permissions != null) {
                        cache.put(getCacheKey(auth), permissions);
                    }
                }
            }
        }
        return permissions;
    }

    private String getCacheKey(Map<String, String> auth) {
        if (auth.get("sessionId") != null) {
            return auth.get("user") + "|" + auth.get("sessionId");
        }
        return auth.get("user") + "|" + auth.get("password");
    }


    private String getPermissionsFromAaf(Map<String, String> auth) {
        try {
            Response response = aafClient.getAuthz(auth);
            debug.debug("AAF authorization: " + response.getStatusInfo().toString());
            switch (response.getStatus()) {
                case 200:
                    String permissions = response.readEntity(String.class);
                    return permissions;
                case 401:
                    return null;
                default:
                    Observation.report(LogMessages.UNEXPECTED_RESPONSE, "AAF", response.getStatusInfo().toString(),
                                    auth.get("user"));
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return null;
    }

    private AuthorizationResult processPermissions(Map<String, String> auth, String permissions) {
        try {
            List<AafUserRole> perms = aafUserRoleProperties.getForUrlMethod(auth.get("path"), auth.get("method"));
            ObjectMapper om = new ObjectMapper();
            AafPermResponse resp = om.readValue(permissions, AafPermResponse.class);
            int tested = 0;
            int passed = 0;
            for (AafUserRole perm : perms) {
                for (AafPerm test : perm.getAafPerms()) {
                    tested++;
                    for (AafPerm userPerm : resp.getPerm()) {

                        if (test.ok(userPerm)) {
                            passed++;
                            break;
                        }
                    }
                }
            }
            // All permissions must be OK
            if (tested > 0 && tested == passed) {
                return AuthorizationResult.Authorized;
            } else {
                return AuthorizationResult.AuthorizationFailure;
            }
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return AuthorizationResult.AuthenticationFailure;
    }

    private Map<String, String> getUserPasssword(ContainerRequestContext requestContext) {

        String header = requestContext.getHeaderString("Authorization");
        Map<String, String> userPassword = getUserPasswordFromAuthorizationHeader(header);
        // Add other stuff....
        userPassword.put("path", requestContext.getUriInfo().getAbsolutePath().getPath());
        userPassword.put("method", requestContext.getMethod());
        Principal principal = requestContext.getSecurityContext().getUserPrincipal();
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            Object object = token.getDetails();
            if (object instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) object;
                if (details.getSessionId() != null) {
                    String sessionId = details.getRemoteAddress() + ":" + details.getSessionId();
                    userPassword.put("sessionId", sessionId);
                    userPassword.put("user", token.getName());
                }

            }
        }
        return userPassword;
    }

    private Map<String, String> getUserPasswordFromAuthorizationHeader(String header) {
        Map<String, String> userPassword = new HashMap<>();
        userPassword.put("user", "");
        userPassword.put("password", "");
        if (header != null) {
            String[] auth = header.split("Basic ");
            if (auth.length == 2) {
                String token = getToken(auth[1]);
                if (token.contains(":")) {
                    String[] tokens = token.split(":");
                    userPassword.put("user", tokens[0]);
                    if (tokens.length == 2) {
                        userPassword.put("password", tokens[1]);
                    }
                }
            }
        }
        return userPassword;
    }

    private String getToken(String auth) {
        try {
            String token = new String(DatatypeConverter.parseBase64Binary(auth));
            return token;
        } catch (Exception e) {
            return auth;
        }
    }

}
