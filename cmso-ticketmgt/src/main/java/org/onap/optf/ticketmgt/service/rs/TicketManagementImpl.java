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

package org.onap.optf.ticketmgt.service.rs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.onap.observations.Observation;
import org.onap.optf.ticketmgt.common.LogMessages;
import org.onap.optf.ticketmgt.service.rs.models.TicketData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

@Controller
public class TicketManagementImpl  implements TicketManagement {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;
    
    @Context 
    UriInfo uri;
    
    @Context 
    HttpServletRequest request;

	@Override
	public Response fetchTicket(String apiVersion, String id, UriInfo uri, HttpServletRequest request) {
		// TODO Auto-generated method stub
        Observation.report(LogMessages.FETCH_TICKET, "Received", request.getRemoteAddr(), id, "");
        Response response = null;
        try 
        {
        	TicketData td = new TicketData();
        	td.setId(id);
            response = Response.ok(td).build();
//        } catch (CMSException e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        	Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
//            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.FETCH_TICKET, "Returned", request.getRemoteAddr(), id, response.getStatusInfo().toString());
        return response;
	}

	@Override
	public Response createTicket(String apiVersion, TicketData ticketData, UriInfo uri, HttpServletRequest request) {
		// TODO Auto-generated method stub
        String id = UUID.randomUUID().toString();
        Observation.report(LogMessages.CREATE_TICKET, "Received", request.getRemoteAddr(), id, "");
        Response response = null;
        try 
        {
        	ticketData.setId(id);
            response = Response.ok(ticketData).build();
//        } catch (CMSException e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        	Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
//            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.CREATE_TICKET, "Returned", request.getRemoteAddr(), id, response.getStatusInfo().toString());
        return response;
	}

	@Override
	public Response updateTicket(String apiVersion, TicketData ticketData, UriInfo uri, HttpServletRequest request) {
		// TODO Auto-generated method stub
        String id = ticketData.getId();
        Observation.report(LogMessages.UPDATE_TICKET, "Received", request.getRemoteAddr(), id, "");
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
        Observation.report(LogMessages.UPDATE_TICKET, "Returned", request.getRemoteAddr(), id, response.getStatusInfo().toString());
		return response;
	}


	@Override
	public Response searchTcikets(String apiVersion, String id, String elementId, String startTime, String finishTime,
			Integer maxTickets, String lastId, UriInfo uri, HttpServletRequest request) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
        Observation.report(LogMessages.SEARCH_TICKETS, "Received", request.getRemoteAddr(), uri.getPath(), "");
        Response response = null;
        List<TicketData> list = new ArrayList<>();
        try 
        {
            response = Response.ok(list).build();
//        } catch (CMSException e) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        	Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
//            response = Response.status(e.getStatus()).entity(e.getRequestError()).build();
        } catch (Exception e) {
        	Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response = Response.serverError().build();
        }
        Observation.report(LogMessages.SEARCH_TICKETS, "Returned", request.getRemoteAddr(), uri.getPath(), response.getStatusInfo().toString());
		return response;
	}

	@Override
	public Response deleteScheduleRequest(String apiVersion, String id, HttpServletRequest request) {
        Observation.report(LogMessages.CANCEL_TICKET, "Received", request.getRemoteAddr(), id, "");
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
        Observation.report(LogMessages.CANCEL_TICKET, "Returned", request.getRemoteAddr(), id, response.getStatusInfo().toString());
		return response;
	}

}
