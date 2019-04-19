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

package org.onap.optf.cmso.optimizer.clients.common.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.onap.optf.cmso.optimizer.service.rs.models.NameValue;

@ApiModel(value = "Element Critera", description = "Element criteria for retrieving topology.")
public class ElementCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Element id unique to the request.")
    private String elementId;

    @ApiModelProperty(value = "Implementation specific element data.")
    public List<NameValue> elementData = new ArrayList<>();

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public List<NameValue> getElementData() {
        return elementData;
    }

    public void setElementData(List<NameValue> elementData) {
        this.elementData = elementData;
    }

}
