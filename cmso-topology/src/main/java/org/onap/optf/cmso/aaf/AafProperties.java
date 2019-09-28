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

package org.onap.optf.cmso.aaf;

public enum AafProperties {
    mechidUser("mechid.user"), mechidPass("mechid.pass"), aafUrls("aaf.urls"), aafAuthzPath(
                    "aaf.path.authz"), aafHealthCheckPath("aaf.path.healthcheck"), aafCacheTimeout(
                                    "aaf.cache.timeout"), aafUserRoleProperties(
                                                    "aaf.user.role.properties"), aafDefaultUserDomain(
                                                                    "aaf.default.user.domain"), aafEnabled(
                                                                                    "aaf.enabled"), aafNamespace(
                                                                                                    "aaf.namespace"),;
    private final String text;

    private AafProperties(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
