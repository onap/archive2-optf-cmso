/*
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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;

@ApiModel(value = "Topology Constraint ELements",
                description = "Constraining Element Information returned from TopologyRequuest.")
public class ConstraintElements implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum AvailabilityMatrixScope {
        NONE, GLOBAL, ELEMENT,
    }

    @ApiModelProperty(value = "Element identifier")
    private String elementId;

    @ApiModelProperty(value = "Type of constraint.")
    private String constraintType;

    @ApiModelProperty(
                    value = "If more than one instance of constraintType,"
                                    + " minimum number of available instances required."
                                    + " Useful for identifying availableBackup elements, service paths.")
    private Integer constraintTypeMinimum = 1;

    @ApiModelProperty(value = "Availability matrix name. Availability matrix will not be passed to optimizer engine."
                    + " Generally useful for global concurrency type constraints.")
    private String optimizerAvailabilityMatrixName;

    @ApiModelProperty(value = "Availability matrix scope global  or scoped per elementId.")
    private AvailabilityMatrixScope availabilityMatrixScope = AvailabilityMatrixScope.NONE;

    @ApiModelProperty(value = "Availability matrix is aggregated into element availability marrix.")
    private boolean elementAvailabilityAggreagation = true;

    @ApiModelProperty(value = "Elements ")
    private List<String> elements;

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }

    public Integer getConstraintTypeMinimum() {
        return constraintTypeMinimum;
    }

    public void setConstraintTypeMinimum(Integer constraintTypeMinimum) {
        this.constraintTypeMinimum = constraintTypeMinimum;
    }

    public String getOptimizerAvailabilityMatrixName() {
        return optimizerAvailabilityMatrixName;
    }

    public void setOptimizerAvailabilityMatrixName(String optimizerAvailabilityMatrixName) {
        this.optimizerAvailabilityMatrixName = optimizerAvailabilityMatrixName;
    }

    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }

    public AvailabilityMatrixScope getAvailabilityMatrixScope() {
        return availabilityMatrixScope;
    }

    public void setAvailabilityMatrixScope(AvailabilityMatrixScope availabilityMatrixScope) {
        this.availabilityMatrixScope = availabilityMatrixScope;
    }

    public boolean isElementAvailabilityAggreagation() {
        return elementAvailabilityAggreagation;
    }

    public void setElementAvailabilityAggreagation(boolean elementAvailabilityAggreagation) {
        this.elementAvailabilityAggreagation = elementAvailabilityAggreagation;
    }



}
