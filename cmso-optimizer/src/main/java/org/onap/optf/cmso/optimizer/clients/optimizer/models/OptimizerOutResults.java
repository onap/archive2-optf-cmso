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

import java.util.List;

/*

 */
public class OptimizerOutResults {
    private Long elapsedMillis;
    private List<OptimizerOutSchedule> results;

    public Long getElapsedMillis() {
        return elapsedMillis;
    }

    public void setElapsedMillis(Long elapsed_millis) {
        this.elapsedMillis = elapsed_millis;
    }

    public List<OptimizerOutSchedule> getResults() {
        return results;
    }

    public void setResults(List<OptimizerOutSchedule> schedules) {
        this.results = schedules;
    }

}
