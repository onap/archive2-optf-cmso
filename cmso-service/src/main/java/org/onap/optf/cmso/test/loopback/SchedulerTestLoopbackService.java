/*
 * Copyright © 2017-2018 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
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

package org.onap.optf.cmso.test.loopback;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.onap.optf.cmso.optimizer.model.OptimizerRequest;
import org.onap.optf.cmso.optimizer.model.OptimizerResponse;
import org.onap.optf.cmso.so.bean.MsoOrchestrationQueryResponse;
import org.onap.optf.cmso.wf.bean.WfChangeManagementResponse;

@Api
@Path("/v1/loopbacktest")
@Produces({MediaType.APPLICATION_JSON})
public interface SchedulerTestLoopbackService {
    // ******************************************************************
    @POST
    @Path("/optimize/schedule")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Test Optimizer connection in loopback mode.")
    @ApiResponses(value = {@ApiResponse(code = 202, message = "OK"),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response putToOptimizer(@ApiParam(value = "Optimizer request message") OptimizerRequest request,
                    @Context UriInfo uri);

    // ******************************************************************
    @GET
    @Path("/optimize/schedule/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Test Optimizer connection in loopback mode.", response = OptimizerResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response getOptimizerResponse(@ApiParam(value = "Optimizer request is") @PathParam("id") String id,
                    @Context UriInfo uri);

    // ******************************************************************
    @POST
    @Path("/onap/so/infra/orchestrationRequests/v7/schedule/{vnfName}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Test SO sheduling in loopback mode.",
                    response = WfChangeManagementResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response soScheduleLoopback(@ApiParam(value = "vnfName") @PathParam("vnfName") String vnfName,
                    @ApiParam(value = "SO request message") String request, @Context UriInfo uri);

    // ******************************************************************
    @GET
    @Path("/onap/so/infra/orchestrationRequests/v7/{requestId}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Test SO Status query loopback.", response = MsoOrchestrationQueryResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response soQueryLoopback(@ApiParam(value = "MSO request ID") @PathParam("requestId") String requestId,
                    @Context UriInfo uri);

}
