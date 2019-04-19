/*
 *  ============LICENSE_START==============================================
 *  Copyright (c) 2019 AT&T Intellectual Property.
 *  =======================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain a
 *  copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * ============LICENSE_END=================================================
 */

package org.onap.optf.cmso.optimizer.clients.optimizer.models;

import com.google.common.base.CaseFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.onap.optf.cmso.optimizer.common.LogMessages;
import org.onap.optf.cmso.optimizer.observations.Observation;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

/**
 * The Class OptimizerResponseUtility.
 */
public class OptimizerResponseUtility extends PropertyUtils {

    /**
     * Parses the optimizer result.
     *
     * @param resultsFile the results file
     * @return the optimizer results
     */
    public OptimizerResults parseOptimizerResult(File resultsFile) {
        OptimizerResults results = null;
        try (InputStream input = new FileInputStream(resultsFile)) {
            Constructor constructor = new Constructor(OptimizerOutResults.class);
            constructor.setPropertyUtils(this);
            Yaml yaml = new Yaml(constructor);
            OptimizerOutResults optimizerOut = yaml.load(input);
            results = marshall(optimizerOut);
        } catch (Exception e) {
            Observation.report(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
        return results;
    }

    private OptimizerResults marshall(OptimizerOutResults optimizerOut) {
        OptimizerResults results = new OptimizerResults();
        results.setElapsedMillis(optimizerOut.getElapsedMillis());
        List<OptimizerSchedule> schedules = new ArrayList<>();
        results.setSchedules(schedules);
        for (OptimizerOutSchedule sch : optimizerOut.getResults()) {
            schedules.add(marshall(sch));
        }
        return results;
    }

    private OptimizerSchedule marshall(OptimizerOutSchedule sch) {
        OptimizerSchedule optimizerSchedule = new OptimizerSchedule();
        optimizerSchedule.setNumScheduled(sch.getNumScheduled());
        optimizerSchedule.setTotalCompletionTime(sch.getTotalCompletionTime());
        String[] rows = sch.getElementSlotLoader().split("\n");
        List<ElementSlot> slots = new ArrayList<>();
        optimizerSchedule.setElementSlotLoader(slots);
        for (String row : rows) {
            String[] cols = row.split(",");
            ElementSlot slot = new ElementSlot(cols);
            slots.add(slot);
        }
        return optimizerSchedule;
    }

    /**
     * Gets the property.
     *
     * @param type the type
     * @param name the name
     * @return the property
     */
    @Override
    public Property getProperty(Class<? extends Object> type, String name) {
        name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
        return super.getProperty(type, name);
    }

}
