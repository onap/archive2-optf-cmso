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

package org.onap.optf.cmso.optimizer.service.rs;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.core.OptimizerManager;
import org.onap.optf.cmso.optimizer.exceptions.CmsoException;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
import org.onap.optf.cmso.optimizer.observations.Observation;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerRequest;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerResponse;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerResponse.OptimizeScheduleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Controller
public class OptimizerInterfaceImpl implements OptimizerInterface {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    @Context
    UriInfo uri;

    @Context
    HttpServletRequest httpRequest;

    @Autowired
    OptimizerManager optimizerManager;

    @Autowired
    RequestDao requestDao;

    @Override
    @Transactional
    public Response optimizeSchedule(String apiVersion, OptimizerRequest request) {
        String id = request.getRequestId();
        Observation.report(LogMessages.OPTIMIZE_SCHEDULE, "Received", httpRequest.getRemoteAddr(), id, "");
        Response response = null;
        try {
            optimizerManager.validate(request); // Throws CmsException if invalid message
            OptimizerResponse optimizerResponse = optimizerManager.processOptimizerRequest(request);
            if (optimizerResponse != null)
            {
                response = Response.ok(optimizerResponse).build();
            } else {
                // Request will be processed asynchronously
                response = Response.accepted().build();
            }
        } catch (CmsoException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.OPTIMIZE_SCHEDULE, "Returned", httpRequest.getRemoteAddr(), id,
                        response.getStatusInfo().toString());
        return response;
    }


    @Override
    public Response getPolicies(String apiVersion) {
        // TODO Auto-generated method stub
        Observation.report(LogMessages.GET_POLICIES, "Received", httpRequest.getRemoteAddr(), "", "");
        Response response = null;
        try {
            List<OptimizerResponse> list = new ArrayList<>();
            response = Response.ok(list).build();
            // } catch (CMSException e) {
            // TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            // response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.GET_POLICIES, "Returned", httpRequest.getRemoteAddr(), "",
                        response.getStatusInfo().toString());
        return response;
    }


    @Override
    public Response getSchedule(String apiVersion, String id) {
        // TODO Auto-generated method stub
        Observation.report(LogMessages.GET_SCHEDULE, "Received", httpRequest.getRemoteAddr(), id, "");
        Response response = null;
        try {
            OptimizerResponse atr = new OptimizerResponse();
            atr.setStatus(OptimizeScheduleStatus.CREATED);
            atr.setRequestId(id);
            response = Response.ok(atr).build();
            // } catch (CMSException e) {
            // TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            // response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.GET_SCHEDULE, "Returned", httpRequest.getRemoteAddr(), id,
                        response.getStatusInfo().toString());
        return response;
    }


    @Override
    public Response deleteSchedule(String apiVersion, String id) {
        // TODO Auto-generated method stub
        Observation.report(LogMessages.DELETE_SCHEDULE, "Received", httpRequest.getRemoteAddr(), id, "");
        Response response = null;
        try {
            OptimizerResponse atr = new OptimizerResponse();
            atr.setStatus(OptimizeScheduleStatus.DELETED);
            atr.setRequestId(id);
            response = Response.noContent().build();
            // } catch (CMSException e) {
            // TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
            // response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.DELETE_SCHEDULE, "Returned", httpRequest.getRemoteAddr(), id,
                        response.getStatusInfo().toString());
        return response;
    }
}
