/*
 * Copyright � 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright � 2018 IBM.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         https://creativecommons.org/licenses/by/4.0/
 * 
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.onap.optf.cmso.optimizer.service.rs.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Optimizer Request", description = "Request to provide an \"conflict free\" schedule for passed elements.")
public class OptimizerRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(OptimizerRequest.class);

    @ApiModelProperty(value = "Unique Id of the request")
    private String requestId;

    @ApiModelProperty(
            value = "Implementation specific name value pairs.")
    private List<NameValue> commonData;

    @ApiModelProperty(
            value = "Lists of desired change windows to schedule the elements.")
    private List<ChangeWindow> changeWindows = new ArrayList<>();
    
    @ApiModelProperty(value = "List of the elements to schedule.")
    private List<ElementInfo> elements = new ArrayList<>();

    @ApiModelProperty(value = "List of the policies to control optimization.")
    private List<PolicyInfo> policies = new ArrayList<>();

    public String getRequestId() {
		return requestId;
	}


	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}


	public List<PolicyInfo> getPolicies() {
		return policies;
	}


	public void setPolicies(List<PolicyInfo> policies) {
		this.policies = policies;
	}


	public List<NameValue> getCommonData() {
		return commonData;
	}


	public void setCommonData(List<NameValue> commonData) {
		this.commonData = commonData;
	}


	public List<ElementInfo> getElements() {
		return elements;
	}


	public void setElements(List<ElementInfo> elements) {
		this.elements = elements;
	}


	public List<ChangeWindow> getChangeWindows() {
		return changeWindows;
	}


	public void setChangeWindows(List<ChangeWindow> changeWindows) {
		this.changeWindows = changeWindows;
	}


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
