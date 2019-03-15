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

@ApiModel(value = "Optimizer Response", description = "Response to optimizer request for the requested elements.")
public class OptimizerResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(OptimizerResponse.class);

    public enum OptimizeScheduleStatus
    {
    	CREATED,
    	PENDING_TOPOLOGY,
    	PENDING_TICKETS,
    	PENDING_OPTIMIZER,
    	COMPLETED,
    	FAILED, 
    	DELETED,
    }
    
    @ApiModelProperty(value = "Unique Id of the request")
    private String requestId;

    @ApiModelProperty(value = "Status of the optimization")
    private OptimizeScheduleStatus status;

    @ApiModelProperty(value = "Message for failed optimization")
    private String errorMessage;


    @ApiModelProperty(value = "List of schedules returned by the optimizer.")
    private List<OptimizerScheduleInfo> schedules = new ArrayList<>();
    
    public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}


	public List<OptimizerScheduleInfo> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<OptimizerScheduleInfo> schedules) {
		this.schedules = schedules;
	}

	public OptimizeScheduleStatus getStatus() {
		return status;
	}

	public void setStatus(OptimizeScheduleStatus status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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
