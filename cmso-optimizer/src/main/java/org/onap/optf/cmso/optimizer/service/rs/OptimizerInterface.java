/*
 * Copyright � 2017-2019 AT&T Intellectual Property.
 * Modifications Copyright � 2018 IBM.
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

package org.onap.optf.cmso.optimizer.service.rs;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.optf.cmso.common.CMSRequestError;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerRequest;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerResponse;
import org.onap.optf.cmso.optimizer.service.rs.models.PolicyInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Optimizer Interface")
@Path("/{apiVersion}")
@Produces({MediaType.APPLICATION_JSON})
public interface OptimizerInterface {
    // ******************************************************************
    
    @POST
    @Path("/optimize/shedule")
    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/{apiVersion}/optimize/shedule", method = RequestMethod.POST, 
    		consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "", notes = "API to request schedule optimization for the passed elements." 
)
    @ApiResponses(
            value = {@ApiResponse(code = 202, message = "Accepted"), 
            		@ApiResponse(code = 400, message = "Bad request", response = CMSRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response optimizeSchedule(
            @ApiParam(value = "v1") @PathParam("apiVersion") @PathVariable(value="v1") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Optimization data.")  OptimizerRequest optimizerRequest
            );

    @GET
    @Path("/policies")
    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/{apiVersion}/policies", method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "", notes = "API to retrieve supported change management policies.", 
    		response = PolicyInfo.class, responseContainer = "List")
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "OK"), 
            		@ApiResponse(code = 400, message = "Bad request", response = CMSRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response getPolicies(
            @ApiParam(value = "v1") @PathParam("apiVersion") @PathVariable(value="v1") @DefaultValue("v1") String apiVersion
            );

    @GET
    @Path("/optimize/schedule/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/{apiVersion}/schedule/{id}", method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "", notes = "API to poll for  "
    		+ " optimized schedule.", 
    		response = OptimizerResponse.class)
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "OK"), 
            		@ApiResponse(code = 404, message = "Not found.", response = CMSRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response getSchedule(
            @ApiParam(value = "v1") @PathParam("apiVersion") @PathVariable(value="v1") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Request id") @PathParam("id") @PathVariable(value="id")  String id
            );

    @DELETE
    @Path("/optimize/schedule/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/{apiVersion}/schedule/{id}", method = RequestMethod.DELETE, 
    		produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "", notes = "API to acknowledge and delete"
    		+ " optimized schedule request. Acknowledgesthat optimization has rsults have been retrieved an are safe to delete")
    @ApiResponses(
            value = {@ApiResponse(code = 204, message = "Deleted"), 
            		@ApiResponse(code = 404, message = "Not found.", response = CMSRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response deleteSchedule(
            @ApiParam(value = "v1") @PathParam("apiVersion") @PathVariable(value="v1") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Request id") @PathParam("id") @PathVariable(value="id")  String id
            );



}
