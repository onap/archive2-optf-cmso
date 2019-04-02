/*
 *  ============LICENSE_START==============================================
 *  Copyright (c) 2019 AT&T Intellectual Property.
 *  =======================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain a
 *  copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * ============LICENSE_END=================================================
 */

package org.onap.optf.cmso.optimizer.clients.optimizer;

import com.google.ical.compat.jodatime.DateTimeIterator;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.onap.optf.cmso.optimizer.availability.timewindows.RecurringWindows;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.ElementSlot;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerSchedule;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyElementInfo;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse;
import org.onap.optf.cmso.optimizer.service.rs.models.ChangeWindow;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerRequest;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerScheduleInfo;
import org.onap.optf.cmso.optimizer.service.rs.models.ScheduledElement;
import org.onap.optf.cmso.optimizer.service.rs.models.ScheduledElement.ScheduleType;
import org.onap.optf.cmso.optimizer.service.rs.models.UnScheduledElement;
import org.onap.optf.cmso.optimizer.service.rs.models.UnScheduledElement.NotScheduledReason;

// This class ensures that the node indices nodes and the time slots are the
// same when processing the optimizer engine response as when initiating.
public class ElementWindowMapping {

    protected OptimizerRequest optimizerRequest;
    protected TopologyResponse topologyResponse;

    protected Map<String, TopologyElementInfo> nodeInfo = new TreeMap<>();
    private List<TopologyElementInfo> nodeArray = null;

    public ElementWindowMapping(OptimizerRequest optimizerRequest, TopologyResponse topologyResponse)
                    throws ParseException {
        this.optimizerRequest = optimizerRequest;
        this.topologyResponse = topologyResponse;
        initialize();

    }

    private void initialize() throws ParseException {
        List<TopologyElementInfo> elements = topologyResponse.getElements();
        for (TopologyElementInfo info : elements) {
            nodeInfo.put(info.getElementId(), info);
        }
    }

    protected DateTimeIterator getRecurringIterator() throws ParseException {
        // Only support 1 change window for now
        ChangeWindow window = optimizerRequest.getChangeWindows().get(0);
        Long duration = new Long(optimizerRequest.getNormalDuration());
        if (optimizerRequest.getAdditionalDuration() != null) {
            duration += optimizerRequest.getAdditionalDuration();
        }
        DateTimeIterator recur = RecurringWindows.getRecurringListForChangeWindow(window, duration);
        return recur;
    }

    public void initializeForProcessResult()
    {
       // we need nodeInfo to be an array to speed up the result processing.
       // but we need it sorted by elementId as when we created it....
       nodeArray = nodeInfo.values().stream().collect(Collectors.toList());
       nodeInfo.clear();

    }
    public OptimizerScheduleInfo processResult(OptimizerSchedule result) throws ParseException {
        // When considering the memory vs performance
        // 5 minute duration for a month long change window is 8928 slots
        // The assumption is that there were be fewer allocated slots
        // than potential slots.
        List<ElementSlot> elements = result.getElementSlotLoader();
        Map<Integer, List<ElementSlot>> mapSlotToElement = elements.stream().
                        collect(Collectors.groupingBy(ElementSlot::getSlot));
        DateTimeIterator iter = getRecurringIterator();
        // TODO - supporting only 1 change window at the moment.....
        Long endWindow = optimizerRequest.getChangeWindows().get(0).getEndTime().getTime();
        Integer slotIndex = 1;
        while (iter.hasNext()) {
            DateTime dateTime = iter.next();
            if (dateTime.isAfter(endWindow))
                break;
            List<ElementSlot> list = mapSlotToElement.get(slotIndex);
            if (list != null) {
                list.stream().forEach(x -> x.setTime(dateTime.getMillis()));
            }
            slotIndex++;
        }
        //
        // All assigned ElementSlots now have corresponding UTC time
        //
        OptimizerScheduleInfo info = new OptimizerScheduleInfo();
        for (ElementSlot slot : elements)
        {
            updateInfo(slot, info);
        }
        return info;
    }

    private void updateInfo(ElementSlot slot, OptimizerScheduleInfo info)
    {
        TopologyElementInfo element = nodeArray.get(slot.getElementIndex()-1);
        if (slot.getSlot() > 0)
        {
            ScheduledElement scheduled = new ScheduledElement();
            Integer durationInSeconds = optimizerRequest.getNormalDuration();
            if (optimizerRequest.getAdditionalDuration() != null) {
                durationInSeconds += optimizerRequest.getAdditionalDuration();
            }
            scheduled.setDurationSeconds(durationInSeconds.longValue());
            scheduled.setElementId(element.getElementId());
            scheduled.setStartTime(new Date(slot.getTime()));
            scheduled.setEndTime(new Date(slot.getTime() + (durationInSeconds*1000)));
            scheduled.setScheduleType(ScheduleType.INDIVIDUAL);
            info.getScheduledElements().add(scheduled);
        }
        else
        {
            UnScheduledElement unscheduled = new UnScheduledElement();
            unscheduled.setElementId(element.getElementId());
            unscheduled.setGroupId("unknown");
            unscheduled.getNotScheduledReaons().add(NotScheduledReason.Other);
            unscheduled.getNotScheduledMessages().add("Unknown");
            info.getUnScheduledElements().add(unscheduled);
        }
    }


}
