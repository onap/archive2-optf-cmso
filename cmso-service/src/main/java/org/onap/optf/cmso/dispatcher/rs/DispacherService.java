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

package org.onap.optf.cmso.dispatcher.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@Path("/dispatch")
@Produces({MediaType.APPLICATION_JSON})
public interface DispacherService {

    // ******************************************************************
    @GET
    @Path("/schedule/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Does dispsatch of provided cm schedule id.", response = Integer.class)
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Request failed")})
    public Response dispatchSchedule(@ApiParam(value = "Identifier", allowMultiple = false) @PathParam("id") String id,
            @Context UriInfo uri, @Context HttpServletRequest request);

    // ******************************************************************
    @GET
    @Path("/optimizer/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Does dispsatch of provided cm schedule id.", response = Integer.class)
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Request failed")})
    public Response dispatchOptimizer(
            @ApiParam(value = "Identifier", allowMultiple = false) @PathParam("id") String id, @Context UriInfo uri,
            @Context HttpServletRequest request);

    // ******************************************************************
    @GET
    @Path("/schedulestatus/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Does dispsatch of provided schedule id.", response = Integer.class)
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Request failed")})
    public Response dispatchScheduleStatus(
            @ApiParam(value = "Identifier", allowMultiple = false) @PathParam("id") String id, @Context UriInfo uri,
            @Context HttpServletRequest request);

    // ******************************************************************
    @GET
    @Path("/sostatus/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Does dispsatch of provided cm schedule id.", response = Integer.class)
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Request failed")})
    public Response dispatchSoStatus(@ApiParam(value = "Identifier", allowMultiple = true) @PathParam("id") String id,
            @Context UriInfo uri, @Context HttpServletRequest request);

}
