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

package org.onap.optf.cmso.topology.service.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.onap.observations.Observation;
import org.onap.optf.cmso.topology.common.LogMessages;
import org.onap.optf.cmso.topology.service.rs.models.TopologyRequest;
import org.onap.optf.cmso.topology.service.rs.models.TopologyResponse;
import org.onap.optf.cmso.topology.service.rs.models.TopologyResponse.TopologyRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

@Controller
public class TopologyInterfaceImpl  implements TopologyInterface {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;
    
    @Context 
    UriInfo uri;
    
    @Context 
    HttpServletRequest request;


	@Override
	public Response retrieveCurrentTopology(String apiVersion, TopologyRequest topologyRequest) 
	{
		// TODO Auto-generated method stub
		String id = topologyRequest.getRequestId();
        Observation.report(LogMessages.GET_ACTIVE_TICKETS, "Received", request.getRemoteAddr(), id, "");
        Response response = null;
        try 
        {
        	TopologyResponse atr = new TopologyResponse();
        	atr.setRequestId(topologyRequest.getRequestId());
        	atr.setStatus(TopologyRequestStatus.COMPLETED);
            response = Response.ok(atr).build();
//        } catch (CMSException e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        	Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
//            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.GET_ACTIVE_TICKETS, "Returned", request.getRemoteAddr(), id, response.getStatusInfo().toString());
		return response;
	}


	@Override
	public Response getTopologyRequest(String apiVersion, String id) {
		// TODO Auto-generated method stub
        Observation.report(LogMessages.GET_ACTIVE_TICKETS, "Received", request.getRemoteAddr(), id, "");
        Response response = null;
        try 
        {
        	TopologyResponse atr = new TopologyResponse();
        	atr.setRequestId(id);
        	atr.setStatus(TopologyRequestStatus.COMPLETED);
            response = Response.ok(atr).build();
//        } catch (CMSException e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        	Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
//            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.GET_ACTIVE_TICKETS, "Returned", request.getRemoteAddr(), id, response.getStatusInfo().toString());
		return response;
	}


	@Override
	public Response deleteTopologyRequest(String apiVersion, String id) {
		// TODO Auto-generated method stub
        Observation.report(LogMessages.GET_ACTIVE_TICKETS, "Received", request.getRemoteAddr(), id, "");
        Response response = null;
        try 
        {
            response = Response.noContent().build();
//        } catch (CMSException e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        	Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
//            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.GET_ACTIVE_TICKETS, "Returned", request.getRemoteAddr(), id, response.getStatusInfo().toString());
		return response;
	}
}
