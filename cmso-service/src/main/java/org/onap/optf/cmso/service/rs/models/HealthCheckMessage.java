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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class HealthCheckMessage.
 */
@ApiModel
public class HealthCheckMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private static EELFLogger log = EELFManager.getInstance().getLogger(HealthCheckMessage.class);

    private Boolean healthy = false;
    private String buildInfo = "";
    private String currentTime = "";
    private String hostname = "";

    private List<HealthCheckComponent> components = new ArrayList<HealthCheckComponent>();

    /**
     * Gets the healthy.
     *
     * @return the healthy
     */
    public Boolean getHealthy() {
        return healthy;
    }

    /**
     * Sets the healthy.
     *
     * @param healthy the new healthy
     */
    public void setHealthy(Boolean healthy) {
        this.healthy = healthy;
    }

    /**
     * Gets the builds the info.
     *
     * @return the builds the info
     */
    public String getBuildInfo() {
        return buildInfo;
    }

    /**
     * Sets the builds the info.
     *
     * @param buildInfo the new builds the info
     */
    public void setBuildInfo(String buildInfo) {
        this.buildInfo = buildInfo;
    }

    /**
     * Gets the current time.
     *
     * @return the current time
     */
    public String getCurrentTime() {
        return currentTime;
    }

    /**
     * Sets the current time.
     *
     * @param currentTime the new current time
     */
    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * Gets the hostname.
     *
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Sets the hostname.
     *
     * @param hostname the new hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Gets the components.
     *
     * @return the components
     */
    public List<HealthCheckComponent> getComponents() {
        return components;
    }

    /**
     * Sets the components.
     *
     * @param components the new components
     */
    public void setComponents(List<HealthCheckComponent> components) {
        this.components = components;
    }

    /**
     * Adds the component.
     *
     * @param components the components
     */
    public void addComponent(HealthCheckComponent components) {
        this.components.add(components);
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
