/*
 * Copyright © 2017-2019 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
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

package org.onap.optf.cmso.topology.service.rs;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.onap.optf.cmso.common.CmsoRequestError;
import org.onap.optf.cmso.topology.service.rs.models.TopologyRequest;
import org.onap.optf.cmso.topology.service.rs.models.TopologyResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api("Topology Interface")
@Path("/{apiVersion}")
@Produces({MediaType.APPLICATION_JSON})
public interface TopologyInterface {
    // ******************************************************************

    @POST
    @Path("/current/")
    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/{apiVersion}/current", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON,
                    produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Request Topology",
                    notes = "API to retrieve toplogy for scheduling 'conflict free' mainentance."
                                    + " Retrieves the element information related to the list of elements"
                                    + " targeted for mainenance activity."
                                    + " Scope of related elements to be returned are defined in the passed"
                                    + " ToplogogyRequest."
                                    + " Elements returned must include in the elementData, the identifier that"
                                    + " the element is known as"
                                    + " in the ticket management system."
                                    + "\nThe Topology Service may implement asynchronous requests by"
                                    + " returning IN_PROGRESS status."
                                    + " If IN_PROGRESS, the optimizer will begin polling until"
                                    + " COMPLETED is returned with the response. ",
                    response = TopologyResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
                    @ApiResponse(code = 400, message = "Bad request", response = CmsoRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response retrieveCurrentTopology(
                    @ApiParam(value = "v1") @PathParam("apiVersion") @PathVariable(
                                    value = "v1") @DefaultValue("v1") String apiVersion,
                    @ApiParam(value = "Topology criteria.") TopologyRequest topologyRequest);

    @GET
    @Path("/current/request/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/current/request/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON,
                    produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Poll Asynchronous Topology Request",
                    notes = "If a topology request results in asynchronous request (IN_PROGRESS)"
                                    + " this GET is used to retrieve status until COMPLETED."
                                    + " At which time, the optimizer will "
                                    + " issue a DELETE to acknowledge receipt."
                                    + "\nThe Topology Service implementation may delete the cache when"
                                    + " returning completed."
                                    + " The optimizer will treat subsequent not found on delete as normal.",
                    response = TopologyResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
                    @ApiResponse(code = 404, message = "Not Found", response = CmsoRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response getTopologyRequest(
                    @ApiParam(value = "v1") @PathParam("apiVersion") @PathVariable(
                                    value = "v1") @DefaultValue("v1") String apiVersion,
                    @ApiParam(value = "Request Id") @PathParam("id") String id);

    @DELETE
    @Path("/current/request/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/current/request/{id}", method = RequestMethod.DELETE,
                    consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Acknowledge Topology Response", notes = "API to acknowledge COMPLETED toplogy request.",
                    response = TopologyResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
                    @ApiResponse(code = 404, message = "Not Found", response = CmsoRequestError.class),
                    @ApiResponse(code = 500, message = "Unexpected Runtime error", response = Exception.class)})
    public Response deleteTopologyRequest(
                    @ApiParam(value = "v1") @PathParam("apiVersion") @PathVariable(
                                    value = "v1") @DefaultValue("v1") String apiVersion,
                    @ApiParam(value = "Request Id") @PathParam("id") String id);


}
