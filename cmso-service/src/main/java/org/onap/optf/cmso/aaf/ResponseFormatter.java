/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.optf.cmso.aaf;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.onap.optf.cmso.common.exceptions.CMSException;

class ResponseFormatter {

    private static final String ACCEPT_HEADER = "accept";

    static void errorResponse(HttpServletRequest request, HttpServletResponse response, CMSException error) throws IOException {
        String accept = request.getHeader(ACCEPT_HEADER) == null ? MediaType.APPLICATION_JSON : request.getHeader(ACCEPT_HEADER);
        response.setStatus(error.getStatus().getStatusCode());
        response.getWriter().write(error.getRequestError().toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

}
