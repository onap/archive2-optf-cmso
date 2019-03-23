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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.optf.cmso.common.CMSRequestError;
import org.onap.optf.cmso.service.rs.models.v2.OptimizedScheduleMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("CMSO Optimized Schedule API")
@Path("/{apiVersion}")
@Produces({MediaType.APPLICATION_JSON})
public interface CmsoOptimizedScheduleService {

    // ******************************************************************
    @POST
    @Path("/schedules/optimized/{scheduleId}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Creates a request for an optimized schedule")
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
                    value = "Data for creating a schedule request for the given schedule id") OptimizedScheduleMessage scheduleMessage);


}
