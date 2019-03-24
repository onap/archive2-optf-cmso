/*
 * Copyright � 2017-2018 AT&T Intellectual Property. Modifications Copyright � 2018 IBM.
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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.ticketmgt.bean.TmApprovalStatusEnum;
import org.onap.optf.cmso.ticketmgt.bean.TmChangeInfo;
import org.onap.optf.cmso.ticketmgt.bean.TmStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

@Controller
public class TicketMgtLoopbackServiceImpl implements TicketMgtLoopbackService {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    PropertiesManagement pm;

    @Override
    public Response tmGetChangeRecord(JsonNode request, UriInfo uri) {
        TmChangeInfo changeInfo = new TmChangeInfo();
        changeInfo.setChangeId("CHG000000000001");
        changeInfo.setStatus(TmStatusEnum.Scheduled.toString());
        changeInfo.setApprovalStatus(TmApprovalStatusEnum.Approved.toString());
        return Response.ok().entity(changeInfo).build();
    }

    @Override
    public Response tmCreateChangeRecord(JsonNode request, UriInfo uri) {
        Map<String, String> response = new HashMap<String, String>();
        response.put("changeId", "CHG000000000001");
        return Response.ok().entity(response).build();
    }

    @Override
    public Response tmCloseCancelChangeRecord(JsonNode request, UriInfo uri) {
        ObjectNode req = (ObjectNode) request;
        String changeId = req.get("changeId").asText();
        String resp = changeId + " was update successfully.";
        return Response.ok().entity(resp).build();
    }

    @Override
    public Response tmUpdateChangeRecord(JsonNode request, UriInfo uri) {
        ObjectNode req = (ObjectNode) request;
        String changeId = req.get("changeId").asText();
        String resp = changeId + " was updated successfully.";
        return Response.ok().entity(resp).build();
    }

}
