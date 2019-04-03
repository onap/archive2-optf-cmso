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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.joda.time.DateTime;
import org.onap.optf.cmso.optimizer.availability.policies.model.TimeLimitAndVerticalTopology;
import org.onap.optf.cmso.optimizer.availability.timewindows.RecurringWindows;
import org.onap.optf.cmso.optimizer.clients.optimizer.models.OptimizerParameters;
import org.onap.optf.cmso.optimizer.clients.ticketmgt.models.ActiveTicketsResponse;
import org.onap.optf.cmso.optimizer.clients.ticketmgt.models.TicketData;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyElementInfo;
import org.onap.optf.cmso.optimizer.clients.topology.models.TopologyResponse;
import org.onap.optf.cmso.optimizer.service.rs.models.ChangeWindow;
import org.onap.optf.cmso.optimizer.service.rs.models.OptimizerRequest;

public class ElementAvailability extends ElementWindowMapping{

    private List<TimeLimitAndVerticalTopology> policies;
    private ActiveTicketsResponse ticketResponse;

    private OptimizerParameters parameters = null;

    private List<List<ChangeWindow>> globalRelativeAvailability = new ArrayList<>();

    private Map<String, List<TicketData>> nodeUnAvailability = new TreeMap<>();

    public ElementAvailability(List<TimeLimitAndVerticalTopology> policies, OptimizerRequest optimizerRequest,
                    TopologyResponse topologyResponse, ActiveTicketsResponse ticketResponse) throws ParseException {
        super(optimizerRequest, topologyResponse);
        this.policies         = policies;
        this.ticketResponse   = ticketResponse;
    }

    public void populate(OptimizerParameters parameters) throws ParseException {
        this.parameters = parameters;
        for (ChangeWindow changeWindow : optimizerRequest.getChangeWindows()) {
            if  (policies.size() > 0) {
                globalRelativeAvailability
                                .add(RecurringWindows.getAvailabilityWindowsForPolicies(policies, changeWindow));
            } else {
                List<ChangeWindow> wholeWindow = new ArrayList<>();
                wholeWindow.add(changeWindow);
                globalRelativeAvailability.add(wholeWindow);
            }
        }
        for (String id : nodeInfo.keySet()) {
            calculateNodeAvailability(nodeInfo.get(id));
        }
        setNoConflicts();
        parameters.setMaxTime(new Long(parameters.getNoConflict().get(0).size()));
        parameters.setNumElements(new Long(parameters.getNoConflict().size()));

        // for now we have 1 loader with unlimited capacity
        parameters.setNumLoaders(1L);
        Long loaderCapacity = parameters.getNumElements();
        List<Long> capacity =  new ArrayList<>();
        for (Long slot =0L ; slot < parameters.getMaxTime() ; slot++) {
            capacity.add(loaderCapacity);
        }
        parameters.getLoaderCapacity().add(capacity);

        // For now every slot has the same concurrency limit
        capacity =  new ArrayList<>();
        Long limit = new Long(optimizerRequest.getConcurrencyLimit());
        if (limit > parameters.getNumElements()) {
            limit = parameters.getNumElements();
        }

        for (Long slot =0L ; slot < parameters.getMaxTime() ; slot++) {
            capacity.add(limit);
        }
        parameters.setElementSlotCapacity(capacity);

    }

    private void setNoConflicts() throws ParseException {
        // Only support 1 change window for now
        ChangeWindow window = optimizerRequest.getChangeWindows().get(0);
        Long duration = new Long(optimizerRequest.getNormalDuration());
        if (optimizerRequest.getAdditionalDuration() != null) {
            duration += optimizerRequest.getAdditionalDuration();
        }
        for (String elementId : nodeInfo.keySet()) {

            TopologyElementInfo info = nodeInfo.get(elementId);
            // Library for lat/lon to timzezone is MIT license.
            // We must provided
            String timeZone = "GMT";
            if (info.getElementLocation() != null && info.getElementLocation().getTimezone() != null
                            && !info.getElementLocation().getTimezone().equals("")) {
                timeZone = info.getElementLocation().getTimezone();
            }
            DateTimeIterator recur = getRecurringIterator();
            List<Boolean> element = new ArrayList<>();
            // calculate number time slots
            long numberOfTimeSlots = calculateNumberOfSlotsInWindow(window, duration);
            while (recur.hasNext() && element.size() < numberOfTimeSlots) {
                DateTime next = recur.next();
                if (next.isAfter(window.getEndTime().getTime())) {
                    break;
                }
                ChangeWindow slot = new ChangeWindow();
                slot.setStartTime(next.toDate());
                slot.setEndTime(next.plus(duration).toDate());
                if (slotIsAvailable(slot, timeZone, nodeUnAvailability.get(elementId))) {
                    element.add(true);
                } else {
                    element.add(false);
                }
            }
            parameters.getNoConflict().add(element);
        }

    }

    private long calculateNumberOfSlotsInWindow(ChangeWindow window, Long duration)
    {
        long windowSize = window.getEndTime().getTime() - window.getStartTime().getTime();
        long numberOfSlots = windowSize /duration;
        return numberOfSlots;
    }

    private boolean slotIsAvailable(ChangeWindow slot, String timeZone, List<TicketData> tickets) {
        if (isGloballyAvailable(slot, timeZone) && isNotRestricted(slot, tickets)) {
            return true;
        }
        return false;
    }

    private boolean isNotRestricted(ChangeWindow slot, List<TicketData> tickets) {
        if (tickets != null) {
            for (TicketData ticket : tickets) {
                ChangeWindow window = new ChangeWindow();
                window.setStartTime(ticket.getStartTime());
                window.setEndTime(ticket.getEndTime());
                if (slot.overlaps(window)) {
                    return false;
                }
            }
        }
        return true;
    }

    //
    // Globally availability are generally maintenance window definitions
    // which are UTC times that are treated as relative to the local time zone.
    // The slot is an absolute UTCT time as well.
    // When we test to see if the slot is 'globally' available we must adjust it
    // to the local time of the element to see it it matches the relative
    // Consider
    // slot UTC time is 06:00-07:00
    // global availability (maintenance window) 00:00-06:00
    // the slot for an element in US/Eastern
    // time would be converted to 01:00-02:00
    // and would fit in the local maintenance window
    //
    private boolean isGloballyAvailable(ChangeWindow slot, String timeZone) {
        boolean available = false;
        for (ChangeWindow global : globalRelativeAvailability.get(0)) {
            if (global.containsInTimeZone(slot, timeZone)) {
                available = true;
            }
        }
        return available;
    }

    private void calculateNodeAvailability(TopologyElementInfo info) {
        Set<String> requiredElements = new HashSet<>();
        requiredElements.add(info.getElementId());
        if (info.getRequiredElements() != null) {
            requiredElements.addAll(info.getRequiredElements());
        }
        if (ticketResponse.getElements() != null) {
            List<TicketData> tickets = ticketResponse.getElements();
            for (TicketData data : tickets) {
                for (String id : data.getElementIds()) {
                    if (requiredElements.contains(id)) {
                        updateNodeAvailability(id, data);
                        break;
                    }
                }
            }
        }
    }

    private void updateNodeAvailability(String elementId, TicketData data) {
        List<TicketData> list = nodeUnAvailability.get(elementId);
        if (list == null) {
            list = new ArrayList<>();
            nodeUnAvailability.put(elementId, list);
        }
        list.add(data);
    }

}
