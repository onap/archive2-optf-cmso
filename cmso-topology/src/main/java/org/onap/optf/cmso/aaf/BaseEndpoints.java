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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Intent is to use AAF vanity URL however, this allows us to support a list of URLs.
 */
@Component
public class BaseEndpoints {

    @Autowired
    Environment env;

    private Map<EndpointInterface, List<String>> endpointMap = new HashMap<>();
    private Map<EndpointInterface, String> endpointMapOk = new HashMap<>();

    /**
     * Gets the endpoint.
     *
     * @param ep the ep
     * @param endpoints the endpoints
     * @return the endpoint
     */
    public String getEndpoint(EndpointInterface ep, List<String> endpoints) {
        loadUrls(ep);
        endpoints.clear();
        endpoints.addAll(endpointMap.get(ep));
        String endpoint = null;
        if (endpoints.size().isEmpty()) {
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
    /**
     * Gets the next endpoint.
     *
     * @param ep the ep
     * @param endpoints the endpoints
     * @return the next endpoint
     */
    // An attempt to track the most recent "working" endpoint.
    public String getNextEndpoint(EndpointInterface ep, List<String> endpoints) {
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

    private synchronized void loadUrls(EndpointInterface endpoint) {
        endpointMap = new HashMap<>();
        String urls = env.getProperty(AafProperties.aafUrls.toString());
        String[] list = urls.split("\\|");
        for (String url : list) {
            for (EndpointInterface ep : endpoint.getValues()) {
                addToEndpointMap(ep, url);
            }
        }
    }


    private void addToEndpointMap(EndpointInterface ep, String endpoint) {
        List<String> list = endpointMap.get(ep);
        if (list == null) {
            list = new ArrayList<>();
            endpointMap.put(ep, list);
        }
        String path = env.getProperty(ep.getPathName().toString(), ep.defaultPath());
        list.add(endpoint + path);
    }
}
