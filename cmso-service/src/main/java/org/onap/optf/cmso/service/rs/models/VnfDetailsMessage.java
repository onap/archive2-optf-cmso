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

package org.onap.optf.cmso.service.rs.models;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;

/**
 * The Class VnfDetailsMessage.
 */
@ApiModel(value = "VNF Details", description = "Details and scheduling criteria for the VNFs to be changed.")
public class VnfDetailsMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(VnfDetailsMessage.class);

    @ApiModelProperty(value = "Name of the list of VNFs to be changed as a group")
    private String groupId;

    @ApiModelProperty(value = "Lists of the VNF names to be changed")
    private List<String> node;

    @ApiModelProperty(
            value = "Lists of desired change windows that the optimize"
                            + "r can select from. (Only 1 change window supported at this time)")
    private List<ChangeWindowMessage> changeWindow;

    /**
     * Gets the group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the group id.
     *
     * @param groupId the new group id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the node.
     *
     * @return the node
     */
    public List<String> getNode() {
        return node;
    }

    /**
     * Sets the node.
     *
     * @param node the new node
     */
    public void setNode(List<String> node) {
        this.node = node;
    }

    /**
     * Gets the change window.
     *
     * @return the change window
     */
    public List<ChangeWindowMessage> getChangeWindow() {
        return changeWindow;
    }

    /**
     * Sets the change window.
     *
     * @param changeWindow the new change window
     */
    public void setChangeWindow(List<ChangeWindowMessage> changeWindow) {
        this.changeWindow = changeWindow;
    }

    /**
     * To string.
     *
     * @return the string
     */
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
