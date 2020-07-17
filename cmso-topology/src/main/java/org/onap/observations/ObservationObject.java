/*
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
 */

package org.onap.observations;

import com.att.eelf.i18n.EELFResolvableErrorEnum;
import com.att.eelf.i18n.EELFResourceManager;
import org.apache.logging.log4j.Level;
import javax.ws.rs.core.Response.Status;


/**
 * The Class ObservationObject.
 */
public class ObservationObject implements ObservationInterface {

    // *************************************************************************************************
    // Interface class that matches the ObservationInteface pattern
    // This will be used in case we decide to provide external overrides and we need to instantiate
    // For now, we'll just use the Enum itself.
    //
    //
    private Enum<?> value = null;

    private Level level = null;
    private String message = null;
    private Status status = null;
    private String domain = null;
    private Boolean metric = false;
    private Boolean audit = false;

    /**
     * Instantiates a new observation object.
     *
     * @param obs the o
     */
    public ObservationObject(ObservationInterface obs) {
        this.value   = obs.getValue();
        this.level   = obs.getLevel();
        this.message = obs.getMessage();
        this.status  = obs.getStatus();
        this.domain  = obs.getDomain();
        this.metric  = obs.getMetric();
        this.audit   = obs.getAudit();

    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    @Override
    public Enum<?> getValue() {
        return value;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * Gets the domain.
     *
     * @return the domain
     */
    @Override
    public String getDomain() {
        return domain;
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    @Override
    public Level getLevel() {
        return level;
    }

    /**
     * Name.
     *
     * @return the string
     */
    @Override
    public String name() {
        return value.name();
    }

    /**
     * Gets the audit.
     *
     * @return the audit
     */
    @Override
    public Boolean getAudit() {
        return audit;
    }

    /**
     * Gets the metric.
     *
     * @return the metric
     */
    @Override
    public Boolean getMetric() {
        return metric;
    }

    /**
     * Gets the message.
     *
     * @param arguments the arguments
     * @return the message
     */
    public String getMessagef(String... arguments) {
        return EELFResourceManager.format((EELFResolvableErrorEnum) value, arguments);
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(Enum<?> value) {
        this.value = value;
    }

    /**
     * Sets the level.
     *
     * @param level the new level
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(Status status) {
        this.status = status;
    }


}
