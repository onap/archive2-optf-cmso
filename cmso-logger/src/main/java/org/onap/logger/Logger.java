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

    /**
     * Report.
     *
     * @param obs the o
     * @param arguments the arguments
     */
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
