/**
 * ============LICENSE_START=======================================================
 * org.onap.optf.cmso
 * ================================================================================
 * Copyright © 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.optf.ticketmgt.aaf;

import org.springframework.core.Ordered;

public enum FilterPriority {
    AAF_AUTHENTICATION(Ordered.HIGHEST_PRECEDENCE), AAF_AUTHORIZATION(Ordered.HIGHEST_PRECEDENCE + 1); // higher number
                                                                                                       // = lower
                                                                                                       // priority

    private final int priority;

    FilterPriority(final int p) {
        priority = p;
    }

    public int getPriority() {
        return priority;
    }
}
