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

package org.onap.optf.cmso.service.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.onap.optf.cmso.common.CMSRequestError;
import org.onap.optf.cmso.model.Schedule;
import org.onap.optf.cmso.service.rs.models.ApprovalMessage;
import org.onap.optf.cmso.service.rs.models.CMSMessage;
import org.onap.optf.cmso.service.rs.models.CmDetailsMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@Path("/{apiVersion}")
@Produces({MediaType.APPLICATION_JSON})
public interface CMSOService {
    // ******************************************************************
    @GET
    @Path("/schedules")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Returns a list of Scheduler Requests based upon the filter criteria.",
            response = Schedule.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "No records found", response = CMSRequestError.class),
            @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response searchScheduleRequests(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @DefaultValue(value = "false") @ApiParam(
                    value = "Include details") @QueryParam("includeDetails") Boolean includeDetails,
            @ApiParam(value = "Schedule identifier", allowMultiple = true) @QueryParam("scheduleId") String scheduleId,
            @ApiParam(value = "Schedule name", allowMultiple = true) @QueryParam("scheduleName") String scheduleName,
            @ApiParam(value = "SCheduler creator User id of ",
                    allowMultiple = true) @QueryParam("userId") String userId,
            @ApiParam(value = "Schedule status", allowMultiple = true) @QueryParam("status") String status,
            @ApiParam(value = "Creation date and time (<low date>[,<hi date>])",
                    allowMultiple = true) @QueryParam("createDateTime") String createDateTime,
            @ApiParam(value = "Optimizer status",
                    allowMultiple = true) @QueryParam("optimizerStatus") String optimizerStatus,
            @ApiParam(value = "Workflow", allowMultiple = true) @QueryParam("WorkflowName") String workflowName,
            @Context UriInfo uri, @Context HttpServletRequest request);

    // ******************************************************************
    @POST
    @Path("/schedules/{scheduleId}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Creates a schedule request for scheduleId")
    @ApiResponses(
            value = {@ApiResponse(code = 202, message = "Schedule request accepted for optimization."),
                    @ApiResponse(code = 409, message = "Schedule request already exists for this schedule id.",
                            response = CMSRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error")})
    public Response createScheduleRequest(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(
                    value = "Schedule id to uniquely identify the schedule request being created.") @PathParam("scheduleId") String scheduleId,
            @ApiParam(
                    value = "Data for creating a schedule request for the given schedule id") CMSMessage scheduleMessage,
            @Context HttpServletRequest request);

    // ******************************************************************
    @DELETE
    @Path("/schedules/{scheduleId}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Cancels the schedule request for scheduleId")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Delete successful"),
            @ApiResponse(code = 404, message = "No record found", response = CMSRequestError.class),
            @ApiResponse(code = 500, message = "Unexpected Runtime error")})
    public Response deleteScheduleRequest(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(
                    value = "Schedule id to uniquely identify the schedule request being deleted.") @PathParam("scheduleId") String scheduleId,
            @Context HttpServletRequest request);

    // ******************************************************************
    @GET
    @Path("/schedules/{scheduleId}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Retrieve the schedule request for scheduleId", response = Schedule.class)
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "No record found"),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error")})
    public Response getScheduleRequestInfo(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(
                    value = "Schedule id to uniquely identify the schedule info being retrieved.") @PathParam("scheduleId") String scheduleId,
            @Context HttpServletRequest request);

    // ******************************************************************
    @POST
    @Path("/schedules/{scheduleId}/approvals")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "",
            notes = "Adds an accept/reject approval status to the schedule request identified by scheduleId")
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "No record found"),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error")})
    public Response approveScheduleRequest(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(
                    value = "Schedule id to uniquely identify the schedule request being accepted or rejected.") @PathParam("scheduleId") String scheduleId,
            @ApiParam(value = "Accept or reject approval message") ApprovalMessage approval,
            @Context HttpServletRequest request);

    // ******************************************************************
    @GET
    @Path("/schedules/scheduleDetails")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Returns a list of Schedule request details based upon the filter criteria.",
            response = CmDetailsMessage.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "No records found", response = CMSRequestError.class),
            @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response searchScheduleRequestDetails(
            @ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Schedule identifier",
                    allowMultiple = true) @QueryParam("request.scheduleId") String scheduleId,
            @ApiParam(value = "Schedule name",
                    allowMultiple = true) @QueryParam("request.scheduleName") String scheduleName,
            @ApiParam(value = "Scheduler creator User id of ",
                    allowMultiple = true) @QueryParam("request.userId") String userId,
            @ApiParam(value = "Schedule status", allowMultiple = true) @QueryParam("request.status") String status,
            @ApiParam(value = "Creation date and time (<low date>[,<hi date>])",
                    allowMultiple = true) @QueryParam("request.createDateTime") String createDateTime,
            @ApiParam(value = "Optimizer status",
                    allowMultiple = true) @QueryParam("request.optimizerStatus") String optimizerStatus,
            @ApiParam(value = "Request Approval user id",
                    allowMultiple = true) @QueryParam("request.approvalUserId") String requestApprovalUserId,
            @ApiParam(value = "Request Approval status",
                    allowMultiple = true) @QueryParam("request.approvalStatus") String requestApprovalStatus,
            @ApiParam(value = "Request Approval type",
                    allowMultiple = true) @QueryParam("request.approvalType") String requestApprovalType,
            @ApiParam(value = "Workflow", allowMultiple = true) @QueryParam("WorkflowName") String workflowName,
            @ApiParam(value = "VNF Name", allowMultiple = true) @QueryParam("vnfName") String vnfName,
            @ApiParam(value = "VNF Id", allowMultiple = true) @QueryParam("vnfId") String vnfId,
            @ApiParam(value = "VNF Status", allowMultiple = true) @QueryParam("vnfStatus") String vnfStatus,
            // @ApiParam(value="VNF Schedule Id", allowMultiple=true) @QueryParam("vnfScheduleId")
            // String
            // vnfScheduleId,
            @ApiParam(value = "Start time <low>,<high>",
                    allowMultiple = true) @QueryParam("startTime") String startTime,
            @ApiParam(value = "Finish time <low>,<high>",
                    allowMultiple = true) @QueryParam("finishTime") String finishTime,
            @ApiParam(value = "Last instance start time <low>,<high>",
                    allowMultiple = true) @QueryParam("lastInstanceTime") String lastInstanceTime,
            @ApiParam(value = "TM Change Ticket Change Id",
                    allowMultiple = true) @QueryParam("tmChangeId") String tmChangeId,
            // @ApiParam(value="Approval user id", allowMultiple=true) @QueryParam("approvalUserId")
            // String approvalUserId,
            // @ApiParam(value="Approval status", allowMultiple=true) @QueryParam("approvalStatus")
            // String
            // approvalStatus,
            // @ApiParam(value="Approval type", allowMultiple=true) @QueryParam("approvalType")
            // String
            // approvalType,
            @ApiParam(value = "Maximum number of schedules to return") @QueryParam("maxSchedules") Integer maxSchedules,
            @ApiParam(value = "Return schedules > lastScheduleId") @QueryParam("lastScheduleId") String lastScheduleId,
            @ApiParam(
                    value = "Return concurrencyLimit") @QueryParam("request.concurrencyLimit") Integer concurrencyLimit,
            @Context UriInfo uri, @Context HttpServletRequest request);

}
