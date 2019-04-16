/*
 * Copyright © 2017-2019 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * Unless otherwise specified, all documentation contained herein is licensed under the Creative
 * Commons License, Attribution 4.0 Intl. (the "License"); you may not use this documentation except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.optf.cmso.sostatus;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.util.UUID;
import org.onap.observations.Mdc;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.model.ChangeManagementSchedule;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDao;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This is the Quartz Job that is run to send the workflow to VID for execution.
 *
 *
 */
@Component
@DisallowConcurrentExecution
public class MsoStatusJob implements Job {
    private static EELFLogger log = EELFManager.getInstance().getLogger(MsoStatusJob.class);
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    MsoStatusClient mso;

    @Autowired
    ChangeManagementScheduleDao cmScheduleDao;

    @Autowired
    Environment env;

    public enum ContextKeys {
        msoRequestId, scheduleId,
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Mdc.quartzJobBegin(context);
        String id = context.getJobDetail().getJobDataMap().getString(ContextKeys.scheduleId.toString());
        String requestId = context.getJobDetail().getJobDataMap().getString(ContextKeys.msoRequestId.toString());
        debug.debug(LogMessages.MSO_STATUS_JOB, "Entered", requestId, id.toString());
        try {
            UUID uuid = UUID.fromString(id);
            ChangeManagementSchedule cmSchedule = cmScheduleDao.findById(uuid).orElse(null);
            if (cmSchedule == null) {
                log.warn(LogMessages.MSO_POLLING_MISSING_SCHEDULE, id, requestId);
                return;
            }
            mso.poll(cmSchedule);
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        debug.debug(LogMessages.MSO_STATUS_JOB, "Exited", requestId, id);
    }

}
