/*******************************************************************************
 *
 *  Copyright © 2019 AT&T Intellectual Property.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  Unless otherwise specified, all documentation contained herein is licensed
 *  under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 *  you may not use this documentation except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          https://creativecommons.org/licenses/by/4.0/
 *
 *  Unless required by applicable law or agreed to in writing, documentation
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.onap.optf.cmso.optimizer.model;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Unscheduled Element", description = "Scheduled element returned by the optimizer.")
public class UnScheduledElement implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(UnScheduledElement.class);

    public enum NotScheduledReason
    {
    	ConcurrencyConstraint,
    	AvailabilityConstraint,
    	Other,
    }

    @ApiModelProperty(value = "Element identifier")
    private String elementId;

    @ApiModelProperty(value = "Group identifier")
    private String groupId;

    @ApiModelProperty(value = "List of reasons not able to schedule this element.")
    private List<NotScheduledReason> notScheduledReaons = new ArrayList<>();

    @ApiModelProperty(value = "List of messages not able to schedule this element.")
    private List<String> notScheduledMessages = new ArrayList<>();

    public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public List<NotScheduledReason> getNotScheduledReaons() {
		return notScheduledReaons;
	}

	public void setNotScheduledReaons(List<NotScheduledReason> notScheduledReaons) {
		this.notScheduledReaons = notScheduledReaons;
	}

	public List<String> getNotScheduledMessages() {
		return notScheduledMessages;
	}

	public void setNotScheduledMessages(List<String> notScheduledMessages) {
		this.notScheduledMessages = notScheduledMessages;
	}

	public String getGroupId() {
  return groupId;}

  public void setGroupId(String groupId) {
  this.groupId = groupId;}

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
