/*
 * Copyright © 2017-2018 AT&T Intellectual Property.
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

package org.onap.optf.cmso.ticketmgt.bean;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.onap.optf.cmso.common.LogMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Component
public class BuildCreateRequest {

    //
    // Prototype of a ticket flow... Create/Get(check status)/Update/Close/Cancel
    // Values here a derived from the scheduling process
    // Other values derived from ticket templates...
    // This is for example purposes only ans every provider
    // will have unique requirements.
    // This assumes multiple VNFs can appear on a single ticket
    //
    public enum Variables {
        vnfList(168), requesterId(50), plannedStartDate(15), plannedEndDate(15), validationStartTime(
                0), backoutStartTime(0), assetList(0), validationStartTimeDisplay(0), backoutStartTimeDisplay(
                        0), plannedStartTimeDisplay(0), plannedEndTimeDisplay(0), vnfName(0), changeId(
                                15), actualStartDate(
                                        15), actualEndDate(15), status(0), closureCode(30), closingComments(1332),;
        private final int maxLength;

        private Variables(int max) {
            this.maxLength = max;
        }

        public int getMaxLength() {
            return maxLength;
        }
    }

    private static EELFLogger log = EELFManager.getInstance().getLogger(BuildCreateRequest.class);
    private static EELFLogger metrics = EELFManager.getInstance().getMetricsLogger();
    private static EELFLogger errors = EELFManager.getInstance().getErrorLogger();
    private static EELFLogger debug = EELFManager.getInstance().getDebugLogger();

    @Autowired
    Environment env;

    public JsonNode createChangeRecordRequest(Map<String, Object> variables, List<TmAsset> assetList,
            String workflowName) {
        JsonNode rawjson = getYaml("CreateChangeTicket");
        JsonNode json = substituteJson(rawjson, variables);
        return json;
    }

    public JsonNode createCloseCancelChangeRecord(Map<String, Object> variables) {
        JsonNode rawjson = getYaml("CloseCancelChangeRecord");
        JsonNode json = substituteJson(rawjson, variables);
        return json;
    }

    public JsonNode createCancelChangeRecord(Map<String, Object> variables) {
        JsonNode rawjson = getYaml("CancelChangeRecord");
        JsonNode json = substituteJson(rawjson, variables);
        return json;
    }

    public JsonNode createUpdateChangeRecord(Map<String, Object> variables) {
        JsonNode rawjson = getYaml("UpdateChangeRecord");
        JsonNode json = substituteJson(rawjson, variables);
        return json;
    }

    public JsonNode createGetChangeRecord(Map<String, Object> variables) {
        JsonNode rawjson = getYaml("GetChangeRecord");
        JsonNode json = substituteJson(rawjson, variables);
        return json;
    }

    private JsonNode substituteJson(JsonNode json, Map<String, Object> variables) {
        StrSubstitutor sub = new StrSubstitutor(variables);
        substitute(sub, json, null, null);
        return json;
    }

    private void substitute(StrSubstitutor sub, JsonNode json, JsonNode parent, String name) {
        switch (json.getNodeType()) {
            case STRING:
                TextNode tn = (TextNode) json;
                String value = tn.textValue();
                updateNode(sub, (ObjectNode) parent, name, value);
                break;
            case ARRAY:
                break;
            case BINARY:
                break;
            case BOOLEAN:
                break;
            case MISSING:
                break;
            case NULL:
                break;
            case NUMBER:
                break;
            case OBJECT:
                ObjectNode objectnode = (ObjectNode) json;
                Iterator<String> fieldnames = objectnode.fieldNames();
                while (fieldnames.hasNext()) {
                    String nextName = fieldnames.next();
                    JsonNode jn = objectnode.get(nextName);
                    substitute(sub, jn, json, nextName);
                }
                break;
            case POJO:
                break;
            default:
        }

    }

    private void updateNode(StrSubstitutor sub, ObjectNode parent, String name, String value) {
        value = sub.replace(value);
        if (isInteger(name)) {
            try {
                parent.put(name, Long.valueOf(value));
            } catch (Exception e) {
                errors.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
                parent.put(name, value);
            }
        } else {
            parent.put(name, value);
        }
    }

    private boolean isInteger(String name) {
        if (name.equals(Variables.plannedEndDate.toString()))
            return true;
        if (name.equals(Variables.plannedStartDate.toString()))
            return true;
        if (name.equals(Variables.actualStartDate.toString()))
            return true;
        if (name.equals(Variables.actualEndDate.toString()))
            return true;
        return false;
    }

    public JsonNode getYaml(String workflowName) {
        JsonNode json = null;
        // Get the YAML file for this workflow
        String yamlFile = env.getProperty("tm.template.folder") + File.separator + workflowName + ".yaml";
        // String yamlFile =
        // "C:\\cmso\\master\\etc\\config\\templates\\vtm\\questionnaires\\Build
        // Software Upgrade for vNFs.yaml";
        try {
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            File file = new File(yamlFile);
            json = om.readTree(file);
        } catch (Exception e) {
            debug.debug("Unexpected exception reading : (0} ", yamlFile, e);
        }
        return json;
    }

}
