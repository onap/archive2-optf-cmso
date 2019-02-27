/*
 * Copyright © 2017-2019 AT&T Intellectual Property.
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

package org.onap.optf.cmso.dispatcher.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.dispatcher.DispatchJob;
import org.onap.optf.cmso.optimizer.CMSOptimizerClient;
import org.onap.optf.cmso.sostatus.MsoStatusClient;
import org.onap.optf.cmso.ticketmgt.TmStatusClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

@Controller
public class DispatcherServiceImpl implements DispacherService {
    private static EELFLogger log = EELFManager.getInstance().getLogger(DispatcherServiceImpl.class);
    private static EELFLogger audit = EELFManager.getInstance().getAuditLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    DispatchJob dispatchJob;

    @Autowired
    CMSOptimizerClient sniroClient;

    @Autowired
    TmStatusClient tmStatusClient;

    @Autowired
    MsoStatusClient msoStatusClient;

    @Override
    @Transactional
    public Response dispatchSchedule(Integer id, UriInfo uri, HttpServletRequest request) {
        debug.debug("dispatchSchedule entered  {}" , id);
        try {
            dispatchJob.execute(id);
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
            debug.error(e.getMessage(), e);
        }
        Response response = Response.ok().build();
        return response;
    }

    @Override
    @Transactional
    public Response dispatchOptimizer(Integer id, UriInfo uri, HttpServletRequest request) {
        debug.debug("dispatchOptimizer entered {}", id);
        try {
            sniroClient.scheduleSniroOptimization(id);
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
            debug.error(e.getMessage(), e);
        }
        Response response = Response.ok().build();
        audit.info("dispatchSNIRO");
        return response;
    }

    @Override
    @Transactional
    public Response dispatchScheduleStatus(Integer id, UriInfo uri, HttpServletRequest request) {
        debug.debug("dispatchScheduleStatus entered {}", id);
        try {
            tmStatusClient.checkStatus(id);
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
            debug.error(e.getMessage(), e);
        }
        Response response = Response.ok().build();
        return response;
    }

    @Override
    @Transactional
    public Response dispatchSoStatus(Integer id, UriInfo uri, HttpServletRequest request) {
        debug.debug("dispatchSoStatus entered {}", id);
        try {
            msoStatusClient.execute(id);
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e.getMessage());
            debug.error(e.getMessage(), e);
        }
        Response response = Response.ok().build();
        return response;
    }

}
