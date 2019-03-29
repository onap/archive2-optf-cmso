/*
 * Copyright © 2019 AT&T Intellectual Property.
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
package org.onap.observations;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.apache.log4j.Level;


public class Observation {
    private static EELFLogger log = EELFManager.getInstance().getLogger(Observation.class);
    private static EELFLogger metrics = EELFManager.getInstance().getMetricsLogger();
    private static EELFLogger audit = EELFManager.getInstance().getAuditLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    // *************************************************************************************************
    public static void report(ObservationInterface o, Exception e, String... arguments) {
        Mdc.setCaller(4);
        Mdc.setObservation(o);
        if (o.getAudit()) {
            audit.info(o, e, arguments);
        }
        if (o.getMetric()) {
            metrics.info(o, e, arguments);
        }
        Level l = o.getLevel();
        switch (l.toInt()) {
            case Level.WARN_INT:
                errors.warn(o, arguments);
                debug.debug(o, e, arguments);
                break;
            case Level.INFO_INT:
                log.info(o, e, arguments);
                debug.debug(o, e, arguments);
                break;
            case Level.ERROR_INT:
                errors.error(o, arguments);
                debug.debug(o, e, arguments);
                break;
            case Level.TRACE_INT:
                debug.trace(o, e, arguments);
                break;
            case Level.DEBUG_INT:
                debug.debug(o, e, arguments);
                break;
            default:
                log.info(o, e, arguments);
        }
        Mdc.clearCaller();
    }

    public static void report(ObservationInterface o, String... arguments) {
        Mdc.setCaller(4);
        Mdc.setObservation(o);
        if (o.getAudit()) {
            audit.info(o, arguments);
        }
        if (o.getMetric()) {
            metrics.info(o, arguments);
        }
        Level l = o.getLevel();
        switch (l.toInt()) {
            case Level.WARN_INT:
                errors.warn(o, arguments);
                debug.debug(o, arguments);
                break;
            case Level.INFO_INT:
                log.info(o, arguments);
                debug.debug(o, arguments);
                break;
            case Level.ERROR_INT:
                errors.error(o, arguments);
                debug.debug(o, arguments);
                break;
            case Level.TRACE_INT:
                debug.debug(o, arguments);
                break;
            case Level.DEBUG_INT:
                debug.debug(o, arguments);
                break;
            default:
                log.info(o, arguments);
        }
        Mdc.clearCaller();
    }

}
