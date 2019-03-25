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

package org.onap.optf.cmso.optimizer.model.dao;

import java.util.Optional;
import java.util.UUID;
import org.onap.optf.cmso.optimizer.model.Optimizer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OptimizerDao extends PagingAndSortingRepository<Optimizer, UUID> {
    @Override
    Optional<Optimizer> findById(UUID id);

    @SuppressWarnings("unchecked")
    @Override
    Optimizer save(Optimizer persisted);

    @Override
    void delete(Optimizer toDelete);

}
