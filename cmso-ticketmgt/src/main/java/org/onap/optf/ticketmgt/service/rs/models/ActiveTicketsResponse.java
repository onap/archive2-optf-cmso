/*
 * Copyright © 2017-2019 AT&T Intellectual Property.
 * Modifications Copyright © 2018 IBM.
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

package org.onap.optf.ticketmgt.service.rs.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Ticket Management Response", description = "Response to active ticket query for the requested elements.")
public class ActiveTicketsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(ActiveTicketsResponse.class);

    public enum ActiveTicketResponseStatus
    {
    	IN_PROGESS,
    	COMPLETED,
    }
    @ApiModelProperty(value = "Unique Id of the request")
    private String requestId;

    @ApiModelProperty(value = "List of TicketData for the requested elements. A single ticket may apply to more than 1 passed elementId.")
    private List<TicketData> elements = new ArrayList<>();
    
    @ApiModelProperty(value = "Status of ticket request. IN_PROGRESS will indicate asynchronous processing is required.")
    private ActiveTicketResponseStatus status;
 
    @ApiModelProperty(value = "If request is asynchronous (IN_PROGRESS), suggested interval to the next poll.")
    private Integer pollingSeconds;
    
    public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public List<TicketData> getElements() {
		return elements;
	}

	public void setElements(List<TicketData> elements) {
		this.elements = elements;
	}

	public ActiveTicketResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ActiveTicketResponseStatus status) {
		this.status = status;
	}

	public Integer getPollingSeconds() {
		return pollingSeconds;
	}

	public void setPollingSeconds(Integer pollingSeconds) {
		this.pollingSeconds = pollingSeconds;
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
