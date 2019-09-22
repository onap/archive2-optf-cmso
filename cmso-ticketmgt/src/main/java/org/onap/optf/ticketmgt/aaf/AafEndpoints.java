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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Intent is to use AAF vanity URL however, this allows us to support a list of URLs.
 *
 */
@Component
public class AafEndpoints extends BaseEndpoints {

    @Autowired
    Environment env;

    public enum Endpoint implements EndpointInterface {
        AUTHZ(AafProperties.aafAuthzPath, "/authz/perms/user/"), HEALTHCHECK(AafProperties.aafHealthCheckPath, "/"),;

        private final AafProperties pathName;
        private final String defaultPath;

        private Endpoint(AafProperties pathname, String defaultPath) {
            this.pathName = pathname;
            this.defaultPath = defaultPath;
        }

        @Override
        public AafProperties getPathName() {
            return pathName;
        }

        @Override
        public String defaultPath() {
            return defaultPath;
        }

        @Override
        public EndpointInterface[] getValues() {
            return Endpoint.values();
        }
    }
}
