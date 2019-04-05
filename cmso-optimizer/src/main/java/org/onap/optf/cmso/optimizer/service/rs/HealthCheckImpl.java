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
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.onap.optf.cmso.optimizer.model.dao.RequestDao;
import org.onap.optf.cmso.optimizer.service.rs.models.HealthCheckComponent;
import org.onap.optf.cmso.optimizer.service.rs.models.HealthCheckMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

@Controller
public class HealthCheckImpl implements HealthCheck {
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();


    @Autowired
    Environment env;

    @Autowired
    RequestDao requestDao;



    @Context
    UriInfo uri;

    @Context
    HttpServletRequest request;


    @Override
    public Response healthCheck(String apiVersion, Boolean checkInterfaces) {
        debug.debug("Entered healthcheck");
        Response response = null;
        HealthCheckMessage hc = new HealthCheckMessage();
        hc.setHealthy(true);

        addToHealthCheckMessage(hc, this.healthCheckDb());

        if (hc.getHealthy()) {
            response = Response.ok().entity(hc).build();
        }
        else {
            response = Response.status(Response.Status.BAD_REQUEST).entity(hc).build();
        }
        return response;
    }

    private void addToHealthCheckMessage(HealthCheckMessage hc, HealthCheckComponent hcc) {
        if (!hcc.getHealthy()) {
            hc.setHealthy(false);
        }

        hc.setHostname(System.getenv("HOSTNAME"));
        hc.addComponent(hcc);
    }

    private HealthCheckComponent healthCheckDb() {
        HealthCheckComponent hcc = new HealthCheckComponent();
        hcc.setName("Optimizer database");
        String url = env.getProperty("spring.datasource.url");
        hcc.setUrl(url);
        try {
            requestDao.findById(UUID.randomUUID());
            hcc.setHealthy(true);
            hcc.setStatus("OK");
        } catch (Exception e) {
            hcc.setStatus(e.getMessage());

        }
        return hcc;
    }



}
