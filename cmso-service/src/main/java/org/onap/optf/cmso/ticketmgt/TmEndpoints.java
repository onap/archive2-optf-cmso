/*
 * Copyright � 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright � 2018 IBM.
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

package org.onap.optf.cmso.ticketmgt;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TmEndpoints {
    private static EELFLogger log = EELFManager.getInstance().getLogger(TmEndpoints.class);
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    //
    // This class was desinged to support a list of endpoints for each interface
    // to support failover to alternate endpoints.
    // This has been gutted so that only a single endpoint for each
    // interface is loaded from the environment.
    //
    public enum Endpoint {
        GET("tm.getPath"), CREATE("tm.createPath"), CLOSE("tm.closePath"), UPDATE("tm.updatePath"),;
        private final String pathName;

        private Endpoint(String pathname) {
            this.pathName = pathname;
        }

        @Override
        public String toString() {
            return pathName;
        }
    }

    private boolean legacyLoaded = false;
    private Map<Endpoint, List<String>> endpointMap = new HashMap<>();
    private Map<Endpoint, String> endpointMapOk = new HashMap<>();

    /**
     * Gets the endpoint.
     *
     * @param ep the ep
     * @param endpoints the endpoints
     * @return the endpoint
     */
    public String getEndpoint(Endpoint ep, List<String> endpoints) {
        loadLegacy();
        endpoints.clear();
        endpoints.addAll(endpointMap.get(ep));
        String endpoint = null;
        if (endpoints.size() > 0) {
            // Make an attempt to return the most recent "working" endpoint.
            //
            synchronized (endpointMapOk) {
                endpoint = endpointMapOk.get(ep);
                if (endpoint == null) {
                    endpoint = endpoints.get(0);
                    endpointMapOk.put(ep, endpoint);
                }
            }
            endpoints.remove(endpoint);
        }
        return endpoint;
    }

    // Call this if the previous enpoint failed to connect.
    // An attempt to track the most recent "working" endpoint.
    /**
     * Gets the next endpoint.
     *
     * @param ep the ep
     * @param endpoints the endpoints
     * @return the next endpoint
     */
    public String getNextEndpoint(Endpoint ep, List<String> endpoints) {
        String endpoint = null;
        if (endpoints.size() > 0) {
            endpoint = endpoints.remove(0);
            synchronized (endpointMapOk) {
                // Let's hope this one works.
                endpointMapOk.put(ep, endpoint);
            }
        }
        return endpoint;
    }

    private synchronized void loadLegacy() {
        if (legacyLoaded) {
            return;
        }
        log.info("Loading legacy endpoints");
        endpointMap = new HashMap<>();
        addToEndpointMap(Endpoint.CREATE);
        addToEndpointMap(Endpoint.GET);
        addToEndpointMap(Endpoint.UPDATE);
        addToEndpointMap(Endpoint.CLOSE);
    }

    private void addToEndpointMap(Endpoint ep) {
        List<String> list = endpointMap.get(ep);
        if (list == null) {
            list = new ArrayList<>();
            endpointMap.put(ep, list);
        }
        list.add(env.getProperty(ep.toString()));
    }

    @Override
    public String toString() {
        return endpointMap.toString();
    }
}
