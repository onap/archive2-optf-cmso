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
results:
 result:
   num_scheduled: 0
   total_completion_time: 0
   element_slot_loader: |
     1,0,1
     2,0,1
     3,0,1
     4,0,1
     5,0,1
 result:
   num_scheduled: 1
   total_completion_time: 2
   element_slot_loader: |
     1,0,1
     2,0,1
     3,2,1
     4,0,1
     5,0,1
 result:
   num_scheduled: 4
   total_completion_time: 8
   element_slot_loader: |
     1,2,1
     2,1,1
     3,2,1
     4,0,1
     5,3,1
 elapsed_millis: 3400

 */
public class OptimizerOutYaml {

    private OptimizerOutResults results;

    public OptimizerOutResults getResults() {
        return results;
    }

    public void setResults(OptimizerOutResults results) {
        this.results = results;
    }

}
