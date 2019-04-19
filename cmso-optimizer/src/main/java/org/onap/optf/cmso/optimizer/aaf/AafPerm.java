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
 ******************************************************************************/

package org.onap.optf.cmso.optimizer.aaf;

import java.util.HashSet;
import java.util.Set;
import org.onap.aaf.cadi.aaf.AAFPermission;


/**
 * The Class AafPerm.
 */
public class AafPerm {
    private String type;
    private String instance;
    private String action;
    private Set<String> actions = new HashSet<>();

    /**
     * Gets the action.
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action.
     *
     * @param action the new action
     */
    public void setAction(String action) {
        this.action = action;
        String[] list = action.split(",");
        for (String a : list) {
            actions.add(a);
        }
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the single instance of AafPerm.
     *
     * @return single instance of AafPerm
     */
    public String getInstance() {
        return instance;
    }

    /**
     * Sets the instance.
     *
     * @param instance the new instance
     */
    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * Gets the actions.
     *
     * @return the actions
     */
    public Set<String> getActions() {
        return actions;
    }

    /**
     * Sets the actions.
     *
     * @param actions the new actions
     */
    public void setActions(Set<String> actions) {
        this.actions = actions;
    }

    /**
     * Matches.
     *
     * @param userPerm the user perm
     * @return true, if successful
     */
    public boolean matches(AAFPermission userPerm) {
        if (type.equals(userPerm.getType())) {
            if (userPerm.getInstance().equals("*") || instance.equals("*") || userPerm.getInstance().equals(instance)) {
                for (String userAction : userPerm.getAction().split(",")) {
                    if (userAction.equals("*") || actions.contains("*") || actions.contains(userAction)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
