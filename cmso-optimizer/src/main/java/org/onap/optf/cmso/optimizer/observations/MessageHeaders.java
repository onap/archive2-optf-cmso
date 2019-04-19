/*
 * Copyright © 2017-2018 AT&T Intellectual Property. Modifications Copyright © 2018 IBM.
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

package org.onap.optf.cmso.optimizer.observations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Class MessageHeaders.
 */
public class MessageHeaders {

    /**
     * The Enum HeadersEnum.
     */
    public enum HeadersEnum {
        UNDEFINED("UNDEFINED"),
        TransactionID("X-TransactionId"),
        FromAppID("X-FromAppId"),
        MinorVersion("X-MinorVersion"),
        PatchVersion("X-PatchVersion"),
        LatestVersion("X-LatestVersion"),;

        private final String text;

        private HeadersEnum(String text) {
            this.text = text;
        }

        /**
         * To string.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return text;
        }
    }

    /** The Constant supportedMajorVersions. */
    public static final Map<String, String> supportedMajorVersions = new HashMap<String, String>();

    static {
        supportedMajorVersions.put("v1", "0");
        supportedMajorVersions.put("v2", "0");
    }

    /** The Constant supportedMajorMinorVersions. */
    public static final Set<String> supportedMajorMinorVersions = new HashSet<String>();

    static {
        supportedMajorMinorVersions.add("v1.0");
        supportedMajorMinorVersions.add("v2.0");
    }

    /** The Constant latestVersion. */
    public static final String latestVersion = "2.0.0";

    /** The Constant patchVersion. */
    public static final String patchVersion = "0";

    /**
     * From string.
     *
     * @param text the text
     * @return the headers enum
     */
    public static HeadersEnum fromString(String text) {
        for (HeadersEnum e : HeadersEnum.values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return HeadersEnum.UNDEFINED;
    }

    /**
     * Gets the patch version.
     *
     * @return the patch version
     */
    public static String getPatchVersion() {
        return patchVersion;
    }

    /**
     * Gets the latest version.
     *
     * @return the latest version
     */
    public static String getLatestVersion() {
        return latestVersion;
    }

    public static Map<String, String> getSupportedmajorversions() {
        return supportedMajorVersions;
    }

    public static Set<String> getSupportedmajorminorversions() {
        return supportedMajorMinorVersions;
    }

    /**
     * Validate major version.
     *
     * @param major the major
     * @return true, if successful
     */
    public static boolean validateMajorVersion(String major) {
        String majorKey = major.toLowerCase();
        if (!supportedMajorVersions.containsKey(majorKey)) {
            return false;
        }
        return true;
    }

    /**
     * Validate major minor version.
     *
     * @param major the major
     * @param minor the minor
     * @return true, if successful
     */
    public static boolean validateMajorMinorVersion(String major, String minor) {
        String majorKey = major.toLowerCase();
        if (!supportedMajorVersions.containsKey(majorKey)) {
            return false;
        }

        if (minor != null) {
            String majorMinorKey = majorKey + "." + minor;
            return supportedMajorMinorVersions.contains(majorMinorKey);
        }
        return true;
    }
}
