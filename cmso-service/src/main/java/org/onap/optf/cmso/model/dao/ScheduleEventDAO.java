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

package org.onap.optf.cmso.model.dao;

import java.util.List;
import java.util.Optional;
import org.onap.optf.cmso.model.ScheduleEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ScheduleEventDAO extends PagingAndSortingRepository<ScheduleEvent, Integer> {
    Optional<ScheduleEvent> findById(Integer id);

    ScheduleEvent save(ScheduleEvent persisted);

    void delete(ScheduleEvent toDelete);

    @Query(value = "SELECT d FROM ScheduleEvent d WHERE d.schedulesId = ?1")
    List<ScheduleEvent> findByScheduleId(Integer id);

    @Query(value = "SELECT d FROM ScheduleEvent d WHERE d.status = ?1 AND d.reminderTimeMillis <= ?2")
    List<ScheduleEvent> findByStatusAndEndTime(String status, Long date);

}
