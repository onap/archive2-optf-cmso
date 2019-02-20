/*
 * Copyright © 2017-2019 AT&T Intellectual Property.
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

package org.onap.optf.cmso.filters;

import static com.att.eelf.configuration.Configuration.MDC_KEY_REQUEST_ID;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.onap.observations.Mdc;
import org.onap.observations.Observation;
import org.onap.optf.cmso.common.LogMessages;
import org.onap.optf.cmso.filters.MessageHeaders.HeadersEnum;
import org.onap.optf.cmso.service.rs.CMSOServiceImpl;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

// @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class CMSOClientFilters implements ClientRequestFilter, ClientResponseFilter {

    private static EELFLogger log = EELFManager.getInstance().getLogger(CMSOServiceImpl.class);
    private static String appId = "cmso";

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		// On the way back
		Mdc.metricEnd(responseContext);
		Mdc.setCaller(17);
		Observation.report(LogMessages.OUTGOING_MESSAGE_RETURNED, 
				requestContext.getMethod(),
				requestContext.getUri().getPath().toString(),
				responseContext.getStatusInfo().toString());
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
		// On the way out 
		Mdc.metricStart(requestContext);
		Mdc.setCaller(17);
		Observation.report(LogMessages.OUTGOING_MESSAGE, 
				requestContext.getMethod(),
				requestContext.getUri().getPath().toString());
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        String transactionId = (String) headers.getFirst(MessageHeaders.HeadersEnum.TransactionID.toString());
        String mdcId = MDC.get(MDC_KEY_REQUEST_ID);
        if (transactionId == null || transactionId.equals(""))
            if (mdcId != null)
                headers.add(HeadersEnum.TransactionID.toString(), mdcId);
        headers.add(HeadersEnum.FromAppID.toString(), appId);
    }

}
