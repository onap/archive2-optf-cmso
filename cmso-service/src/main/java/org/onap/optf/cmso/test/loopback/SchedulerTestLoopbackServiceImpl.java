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

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.joda.time.DateTime;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.common.PropertiesManagement;
import org.onap.optf.cmso.optimizer.model.OptimizerElementInfo;
import org.onap.optf.cmso.optimizer.model.OptimizerRequest;
import org.onap.optf.cmso.optimizer.model.OptimizerResponse;
import org.onap.optf.cmso.optimizer.model.OptimizerScheduleInfo;
import org.onap.optf.cmso.optimizer.model.ScheduledElement;
import org.onap.optf.cmso.optimizer.model.ScheduledElement.ScheduleType;
import org.onap.optf.cmso.optimizer.model.UnScheduledElement;
import org.onap.optf.cmso.service.rs.CmsoOptimizerCallbackImpl;
import org.onap.optf.cmso.service.rs.models.v2.ChangeWindow;
import org.onap.optf.cmso.wf.bean.WfCmResponse200;
import org.onap.optf.cmso.wf.bean.WfMsoRequestReferences;
import org.onap.optf.cmso.wf.bean.WfMsoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

@Controller
public class SchedulerTestLoopbackServiceImpl implements SchedulerTestLoopbackService {
    private static EELFLogger log = EELFManager.getInstance().getLogger(SchedulerTestLoopbackServiceImpl.class);
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    @Autowired
    PropertiesManagement pm;

    // **
    // This is test mode only code.
    private static Map<String, String> optimizerResponses = new HashMap<>();

    @Override
    public Response putToOptimizer(OptimizerRequest request, UriInfo uri) {
        //
        try {
            OptimizerResponse rsp = new OptimizerResponse();
            rsp.setRequestId(request.getRequestId());
            rsp.setStatus(OptimizerResponse.OptimizeScheduleStatus.COMPLETED);
            List<OptimizerScheduleInfo> schedules = getSchedules(request);
            rsp.setSchedules(schedules);
            ObjectMapper om = new ObjectMapper();
            String response = om.writeValueAsString(rsp);
            optimizerResponses.put(rsp.getRequestId(), response);
            return Response.accepted().build();
        } catch (Exception e) {
            log.error("Unexpected exception", e);
        }
        return Response.serverError().build();
    }

    private List<OptimizerScheduleInfo> getSchedules(OptimizerRequest request) {
        List<OptimizerScheduleInfo> list = new ArrayList<>();
        OptimizerScheduleInfo osi = new OptimizerScheduleInfo();
        List<ScheduledElement> scheduledList = new ArrayList<>();
        List<UnScheduledElement> unscheduledList = new ArrayList<>();
        osi.setScheduledElements(scheduledList);
        osi.setUnScheduledElements(unscheduledList);
        list.add(osi);


        List<ChangeWindow> cws = request.getChangeWindows();
        // Assume we cannot get here without at least 1 CW
        ChangeWindow cw = cws.get(0);

        // This is a dumb opt. WIll not make sense for multiple groups
        // Use the code in the callback to help
        Map<String, String> nodes = new HashMap<>();
        List<String> nodeList = new ArrayList<>();
        // get total number of nodes across all groups.
        for (OptimizerElementInfo sr : request.getElements()) {
            nodes.put(sr.getElementId(), sr.getGroupId());
            nodeList.add(sr.getElementId());
        }

        DateTime startTime = new DateTime(cw.getStartTime().getTime());


        long add = request.getAdditionalDuration() * 1000L;
        long normal = request.getNormalDuration() * 1000L;
        int concurrencyLimit = request.getConcurrencyLimit();
        long totalDuration = add + normal;
        long serialized = 0;
        if (nodes.size() > concurrencyLimit) {
            serialized = (nodes.size() / concurrencyLimit);
            serialized = (serialized * totalDuration);
        }
        DateTime latestInstanceStartTime = startTime.plus(serialized);
        DateTime finishTime = latestInstanceStartTime.plus(totalDuration);
        // Reformat request into a response setting the groups start finish
        // time based upon

        Map<String, Map<String, Long>> startAndFinishTimeMap = new HashMap<String, Map<String, Long>>();
        try {
            CmsoOptimizerCallbackImpl.makeMap(startTime.getMillis(), latestInstanceStartTime.getMillis(),
                            concurrencyLimit, totalDuration, nodeList, startAndFinishTimeMap);
            for (String node : nodes.keySet()) {
                Map<String, Long> map = startAndFinishTimeMap.get(node);
                Long nodeStart = map.get("startTime");
                Long nodeEnd = map.get("finishTime");
                ScheduledElement se = new ScheduledElement();
                se.setElementId(node);
                se.setDurationSeconds((nodeEnd - nodeStart) / 1000); // in seconds
                se.setStartTime(new Date(nodeStart));
                se.setEndTime(new Date(nodeEnd));
                se.setScheduleType(ScheduleType.INDIVIDUAL);
                se.setGroupId(nodes.get(node));
                scheduledList.add(se);
            }
        } catch (Exception e) {
            Observation.report(LogMessages.EXPECTED_EXCEPTION, e, e.getMessage());
        }

        return list;
    }

    @Override
    public Response getOptimizerResponse(String id, UriInfo uri) {
        //
        try {
            String rsp = optimizerResponses.get(id);
            return Response.ok(rsp).build();
        } catch (Exception e) {
            log.error("Unexpected exception", e);
        }
        return Response.serverError().build();
    }


    @Override
    public Response soScheduleLoopback(String vnfName, String request, UriInfo uri) {
        String msoRequestId = env.getProperty("loopback.mso.requestId", "4ccbfb85-1d05-442e");
        String ruuid = UUID.randomUUID().toString();
        WfMsoRequestReferences rr = new WfMsoRequestReferences();
        rr.setInstanceId(ruuid);
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
                        + "\"requestType\" : \"createInstance\"," + "\"requestDetails\" : {},"
                        + "\"instanceReferences\" : {}," + "\"requestStatus\" : { " + "\"requestState\" : \"COMPLETE\","
                        + "\"statusMessage\" : \"Vf Module has been created successfully.\","
                        + "\"percentProgress\" : 100," + "\"finishTime\" : \"Crap so cmso uses current time\"}}}";
        return Response.ok().entity(response).build();
    }


}
