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

package org.onap.optf.cmso.topology.service.rs.models;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Topology Element", description = "Element Information returned from TopologyRequuest.")
public class ElementInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(ElementInfo.class);

    @ApiModelProperty(value = "Element identifier")
    private String elementId;

    @ApiModelProperty(value = "Location information for the element.")
    private ElementLocation elementLocation;

    @ApiModelProperty(value = "List of related elements required to be available to execute the chenge.")
    private List<String> requiredElements;

    @ApiModelProperty(value = "Lists of related elements that must be "
                    + " available to avoid network outage while executing the change."
                    + " Each set constraint elements")
    private List<ConstraintElements> constraintElements = new ArrayList<>();

    @ApiModelProperty(value = "Implementation specific element data.")
    public List<NameValue> elementData = new ArrayList<>();

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public ElementLocation getElementLocation() {
        return elementLocation;
    }

    public void setElementLocation(ElementLocation elementLocation) {
        this.elementLocation = elementLocation;
    }

    public List<String> getRequiredElements() {
        return requiredElements;
    }

    public void setRequiredElements(List<String> requiredElements) {
        this.requiredElements = requiredElements;
    }

    public List<NameValue> getElementData() {
        return elementData;
    }

    public void setElementData(List<NameValue> elementData) {
        this.elementData = elementData;
    }

    public List<ConstraintElements> getConstraintElements() {
        return constraintElements;
    }

    public void setConstraintElements(List<ConstraintElements> constraintElements) {
        this.constraintElements = constraintElements;
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
