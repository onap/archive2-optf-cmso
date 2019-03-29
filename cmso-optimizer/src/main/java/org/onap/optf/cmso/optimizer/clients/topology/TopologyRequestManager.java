/*
 * ============LICENSE_START==============================================
 * Copyright (c) 2019 AT&T Intellectual Property.
 * =======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * ============LICENSE_END=================================================
 *
 */

package org.onap.optf.cmso.optimizer.clients.topology;

import java.util.Optional;
import java.util.UUID;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse;
import org.onap.optf.cmso.optimizer.model.Request;
import org.onap.optf.cmso.optimizer.model.Topology;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
import org.onap.optf.cmso.optimizer.model.dao.TopologyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The Class TopologyRequestManager.
 */
@Component
public class TopologyRequestManager {

    @Autowired
    Environment env;

    @Autowired
    RequestDao requestDao;

    @Autowired
    TopologyDao topologyDao;

    @Autowired
    TopologyClient topologyClient;

    /**
     * Creates the topology request.
     *
     * @param requestRow the request row
     * @return the topology response
     */
    public TopologyResponse createTopologyRequest(Request requestRow) {
        Topology topology = getExistingTopology(requestRow.getUuid());
        if (topology == null) {
            topology = new Topology();
            topology.setUuid(requestRow.getUuid());
            topology.setTopologyRetries(0);
        }
        TopologyResponse topologyResponse = topologyClient.makeRequest(requestRow, topology);
        topologyDao.save(topology);
        return topologyResponse;

    }


    /**
     * Gets the existing topology.
     *
     * @param uuid the uuid
     * @return the existing topology
     */
    public Topology getExistingTopology(UUID uuid) {
        Optional<Topology> topologyOpt = topologyDao.findById(uuid);
        if (topologyOpt.isPresent()) {
            return topologyOpt.get();
        }
        return null;
    }
}
