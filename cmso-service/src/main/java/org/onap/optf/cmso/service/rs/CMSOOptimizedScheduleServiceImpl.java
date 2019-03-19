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

package org.onap.optf.cmso.service.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.onap.observations.Observation;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.exceptions.CMSException;
import org.onap.optf.cmso.eventq.CMSQueueJob;
import org.onap.optf.cmso.model.dao.ChangeManagementChangeWindowDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementDetailDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementGroupDAO;
import org.onap.optf.cmso.model.dao.ChangeManagementScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleDAO;
import org.onap.optf.cmso.model.dao.ScheduleQueryDAO;
import org.onap.optf.cmso.service.rs.models.v2.OptimizedScheduleMessage;
import org.onap.optf.cmso.ticketmgt.TmClient;
import org.onap.optf.cmso.ticketmgt.bean.BuildCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

@Controller
public class CMSOOptimizedScheduleServiceImpl implements CMSOOptimizedScheduleService {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    CMSQueueJob qJob;

    @Autowired
    Environment env;

    @Autowired
    ChangeManagementScheduleDAO cmScheduleDAO;

    @Autowired
    ChangeManagementGroupDAO cmGroupDAO;

    @Autowired
    ChangeManagementChangeWindowDAO cmChangeWindowDAO;

    @Autowired
    ChangeManagementDetailDAO cmDetailsDAO;

    @Autowired
    ScheduleQueryDAO scheduleQueryDAO;

    @Autowired
    ScheduleDAO scheduleDAO;

    @Autowired
    TmClient tmClient;

    @Autowired
    BuildCreateRequest buildCreateRequest;


    @Context 
    HttpServletRequest request;
    
    @Override
    @Transactional
    public Response createScheduleRequest(String apiVersion, String scheduleId, OptimizedScheduleMessage scheduleMessage) 
    {
        Observation.report(LogMessages.CREATE_SCHEDULE_REQUEST, "Received", request.getRemoteAddr(), scheduleId,
                scheduleMessage.toString());
        Response response = null;
        try {
            response = Response.accepted().build();
//        } catch (CMSException e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        	Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
//            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.CREATE_SCHEDULE_REQUEST, "Returned", request.getRemoteAddr(), scheduleId,
                response.getStatusInfo().toString());
        return response;
    }


}
