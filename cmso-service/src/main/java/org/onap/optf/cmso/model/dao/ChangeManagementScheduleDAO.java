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
import javax.persistence.LockModeType;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChangeManagementScheduleDAO extends PagingAndSortingRepository<ChangeManagementSchedule, Integer> {
    Optional<ChangeManagementSchedule> findById(Integer id);

    ChangeManagementSchedule save(ChangeManagementSchedule persisted);

    void delete(ChangeManagementSchedule toDelete);

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.changeManagementGroupsId = ?1")
    List<ChangeManagementSchedule> findByChangeManagementGroupId(Integer id);

    @Modifying
    @Query(value = "DELETE FROM ChangeManagementSchedule d WHERE d.changeManagementGroupsId = ?1")
    public int deleteByChangeManagementGroupsId(Integer id);

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.changeManagementGroupsId = ?1 AND d.vnfName = ?2")
    ChangeManagementSchedule findOneByGroupIDAndVnfName(Integer id, String vnfName);

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE (d.status = ?1 AND d.startTimeMillis <= ?2) or d.status = 'Scheduled Immediate' order by d.startTimeMillis")
    List<ChangeManagementSchedule> findByStatusAndEndTime(String status, Long date);

    @Modifying
    @Query(value = "Update ChangeManagementSchedule d set d.status = 'Scheduled', d.dispatcherInstance = '' WHERE d.status = 'Queued for Dispatch' AND d.dispatcherInstance = ?1")
    public int requeueQueuedForDispatch(String dispatcherInstance);

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.status = 'Triggered'")
    List<ChangeManagementSchedule> findAllTriggered();

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.tmApprovalStatus = 'Pending Approval'")
    List<ChangeManagementSchedule> findAllAwaitingTmApproval();

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ChangeManagementSchedule lockOne(Integer id);

}
