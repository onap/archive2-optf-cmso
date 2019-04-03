/*******************************************************************************
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

package org.onap.optf.cmso.aaf;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class AafUserRole.
 */
public class AafUserRole {
    private String url = "";
    private String[] pathParts = {};
    private String perm = "";
    private String method = "";
    private List<AafPerm> aafPerms = new ArrayList<>();

    /**
     * Instantiates a new aaf user role.
     *
     * @param url the url
     * @param perm the perm
     */
    public AafUserRole(String url, String perm) {
        this.setUrl(url);
        this.setPerm(perm);
        pathParts = url.split("\\/");

        String[] perms = perm.split(",");
        for (String p : perms) {
            String[] parts = p.split(" ");
            if (parts.length == 2) {
                method = parts[1];
            }
            else {
                method = "ALL";
            }

            String[] list = parts[0].split("\\|");
            if (list.length == 3) {
                AafPerm aafPerm = new AafPerm();
                aafPerm.setAction(list[2]);
                aafPerm.setInstance(list[1]);
                aafPerm.setType(list[0]);
                aafPerms.add(aafPerm);
            }
        }
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url the new url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the perm.
     *
     * @return the perm
     */
    public String getPerm() {
        return perm;
    }

    /**
     * Sets the perm.
     *
     * @param perm the new perm
     */
    public void setPerm(String perm) {
        this.perm = perm;
    }

    /**
     * Gets the aaf perms.
     *
     * @return the aaf perms
     */
    public List<AafPerm> getAafPerms() {
        return aafPerms;
    }

    /**
     * Sets the aaf perms.
     *
     * @param aafPerms the new aaf perms
     */
    public void setAafPerms(List<AafPerm> aafPerms) {
        this.aafPerms = aafPerms;
    }

    /**
     * Matches.
     *
     * @param path the path
     * @param matchMethod the match method
     * @return true, if successful
     */
    public boolean matches(String path, String matchMethod) {
        if (!this.method.equalsIgnoreCase("ALL") && !this.method.equals("*") && !this.method.equals(matchMethod)) {
            return false;
        }
        List<String> inNodes = new ArrayList<>();
        List<String> matchNodes = new ArrayList<>();
        String[] pathList = path.split("\\/");
        for (String n : pathList) {
            inNodes.add(n);
        }
        for (String n : pathParts) {
            matchNodes.add(n);
        }

        while (!inNodes.isEmpty() && !matchNodes.isEmpty()) {
            String inNode = inNodes.remove(0);
            String matchNode = matchNodes.get(0);
            if (matchNode.equals(inNode) || matchNode.equals("*")) {
                matchNodes.remove(0);
            } else {
                if (!matchNode.equals("**")) {
                    return false;
                }
            }
        }

        //
        if (inNodes.isEmpty() && matchNodes.isEmpty()) {
            return true;
        }

        // We have incoming nodes remaining, see if we can wildcard them
        if (matchNodes.size() == 1) {
            if (matchNodes.get(0).equals("**")) {
                return true;
            }
            if (inNodes.size() == 1 && matchNodes.get(0).equals("*")) {
                return true;
            }
        }
        return false;
    }
}
