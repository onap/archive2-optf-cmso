/*
 * Copyright (c) 2019 AT&T Intellectual Property.
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

package org.onap.optf.ticketmgt.aaf;

import java.util.HashSet;
import java.util.Set;


public class AafPerm {
    private String type;
    private String instance;
    private String action;
    private Set<String> actions = new HashSet<>();

    public String getAction() {
        return action;
    }

    /**
     * Initialize the actions.
     *
     * @param action action list
     */
    public void setAction(String action) {
        this.action = action;
        String[] list = action.split(",");
        for (String a : list) {
            actions.add(a);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public Set<String> getActions() {
        return actions;
    }

    public void setActions(Set<String> actions) {
        this.actions = actions;
    }

    /**
     * Are permissions ok.
     *
     * @param userPerm user permissions
     * @return true = permissions ok
     */
    public boolean ok(AafPerm userPerm) {
        if (type.equals(userPerm.getType())) {
            if (userPerm.getInstance().equals("*") || instance.equals("*") || userPerm.getInstance().equals(instance)) {
                for (String userAction : userPerm.getActions()) {
                    if (userAction.equals("*") || actions.contains("*") || actions.contains(userAction)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
