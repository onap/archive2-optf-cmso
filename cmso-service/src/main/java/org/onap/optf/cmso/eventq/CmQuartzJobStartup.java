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

package org.onap.optf.cmso.eventq;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This bean uses @AUtowired to ensure that it runs once at startup to reset all
 * QueuedForDispatch rows that were in flight last time from this
 * "dispatcherInstnace" shut down.
 * Potentially, in a distributed environment, when a "dispatcherInstance"
 * shutdown is detected by another instance, it can run this query to requeue
 * these items. We are a long way from there.
 * Chances are great that re-queued events will end up being Past Due events
 *
 */
@Component
public class CmQuartzJobStartup {
    private static EELFLogger log = EELFManager.getInstance().getLogger(CmQuartzJobStartup.class);

    @Autowired
    ChangeManagementScheduleDAO cmScheduleDao;

    private AtomicBoolean started = new AtomicBoolean(false);

    public CmQuartzJobStartup() {

    }

    /**
     * Startup.
     */
    @Transactional
    public void startup() {
        try {
            if (started.getAndSet(true)) {
                return;
            }
            int rows = cmScheduleDao.requeueQueuedForDispatch(InetAddress.getLocalHost().getHostAddress());
            log.info("{0} QUeued For Dispatch rows have been requeued at startup", rows);
        } catch (Exception e) {
            log.error("Exception caught in requeueing Queued for DIspatch rows at startup", e);
        }
    }
}
