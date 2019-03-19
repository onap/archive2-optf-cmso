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
import java.util.UUID;

import javax.persistence.LockModeType;

import org.onap.optf.cmso.model.Schedule;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ScheduleDAO extends PagingAndSortingRepository<Schedule, UUID> {
    Optional<Schedule> findById(UUID id);

    Schedule save(Schedule persited);

    void delete(Schedule toDelete);

    @Query(value = "SELECT s FROM Schedule s WHERE s.domain = ?1 AND s.scheduleId= ?2 ")
    Schedule findByDomainScheduleID(String domain, String scheduleId);

    @Query(value = "SELECT s FROM Schedule s WHERE s.domain = ?1 AND s.status = ?2 ")
    List<Schedule> findByDomainStatus(String domain, String Status);

    @Query(value = "SELECT s FROM Schedule s WHERE s.optimizerTransactionId= ?1")
    Schedule findOneByTransactionId(String transactionId);

    @Query(value = "SELECT s FROM Schedule s WHERE s.domain = ?1 AND s.status = 'Notifications Initiated'")
    List<Schedule> findAllInProgress(String string);

    @Query(value = "SELECT d FROM Schedule d WHERE d.uuid = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Schedule lockOne(UUID id);

    @Query(value = "SELECT s FROM Schedule s WHERE s.optimizerTransactionId= ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Schedule lockOneByTransactionId(String transactionId);

}
