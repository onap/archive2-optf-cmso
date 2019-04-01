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

package org.onap.optf.cmso.optimizer.clients.optimizer.models;

/*
num_scheduled: 0
total_completion_time: 0
element_slot_loader: |
  1,0,1
  2,0,1
  3,0,1
  4,0,1
  5,0,1
 */
public class OptimizerOutSchedule {
    private Long numScheduled;
    private Long totalCompletionTime;
    private String elementSlotLoader;

    public Long getNumScheduled() {
        return numScheduled;
    }

    public void setNumScheduled(Long numScheduled) {
        this.numScheduled = numScheduled;
    }

    public Long getTotalCompletionTime() {
        return totalCompletionTime;
    }

    public void setTotalCompletionTime(Long totalCompletionTime) {
        this.totalCompletionTime = totalCompletionTime;
    }

    public String getElementSlotLoader() {
        return elementSlotLoader;
    }

    public void setElementSlotLoader(String elementSlotLoader) {
        this.elementSlotLoader = elementSlotLoader;
    }
}

