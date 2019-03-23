/*******************************************************************************
 *
 * Copyright © 2019 AT&T Intellectual Property.
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
 ******************************************************************************/

package org.onap.optf.cmso.optimizer.model;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Optimizer Schedule Info", description = "Schedule Information returned from optimizer request.")
public class OptimizerScheduleInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(OptimizerScheduleInfo.class);

    @ApiModelProperty(value = "Lists of elements with start times.")
    private List<ScheduledElement> scheduledElements = new ArrayList<>();

    @ApiModelProperty(value = "Lists of elements that were not able to be scheduled.")
    private List<UnScheduledElement> unScheduledElements = new ArrayList<>();


    public List<ScheduledElement> getScheduledElements() {
        return scheduledElements;
    }


    public void setScheduledElements(List<ScheduledElement> scheduledElements) {
        this.scheduledElements = scheduledElements;
    }


    public List<UnScheduledElement> getUnScheduledElements() {
        return unScheduledElements;
    }


    public void setUnScheduledElements(List<UnScheduledElement> unScheduledElements) {
        this.unScheduledElements = unScheduledElements;
    }


    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.debug("Error in toString()", e);
        }
        return "";
    }
}
