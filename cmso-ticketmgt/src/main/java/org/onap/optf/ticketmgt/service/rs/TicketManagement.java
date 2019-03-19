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

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.optf.cmso.common.CMSRequestError;
import org.onap.optf.ticketmgt.service.rs.models.TicketData;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Ticket Management")
@Path("/{apiVersion}")
@Produces({MediaType.APPLICATION_JSON})
public interface TicketManagement {
    // ******************************************************************
    @GET
    @Path("/ticket/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Fetch Ticket", notes = "Returns ticket information for the provided ticket id.",
            response = TicketData.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "No record found", response = CMSRequestError.class),
            @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response fetchTicket(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Unique ticket identifier") @PathParam("id")  String id);

    // ******************************************************************
    @POST
    @Path("/ticket/")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Create Ticket", notes = "Creates a ticket for the passed data")
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "Ticket Created. Ticket Id returned.", response=TicketData.class),
                    @ApiResponse(code = 400, message = "Bad request.", response = CMSRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response createTicket(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Data for creating a ticket") TicketData ticketData);

    // ******************************************************************
    @PUT
    @Path("/ticket/")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Update Ticket", notes = "Updates a ticket to the passed data")
    @ApiResponses(
            value = {@ApiResponse(code = 204, message = "Ticket Updated."),
                    @ApiResponse(code = 400, message = "Bad request.", response = CMSRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response updateTicket(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Data for updating a ticket") TicketData ticketData);

    // ******************************************************************
    @DELETE
    @Path("/ticket/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Cancel the ticket", notes = "Cancels the ticket.")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Delete successful"),
            @ApiResponse(code = 404, message = "No record found", response = CMSRequestError.class),
            @ApiResponse(code = 400, message = "Bad request", response = CMSRequestError.class),
            @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response deleteScheduleRequest(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Ticket id to uniquely identify the ticket being deleted.") @PathParam("id") String id);

    

    // ******************************************************************
    @GET
    @Path("/tickets")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Search Tickets", notes = "Returns a list of based upon the filter criteria.",
            response = TicketData.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request", response = CMSRequestError.class),
            @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response searchTcikets(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Ticket identifier",
                    allowMultiple = true) @QueryParam("id") String id,
            @ApiParam(value = "Element Id",
                    allowMultiple = true) @QueryParam("elementId") String elementId,
            @ApiParam(value = "Start time <low>,<high>",
                    allowMultiple = true) @QueryParam("startTime") String startTime,
            @ApiParam(value = "Finish time <low>,<high>",
                    allowMultiple = true) @QueryParam("finishTime") String finishTime,
            @ApiParam(value = "Maximum number of tickets to return") @QueryParam("maxTickets") Integer maxTickets,
            @ApiParam(value = "Return tickets > last id") @QueryParam("lastId") String lastId);

}
