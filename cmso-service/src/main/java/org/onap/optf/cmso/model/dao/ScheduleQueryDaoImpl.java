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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.onap.optf.cmso.model.ScheduleQuery;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleQueryDaoImpl implements ScheduleQueryDao {
    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<ScheduleQuery> searchSchedules(String where, int limit) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct" + " ss.uuid as uuid" + " from" + " SCHEDULES ss"
                + " left outer join CHANGE_MANAGEMENT_GROUPS g on ss.uuid = g.schedules_uuid"
                + " left outer join CHANGE_MANAGEMENT_SCHEDULES s on g.uuid =  s.change_management_group_uuid"
                + " left outer join DOMAIN_DATA dd on ss.uuid = dd.schedules_uuid"
                + " left outer join SCHEDULE_APPROVALS sa on ss.uuid = sa.schedules_uuid"
                + " left outer join APPROVAL_TYPES at on sa.approval_types_uuid = at.uuid ");
        sql.append(where);
        sql.append(" order by uuid ");
        if (limit > 0) {
            sql.append("LIMIT " + limit);
        }

        Query qry = manager.createNativeQuery(sql.toString(), ScheduleQuery.class);
        List<ScheduleQuery> list = qry.getResultList();
        return list;
    }
}
