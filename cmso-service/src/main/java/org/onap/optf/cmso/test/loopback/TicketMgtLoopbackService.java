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

package org.onap.optf.cmso.test.loopback;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@Path("/v1/tm")
@Produces({MediaType.APPLICATION_JSON})
public interface TicketMgtLoopbackService {

    // ******************************************************************
    @POST
    @Path("/getChangeRecord")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Dummy out ticket management check status call.", response = JsonNode.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response tmGetChangeRecord(@ApiParam(value = "TM request message") JsonNode request, @Context UriInfo uri);

    // ******************************************************************
    @POST
    @Path("/createChangeRecord")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Dummy out ticket management create call.", response = JsonNode.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response tmCreateChangeRecord(@ApiParam(value = "TM request message") JsonNode request,
            @Context UriInfo uri);

    // ******************************************************************
    @POST
    @Path("/closeCancelChangeRecord")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Dummy out ticket management close call.", response = JsonNode.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response tmCloseCancelChangeRecord(@ApiParam(value = "TM request message") JsonNode request,
            @Context UriInfo uri);

    // ******************************************************************
    @POST
    @Path("/updateChangeRecord")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "", notes = "Dummy out ticket management update to in progress call.",
            response = JsonNode.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response tmUpdateChangeRecord(@ApiParam(value = "TM request message") JsonNode request,
            @Context UriInfo uri);

}
