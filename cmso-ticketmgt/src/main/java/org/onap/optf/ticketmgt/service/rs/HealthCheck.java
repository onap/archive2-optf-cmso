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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.optf.ticketmgt.service.rs.models.HealthCheckMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Administration")
@Path("/{apiVersion}")
@Produces({MediaType.APPLICATION_JSON})
public interface HealthCheck {

    // ******************************************************************
    @GET
    @Path("/health")
    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/{apiVersion}/health", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "", notes = "Returns health status of server.", response = HealthCheckMessage.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Not healthy", response = HealthCheckMessage.class)})
    public Response healthCheck(
    		@ApiParam(value = "v1") @PathParam("apiVersion") @DefaultValue("v1") String apiVersion,
            @ApiParam(value = "Check Interfaces") @QueryParam("checkInterfaces") @DefaultValue(value = "true") Boolean checkInterfaces);
}
