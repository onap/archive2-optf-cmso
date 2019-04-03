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
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChangeManagementScheduleDAO extends PagingAndSortingRepository<ChangeManagementSchedule, UUID> {
    @Override
    Optional<ChangeManagementSchedule> findById(UUID id);

    @Override
    ChangeManagementSchedule save(ChangeManagementSchedule persisted);

    @Override
    void delete(ChangeManagementSchedule toDelete);

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.changeManagementGroupUuid = ?1")
    List<ChangeManagementSchedule> findByChangeManagementGroupId(UUID id);

    @Modifying
    @Query(value = "DELETE FROM ChangeManagementSchedule d WHERE d.changeManagementGroupUuid = ?1")
    public int deleteByChangeManagementGroupsId(UUID id);

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.changeManagementGroupUuid = ?1 AND d.vnfName = ?2")
    ChangeManagementSchedule findOneByGroupUuidAndVnfName(UUID id, String vnfName);

    @Query(value = "SELECT d FROM ChangeManagementSchedule d, ChangeManagementGroup g"
            + " WHERE g.schedulesUuid = ?1"
            + " AND g.groupId = ?2"
            + " AND d.changeManagementGroupUuid = g.uuid"
            + " AND d.vnfName = ?3")
    ChangeManagementSchedule findOneByScheduleUuidGroupIdAndVnfName(UUID id, String groupId, String vnfName);


    @Query(value = "SELECT d FROM ChangeManagementSchedule d"
                    + " WHERE (d.status = ?1 AND d.startTimeMillis <= ?2)"
                    + " or d.status = 'Scheduled Immediate' order by d.startTimeMillis")
    List<ChangeManagementSchedule> findByStatusAndEndTime(String status, Long date);

    @Modifying
    @Query(value = "Update ChangeManagementSchedule d set d.status = 'Scheduled',"
                    + " d.dispatcherInstance = ''"
                    + " WHERE d.status = 'Queued for Dispatch' AND d.dispatcherInstance = ?1")
    public int requeueQueuedForDispatch(String dispatcherInstance);

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.status = 'Triggered'")
    List<ChangeManagementSchedule> findAllTriggered();

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.tmApprovalStatus = 'Pending Approval'")
    List<ChangeManagementSchedule> findAllAwaitingTmApproval();

    @Query(value = "SELECT d FROM ChangeManagementSchedule d WHERE d.uuid = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ChangeManagementSchedule lockOne(UUID id);

}
