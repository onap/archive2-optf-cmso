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

package org.onap.optf.cmso.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class CMSRequestError.
 */
public class CmsoRequestError implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty
    RequestError requestError;

    /**
     * Instantiates a new CMS request error.
     *
     * @param messageId the message id
     * @param text the text
     * @param variables the variables
     */
    public CmsoRequestError(String messageId, String text, List<String> variables) {
        requestError = new RequestError(messageId, text, variables);
    }

    /**
     * Instantiates a new CMS request error.
     *
     * @param messageId the message id
     * @param text the text
     */
    public CmsoRequestError(String messageId, String text) {
        requestError = new RequestError(messageId, text, new ArrayList<String>());
    }

    /**
     * The Class RequestError.
     */
    public class RequestError {
        @JsonProperty
        private String messageId;
        @JsonProperty
        private String text;
        @JsonProperty
        private List<String> variables;

        private RequestError(String messageId, String text, List<String> variables) {
            this.messageId = "Scheduler." + messageId;
            this.text      = text;
            this.variables = variables;
        }

        /**
         * To string.
         *
         * @return the string
         */
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(messageId).append(":").append(text).append(":").append(variables);
            return sb.toString();

        }
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return requestError.toString();
    }
}
