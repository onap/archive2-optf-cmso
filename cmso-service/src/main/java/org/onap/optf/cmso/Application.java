/*
 * ============LICENSE_START=======================================================================================
 * Copyright (c) 2019 AT&T Intellectual Property.
 * ===================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================================================
 *
 ******************************************************************************/

package org.onap.optf.cmso;

import com.att.eelf.configuration.Configuration;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.net.InetAddress;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.apache.catalina.connector.Connector;
import org.onap.optf.cmso.common.LogMessages;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = {"org.onap.optf.cmso"})
@EnableAsync
@EnableAutoConfiguration(exclude = { /*DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class*/ })
public class Application extends SpringBootServletInitializer {

    private static EELFLogger log = EELFManager.getInstance().getLogger(Application.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Value("${server.dispatchPort:8089}")
    private String dispatchPort;

    @PostConstruct
    void started() {
        // Make sure all datetimes are stored in UTC format.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        initMdcData();
        SpringApplication.run(Application.class, args);
    }

    protected static void initMdcData() {
        MDC.clear();
        try {
            MDC.put(Configuration.MDC_SERVER_FQDN, InetAddress.getLocalHost().getHostName());
            MDC.put("hostname", InetAddress.getLocalHost().getCanonicalHostName());
            MDC.put("serviceName", System.getProperty("info.build.artifact"));
            MDC.put("version", System.getProperty("info.build.version"));
            MDC.put(Configuration.MDC_SERVER_IP_ADDRESS, InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            log.error(LogMessages.UNEXPECTED_EXCEPTION, e, e.getMessage());
        }
    }

    /**
     * Servlet container.
     *
     * @return the servlet web server factory
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        int port = Integer.parseInt(dispatchPort);
        tomcat.addAdditionalTomcatConnectors(createStandardConnector(port));
        return tomcat;
    }

    private Connector createStandardConnector(int port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(port);
        return connector;
    }
}
