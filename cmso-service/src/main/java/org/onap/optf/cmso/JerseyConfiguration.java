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

package org.onap.optf.cmso;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.onap.optf.cmso.dispatcher.rs.DispatcherServiceImpl;
import org.onap.optf.cmso.filters.CmsoContainerFilters;
import org.onap.optf.cmso.service.rs.AdminToolImpl;
import org.onap.optf.cmso.service.rs.CmsoServiceImpl;
import org.onap.optf.cmso.service.rs.HealthCheckImpl;
import org.onap.optf.cmso.test.loopback.TicketMgtLoopbackServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/")
public class JerseyConfiguration extends ResourceConfig {
    private static final Logger log = Logger.getLogger(JerseyConfiguration.class.getName());

    /**
     * Object mapper.
     *
     * @return the object mapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return objectMapper;
    }

    /**
     * Instantiates a new jersey configuration.
     */
    @Autowired
    public JerseyConfiguration( /* LogRequestFilter lrf */ ) {
        register(CmsoServiceImpl.class);
        register(TicketMgtLoopbackServiceImpl.class);
        register(HealthCheckImpl.class);
        register(AdminToolImpl.class);
        register(DispatcherServiceImpl.class);
        property(ServletProperties.FILTER_FORWARD_ON_404, true);
        // TODO: ONAP Conversion identify appropriate ONAP logging filters if any
        // register(lrf, 6001);
        // register(LogResponseFilter.class, 6004);

        // TODO: Examine which logging features to enable
        register(new LoggingFeature(log));
        register(CmsoContainerFilters.class);
    }

    /**
     * Jersey client.
     *
     * @return the client
     */
    @Bean
    public Client jerseyClient() {
        ClientConfig client = new ClientConfig();

        // TODO: ONAP Conversion identify appropriate ONAP logging filters if any
        // client.register(TransactionIdRequestFilter.class);
        // client.register(TransactionIdResponseFilter.class);
        // client.register(DateTimeParamConverterProvider.class);

        return ClientBuilder.newClient(client);
    }
}
