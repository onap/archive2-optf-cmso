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

package org.onap.optf.cmso.topology.service.rs.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Topology Response", description = "Response to topology query for the requested elements.")
public class TopologyResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(TopologyResponse.class);

    public enum TopologyRequestStatus
    {
    	IN_PROGRESS,
    	COMPLETED,
    }
    
    @ApiModelProperty(value = "Unique Id of the request")
    private String requestId;

    @ApiModelProperty(value = "List of elements for for which topology has been requested.")
    private List<ElementInfo> elements = new ArrayList<>();

    @ApiModelProperty(value = "List of referenced elements representing the topology that has been requested.")
    private List<ReferencedElementInfo> referencedElements = new ArrayList<>();

    @ApiModelProperty(value = "Status of asynchronous request. COMPLETED is returned on initial synchonous request. "
    		+ "If IN_PROGRESS is returned, the optimizer will enter asynchronous polling mode.")
    private TopologyRequestStatus status;

    @ApiModelProperty(value = "If request is asynchronous (IN_PROGRESS), suggested interval to the next poll.")
    private Integer pollingSeconds;
    
    public TopologyRequestStatus getStatus() {
		return status;
	}

	public void setStatus(TopologyRequestStatus status) {
		this.status = status;
	}

	public Integer getPollingSeconds() {
		return pollingSeconds;
	}

	public void setPollingSeconds(Integer pollingSeconds) {
		this.pollingSeconds = pollingSeconds;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public List<ElementInfo> getElements() {
		return elements;
	}

	public void setElements(List<ElementInfo> elements) {
		this.elements = elements;
	}

	public List<ReferencedElementInfo> getReferencedElements() {
		return referencedElements;
	}

	public void setReferencedElements(List<ReferencedElementInfo> referencedElements) {
		this.referencedElements = referencedElements;
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
