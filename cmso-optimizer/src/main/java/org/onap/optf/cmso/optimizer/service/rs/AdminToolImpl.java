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

package org.onap.optf.cmso.optimizer.service.rs;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.onap.optf.cmso.optimizer.common.PropertiesManagement;
import org.springframework.stereotype.Controller;

@Controller
public class AdminToolImpl implements AdminTool {
    private static EELFLogger log = EELFManager.getInstance().getLogger(AdminToolImpl.class);


    @Context
    UriInfo uri;

    @Context
    HttpServletRequest request;

    @Override
    public Response exec(String apiVersion, String id) {
        log.info("AdminTool.exec entered " + uri.getPath());
        if (id.length() < 4) {
            return Response.ok("").build();
        }
        String encrypted = PropertiesManagement.getEncryptedValue(id);
        Response response = Response.ok(encrypted).build();
        return response;
    }

}
