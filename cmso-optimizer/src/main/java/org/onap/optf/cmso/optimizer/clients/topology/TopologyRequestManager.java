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
import javax.ws.rs.core.Response.Status;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.exceptions.CmsoException;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.model.Request;
import org.onap.optf.cmso.optimizer.model.Topology;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
import org.onap.optf.cmso.optimizer.model.dao.TopologyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

    public TopologyResponse createTopologyRequest(UUID uuid)
    {
        try
        {
            Request request = null;
            Optional<Request> requestOptional = requestDao.findById(uuid);
            if (requestOptional.isPresent())
            {
                request = requestOptional.get();
            }
            if (request == null)
            {
                throw new CmsoException(Status.INTERNAL_SERVER_ERROR, LogMessages.EXPECTED_DATA_NOT_FOUND,
                                uuid.toString(), "Request table");
            }
            Topology topology  = null;
            Optional<Topology> topologyOpt  = topologyDao.findById(uuid);
            if (topologyOpt.isPresent())
            {
                topology = topologyOpt.get();

            }
            if (topology == null)
            {
                topology = new Topology();
                topology.setUuid(uuid);
                topology.setTopologyRetries(0);
            }
            TopologyResponse topologyResponse = topologyClient.makeRequest(request, topology);
            switch(topologyResponse.getStatus())
            {
                case COMPLETED:
                    break;
                case FAILED:
                    break;
                case IN_PROGRESS:
                    break;
            }
            return topologyResponse;
        }
        catch (Exception e)
        {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return null;

    }

}
