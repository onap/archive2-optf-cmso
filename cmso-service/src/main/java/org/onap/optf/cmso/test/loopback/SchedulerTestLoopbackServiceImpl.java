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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.onap.optf.cmso.common.BasicAuthenticatorFilter;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.optimizer.bean.CMOptimizerRequest;
import org.onap.optf.cmso.optimizer.bean.CMOptimizerResponse;
import org.onap.optf.cmso.optimizer.bean.CMRequestInfo;
import org.onap.optf.cmso.optimizer.bean.CMSchedule;
import org.onap.optf.cmso.optimizer.bean.CMSchedulingInfo;
import org.onap.optf.cmso.optimizer.bean.CMVnfDetails;
import org.onap.optf.cmso.service.rs.CMSOOptimizerCallbackImpl;
import org.onap.optf.cmso.wf.bean.WfCmResponse200;
import org.onap.optf.cmso.wf.bean.WfMsoRequestReferences;
import org.onap.optf.cmso.wf.bean.WfMsoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

@Controller
public class SchedulerTestLoopbackServiceImpl implements SchedulerTestLoopbackService {
    private static EELFLogger log = EELFManager.getInstance().getLogger(SchedulerTestLoopbackServiceImpl.class);
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();

    @Autowired
    Environment env;

    @Autowired
    PropertiesManagement pm;

    @Override
    public Response putToOptimizer(CMOptimizerRequest request, UriInfo uri) {
        //
        try {
            CMOptimizerResponse r = new CMOptimizerResponse();
            CMRequestInfo ri = request.getRequestInfo();
            CMSchedulingInfo si = request.getSchedulingInfo();
            r.setTransactionId(ri.getTransactionId());
            r.setRequestState("Done.");
            r.setScheduleId(ri.getRequestId());
            String callback = ri.getCallbackUrl();

            // This is a dumb opt. WIll not make sense for multiple groups
            // Use the code in the callback to help
            List<String> nodes = new ArrayList<String>();
            // get total number of nodes across all groups.
            for (CMVnfDetails sr : si.getVnfDetails()) {
                nodes.add(sr.getNode());
            }

            DateTime startTime = CMSOOptimizerCallbackImpl.convertISODate(si.getStartTime(), "startTime");

            // Ignore the finish time for now in the calc. Just accept what they
            // gave
            DateTime finishTime = CMSOOptimizerCallbackImpl.convertISODate(si.getEndTime(), "endTime");
            DateTimeFormatter sniroFmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();

            int add = si.getAdditionalDurationInSecs();
            int normal = si.getNormalDurationInSecs();
            int concurrencyLimit = si.getConcurrencyLimit();
            long totalDuration = (long) add + normal;
            long serialized = 0;
            if (nodes.size() > concurrencyLimit) {
                serialized = (nodes.size() / concurrencyLimit);
                serialized = (serialized * totalDuration) * 1000;
            }
            DateTime latestInstanceStartTime = startTime.plus(serialized);
            finishTime = latestInstanceStartTime.plus(totalDuration * 1000);
            // Reformat request into a response setting the groups start finish
            // time based upon
            Map<String, CMSchedule> map = new HashMap<String, CMSchedule>();
            for (CMVnfDetails sr : si.getVnfDetails()) {
                String groupId = sr.getGroupId();
                CMSchedule cms = map.get(groupId);
                if (cms == null) {
                    cms = new CMSchedule();
                    cms.setGroupId(groupId);
                    cms.setFinishTime(groupId);
                    map.put(groupId, cms);
                    cms.setStartTime(sniroFmt.print(startTime));
                    cms.setFinishTime(sniroFmt.print(finishTime));
                    cms.setLatestInstanceStartTime(sniroFmt.print(latestInstanceStartTime));
                }
                cms.getNode().add(sr.getNode());
            }
            r.setSchedule(map.values().toArray(new CMSchedule[map.values().size()]));

            Thread responseThread = new Thread(new Runnable() {
                public void run() {
                    sendAsyncResponse(r, callback);
                }
            });
            responseThread.start();

            return Response.accepted().build();
        } catch (Exception e) {
            log.error("Unexpected exception", e);
        }
        return Response.serverError().build();
    }

    private void sendAsyncResponse(CMOptimizerResponse r, String url) {
        try {
            Client client = ClientBuilder.newClient();
            String user = env.getProperty("mechid.user", "");
            String pass = pm.getProperty("mechid.pass", "");
            client.register(new BasicAuthenticatorFilter(user, pass));
            WebTarget target = client.target(url);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            try {
                Response response = invocationBuilder.post(Entity.json(r));
                switch (response.getStatus()) {
                    case 202:
                        // Scheduled with SNIRO
                        break;
                    case 400: // Bad request
                    case 500:
                    default: {
                    }
                }
            } catch (ResponseProcessingException e) {
                errors.error(LogMessages.OPTIMIZER_EXCEPTION, e, e.getMessage());
                debug.debug(LogMessages.OPTIMIZER_EXCEPTION, e, e.getMessage());

            } catch (ProcessingException e) {
                log.error(LogMessages.OPTIMIZER_EXCEPTION.toString(), e);
                log.error(LogMessages.OPTIMIZER_EXCEPTION, e.getMessage());
            }
        } catch (Exception e) {
            errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
            debug.debug(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());

        }
    }

    public Response soScheduleLoopback(String vnfName, String request, UriInfo uri) {
        String msoRequestId = env.getProperty("loopback.mso.requestId", "4ccbfb85-1d05-442e");
        String r = UUID.randomUUID().toString();
        WfMsoRequestReferences rr = new WfMsoRequestReferences();
        rr.setInstanceId(r);
        rr.setRequestId(msoRequestId);
        WfMsoResponse mso = new WfMsoResponse();
        mso.setRequestReferences(rr);
        WfCmResponse200 cmResponse = new WfCmResponse200();
        cmResponse.setEntity(mso);
        cmResponse.setStatus(202);;
        return Response.status(Status.OK).entity(cmResponse).build();
    }

    @Override
    public Response soQueryLoopback(String requestId, UriInfo uri) {
        // Abbreviated response. Only interested in requestStatus....
        String response = "{\"request\" : {" + "\"requestId\" : \"dummy-request-id\","
                + "\"startTime\" : \"Wed, 26 Aug 2017 06:36:07 GMT\"," + "\"requestScope\" : \"vfModule\","
                + "\"requestType\" : \"createInstance\"," + "\"requestDetails\" : {}," + "\"instanceReferences\" : {},"
                + "\"requestStatus\" : { " + "\"requestState\" : \"COMPLETE\","
                + "\"statusMessage\" : \"Vf Module has been created successfully.\"," + "\"percentProgress\" : 100,"
                + "\"finishTime\" : \"Crap so cmso uses current time\"}}}";
        return Response.ok().entity(response).build();
    }

}
