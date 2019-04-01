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

package org.onap.optf.cmso.optimizer.clients.optimizer;

import java.util.Optional;
import java.util.UUID;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerResults;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse;
import org.onap.optf.cmso.optimizer.model.Optimizer;
import org.onap.optf.cmso.optimizer.model.Request;
import org.onap.optf.cmso.optimizer.model.dao.OptimizerDao;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The Class OPtimizerRequestManager.
 */
@Component
public class OptimizerRequestManager {

    @Autowired
    Environment env;

    @Autowired
    RequestDao requestDao;

    @Autowired
    OptimizerDao optimizerDao;

    @Autowired
    OptimizerClient optimizerClient;

    /**
     * Creates the topology request.
     *
     * @param requestRow the request row
     * @return the topology response
     */
    public OptimizerResults createTopologyRequest(Request requestRow) {
        Optimizer optimizer = getExistingOptmizer(requestRow.getUuid());
        if (optimizer == null) {
            optimizer = new Optimizer();
            optimizer.setUuid(requestRow.getUuid());
            optimizer.setOptimizeRetries(0);
        }
        OptimizerResults apiResponse = optimizerClient.makeRequest(requestRow, optimizer);
        optimizerDao.save(optimizer);
        return apiResponse;

    }


    /**
     * Gets the existing optimizer row.
     *
     * @param uuid the uuid
     * @return the existing optmizer row
     */
    public Optimizer getExistingOptmizer(UUID uuid) {
        Optional<Optimizer> oppt = optimizerDao.findById(uuid);
        if (oppt.isPresent()) {
            return oppt.get();
        }
        return null;
    }



}
