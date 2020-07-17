/*
 * Copyright © 2019 AT&T Intellectual Property.
 * Modified 2020 Nokia.
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
package org.onap.logger;

import com.att.eelf.configuration.EELFLogger;
import org.apache.logging.log4j.spi.StandardLevel;
import org.onap.observations.ObservationInterface;

public class Logger {

    private final EELFLogger log;
    private final EELFLogger metrics;
    private final EELFLogger audit;
    private final EELFLogger errors;
    private final EELFLogger debug;

    public Logger(EELFLogger log, EELFLogger metrics, EELFLogger audit, EELFLogger errors, EELFLogger debug) {
        this.log = log;
        this.metrics = metrics;
        this.audit = audit;
        this.errors = errors;
        this.debug = debug;
    }

    public void report(ObservationInterface obs, Exception except, String... arguments) {

        if (obs.getAudit()) {
            audit.info(obs, except, arguments);
        }
        if (obs.getMetric()) {
            metrics.info(obs, except, arguments);
        }

        final StandardLevel standardLevel = obs.getLevel().getStandardLevel();

        switch (standardLevel) {
            case WARN:
                errors.warn(obs, arguments);
                debug.debug(obs, except, arguments);
                break;
            case INFO:
                log.info(obs, except, arguments);
                debug.debug(obs, except, arguments);
                break;
            case ERROR:
                errors.error(obs, arguments);
                debug.debug(obs, except, arguments);
                break;
            case TRACE:
                debug.trace(obs, except, arguments);
                break;
            case DEBUG:
                debug.debug(obs, except, arguments);
                break;
            default:
                log.info(obs, except, arguments);
        }

    }

    public void report(ObservationInterface obs, String... arguments) {

        if (obs.getAudit()) {
            audit.info(obs, arguments);
        }
        if (obs.getMetric()) {
            metrics.info(obs, arguments);
        }

        final StandardLevel standardLevel = obs.getLevel().getStandardLevel();

        switch (standardLevel) {
            case WARN:
                errors.warn(obs, arguments);
                debug.debug(obs, arguments);
                break;
            case INFO:
                log.info(obs, arguments);
                debug.debug(obs, arguments);
                break;
            case ERROR:
                errors.error(obs, arguments);
                debug.debug(obs, arguments);
                break;
            case TRACE:
                debug.debug(obs, arguments);
                break;
            case DEBUG:
                debug.debug(obs, arguments);
                break;
            default:
                log.info(obs, arguments);
        }
    }
}
