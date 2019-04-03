/*
 * Copyright © 2017-2018 AT&T Intellectual Property.
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

package org.onap.optf.cmso.common;

import static com.att.eelf.configuration.Configuration.MDC_BEGIN_TIMESTAMP;
import static com.att.eelf.configuration.Configuration.MDC_ELAPSED_TIME;
import static com.att.eelf.configuration.Configuration.MDC_END_TIMESTAMP;
import static com.att.eelf.configuration.Configuration.MDC_INSTANCE_UUID;
import static com.att.eelf.configuration.Configuration.MDC_KEY_REQUEST_ID;
import static com.att.eelf.configuration.Configuration.MDC_PARTNER_NAME;
import static com.att.eelf.configuration.Configuration.MDC_REMOTE_HOST;
import static com.att.eelf.configuration.Configuration.MDC_RESPONSE_CODE;
import static com.att.eelf.configuration.Configuration.MDC_RESPONSE_DESC;
import static com.att.eelf.configuration.Configuration.MDC_SERVER_FQDN;
import static com.att.eelf.configuration.Configuration.MDC_SERVER_IP_ADDRESS;
import static com.att.eelf.configuration.Configuration.MDC_SERVICE_INSTANCE_ID;
import static com.att.eelf.configuration.Configuration.MDC_SERVICE_NAME;
import static com.att.eelf.configuration.Configuration.MDC_STATUS_CODE;
import static com.att.eelf.configuration.Configuration.MDC_TARGET_ENTITY;
import static com.att.eelf.configuration.Configuration.MDC_TARGET_SERVICE_NAME;
import java.net.InetAddress;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.onap.optf.cmso.filters.MessageHeaders;
import org.quartz.JobExecutionContext;
import org.slf4j.MDC;
import com.att.eelf.utils.Stopwatch;

/**
 * EELF logging MDC fields not defined in the MDC Configuration (i.e.
 * MDC_ALERT_SEVERITY)
 **/
public class Mdc {
    public enum Enum {
        // BeginTimestamp,
        // EndTimeStamp,
        // RequestId,
        // ServiceInstanceId,
        VirtualServerName,
        // ServiceName,
        // PartnerName,
        // StatusCOde,
        // ResponseCode,
        // ResponseDescription,
        // InstanceUUID,
        // AlertSeverity,
        // ServerIPAddress,
        // ElapsedTime,
        // ServerFQDN,
        // RemoteHost,
        ClassName, Unused,
        // ProcessKey,
        CustomField1, CustomField2, CustomField3, CustomField4,
        // TargetVirtualEntity,
        // TargetEntity,
        // TargetServiceName,
        ErrorCode, ErrorDescription, Timer,
    }

    public static void begin(HttpServletRequest request, String requestId) {
        MDC.clear();
        Stopwatch.start();
        // MDC.put(MDC_ALERT_SEVERITY, "");
        MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));
        // MDC.put(MDC_ELAPSED_TIME, String.valueOf(Stopwatch.getDuration()));
        // MDC.put(MDC_END_TIMESTAMP, "");
        MDC.put(MDC_INSTANCE_UUID, UUID.randomUUID().toString());
        // MDC.put(MDC_KEY_REQUEST_ID, requestId);
        setRequestId(request, requestId);
        // MDC.put(MDC_PROCESS_KEY, "");
        MDC.put(MDC_REMOTE_HOST, request.getRemoteHost());
        // MDC.put(MDC_RESPONSE_CODE, "");
        // MDC.put(MDC_RESPONSE_DESC, "");
        // MDC.put(MDC_SERVICE_NAME, "");
        try {
            MDC.put(MDC_SERVER_FQDN, InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            MDC.put(MDC_SERVER_FQDN, e.getMessage());
        }
        try {
            MDC.put(MDC_SERVER_IP_ADDRESS, InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            MDC.put(MDC_SERVER_FQDN, e.getMessage());
        }
        MDC.put(MDC_SERVICE_INSTANCE_ID, "UNKNOWN");
        MDC.put(MDC_SERVICE_NAME, "cmso");
        // MDC.put(MDC_STATUS_CODE, "");
        setPartherTargetFromUri(request.getRequestURL().toString());
        // Override partner hostname with username
        MDC.put(MDC_PARTNER_NAME, BasicAuthenticatorFilter.getUser(request));
        // MDC.put(MDC_TARGET_ENTITY, "");
        // MDC.put(MDC_TARGET_SERVICE_NAME, "");
        // MDC.put(MDC_TARGET_VIRTUAL_ENTITY, "");
        MDC.put(Enum.ClassName.name(), getCaller(3));
        // MDC.put(MdcEnum.CustomField1.name(), "");
        // MDC.put(MdcEnum.CustomField2.name(), "");
        // MDC.put(MdcEnum.CustomField3.name(), "");
        // MDC.put(MdcEnum.CustomField4.name(), "");
        // MDC.put(MdcEnum.ErrorCode.name(), "");
        // MDC.put(MdcEnum.ErrorDescription.name(), "");
        // MDC.put(MdcEnum.Timer.name(), "");
        // MDC.put(MdcEnum.Unused.name(), "");
        // MDC.put(MdcEnum.VirtualServerName.name(), "");

    }

    private static void setRequestId(HttpServletRequest request, String requestId) {

        String transactionId = request.getHeader(MessageHeaders.HeadersEnum.TransactionID.toString());
        if (transactionId == null) {
            transactionId = requestId;
        }
        if (transactionId == null)
            transactionId = "Unknown";
        MDC.put(MDC_KEY_REQUEST_ID, transactionId);
    }

    public static void end(Response response) {
        Stopwatch.stop();
        // MDC.put(MDC_ALERT_SEVERITY, "");
        // MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(new
        // Date()));
        MDC.put(MDC_ELAPSED_TIME, String.valueOf(Stopwatch.getDuration()));
        MDC.put(MDC_END_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));
        // MDC.put(MDC_INSTANCE_UUID, "");
        // MDC.put(MDC_KEY_REQUEST_ID, "");
        // MDC.put(MDC_PARTNER_NAME, "");
        // MDC.put(MDC_PROCESS_KEY, "");
        // MDC.put(MDC_REMOTE_HOST, "");
        MDC.put(MDC_RESPONSE_CODE, String.valueOf(response.getStatus()));
        MDC.put(MDC_RESPONSE_DESC, response.getStatusInfo().getReasonPhrase());
        // MDC.put(MDC_SERVICE_NAME, "");
        // MDC.put(MDC_SERVER_FQDN, "");
        // MDC.put(MDC_SERVER_IP_ADDRESS, "");
        // MDC.put(MDC_SERVICE_INSTANCE_ID, "");
        // MDC.put(MDC_SERVICE_NAME, "");
        MDC.put(MDC_STATUS_CODE, "COMPLETE");
        if (response.getStatus() == 500)
            MDC.put(MDC_STATUS_CODE, "ERROR");
        // MDC.put(MDC_TARGET_ENTITY, "");
        // MDC.put(MDC_TARGET_SERVICE_NAME, "");
        // MDC.put(MDC_TARGET_VIRTUAL_ENTITY, "");
        MDC.put(Enum.ClassName.name(), getCaller(3));
        // MDC.put(MdcEnum.CustomField1.name(), "");
        // MDC.put(MdcEnum.CustomField2.name(), "");
        // MDC.put(MdcEnum.CustomField3.name(), "");
        // MDC.put(MdcEnum.CustomField4.name(), "");
        // MDC.put(MdcEnum.ErrorCode.name(), "");
        // MDC.put(MdcEnum.ErrorDescription.name(), "");
        // MDC.put(MdcEnum.Timer.name(), "");
        // MDC.put(MdcEnum.Unused.name(), "");
        // MDC.put(MdcEnum.VirtualServerName.name(), "");

    }

    public static String getCaller(int back) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        return stackTraceElements[back].getClassName() + "." + stackTraceElements[back].getMethodName();
    }

    public static void quartzJobBegin(JobExecutionContext context) {
        MDC.clear();
        Stopwatch.start();
        // MDC.put(MDC_ALERT_SEVERITY, "");
        MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));
        // MDC.put(MDC_ELAPSED_TIME, String.valueOf(Stopwatch.getDuration()));
        // MDC.put(MDC_END_TIMESTAMP, "");
        MDC.put(MDC_INSTANCE_UUID, UUID.randomUUID().toString());
        // MDC.put(MDC_KEY_REQUEST_ID);
        // MDC.put(MDC_PARTNER_NAME, request.getRemoteUser());
        // MDC.put(MDC_PROCESS_KEY, "");
        // MDC.put(MDC_REMOTE_HOST, request.getRemoteHost());
        // MDC.put(MDC_RESPONSE_CODE, "");
        // MDC.put(MDC_RESPONSE_DESC, "");
        // MDC.put(MDC_SERVICE_NAME, "");
        try {
            MDC.put(MDC_SERVER_FQDN, InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            MDC.put(MDC_SERVER_FQDN, e.getMessage());
        }
        try {
            MDC.put(MDC_SERVER_IP_ADDRESS, InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            MDC.put(MDC_SERVER_FQDN, e.getMessage());
        }
        MDC.put(MDC_SERVICE_INSTANCE_ID, "UNKNOWN");
        MDC.put(MDC_SERVICE_NAME, "cmso");
        // MDC.put(MDC_STATUS_CODE, "");
        // MDC.put(MDC_TARGET_ENTITY, "");
        // MDC.put(MDC_TARGET_SERVICE_NAME, "");
        // MDC.put(MDC_TARGET_VIRTUAL_ENTITY, "");
        MDC.put(Enum.ClassName.name(), getCaller(3));
        // MDC.put(MdcEnum.CustomField1.name(), "");
        // MDC.put(MdcEnum.CustomField2.name(), "");
        // MDC.put(MdcEnum.CustomField3.name(), "");
        // MDC.put(MdcEnum.CustomField4.name(), "");
        // MDC.put(MdcEnum.ErrorCode.name(), "");
        // MDC.put(MdcEnum.ErrorDescription.name(), "");
        // MDC.put(MdcEnum.Timer.name(), "");
        // MDC.put(MdcEnum.Unused.name(), "");
        // MDC.put(MdcEnum.VirtualServerName.name(), "");

    }

    public static void quartzJobEnd(JobExecutionContext context) {
        Stopwatch.stop();
        // MDC.put(MDC_ALERT_SEVERITY, "");
        MDC.put(MDC_END_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));
        MDC.put(MDC_ELAPSED_TIME, String.valueOf(Stopwatch.getDuration()));
        // MDC.put(MDC_START_TIMESTAMP, "");
        // MDC.put(MDC_INSTANCE_UUID, UUID.randomUUID().toString());
        // MDC.put(MDC_KEY_REQUEST_ID);
        // MDC.put(MDC_PARTNER_NAME, request.getRemoteUser());
        // MDC.put(MDC_PROCESS_KEY, "");
        // MDC.put(MDC_REMOTE_HOST, request.getRemoteHost());
        // MDC.put(MDC_RESPONSE_CODE, "");
        // MDC.put(MDC_RESPONSE_DESC, "");
        // MDC.put(MDC_SERVICE_NAME, "");
        // try{ MDC.put(MDC_SERVER_FQDN, InetAddress.getLocalHost().getHostName()); }
        // catch (Exception e){ MDC.put(MDC_SERVER_FQDN, e.getMessage());}
        // try{ MDC.put(MDC_SERVER_IP_ADDRESS,
        // InetAddress.getLocalHost().getHostAddress()); } catch (Exception e){
        // MDC.put(MDC_SERVER_FQDN, e.getMessage());}
        // MDC.put(MDC_SERVICE_INSTANCE_ID, "UNKNOWN");
        // MDC.put(MDC_SERVICE_NAME, "cmso");
        // MDC.put(MDC_STATUS_CODE, "");
        // MDC.put(MDC_TARGET_ENTITY, "");
        // MDC.put(MDC_TARGET_SERVICE_NAME, "");
        // MDC.put(MDC_TARGET_VIRTUAL_ENTITY, "");
        MDC.put(Enum.ClassName.name(), getCaller(3));
        // MDC.put(MdcEnum.CustomField1.name(), "");
        // MDC.put(MdcEnum.CustomField2.name(), "");
        // MDC.put(MdcEnum.CustomField3.name(), "");
        // MDC.put(MdcEnum.CustomField4.name(), "");
        // MDC.put(MdcEnum.ErrorCode.name(), "");
        // MDC.put(MdcEnum.ErrorDescription.name(), "");
        // MDC.put(MdcEnum.Timer.name(), "");
        // MDC.put(MdcEnum.Unused.name(), "");
        // MDC.put(MdcEnum.VirtualServerName.name(), "");

    }

    public static Map<String, String> save() {
        Map<String, String> save = MDC.getCopyOfContextMap();
        return save;
    }

    public static void restore(Map<String, String> mdcSave) {
        MDC.clear();
        for (String name : mdcSave.keySet())
            MDC.put(name, mdcSave.get(name));
    }

    public static void metricStart(String requestId, String url) {
        MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));
        // MDC.put(MDC_ELAPSED_TIME, String.valueOf(Stopwatch.getDuration()));
        // MDC.put(MDC_END_TIMESTAMP, "");
        // MDC.put(MDC_INSTANCE_UUID, UUID.randomUUID().toString());
        // MDC.put(MDC_KEY_REQUEST_ID, requestId);
        // MDC.put(MDC_PARTNER_NAME, invocationBuilder.ge.getRemoteUser());
        // MDC.put(MDC_PROCESS_KEY, "");
        // MDC.put(MDC_REMOTE_HOST, request.getRemoteHost());
        // MDC.put(MDC_RESPONSE_CODE, "");
        // MDC.put(MDC_RESPONSE_DESC, "");
        // MDC.put(MDC_SERVICE_NAME, "");
        // try{ MDC.put(MDC_SERVER_FQDN, InetAddress.getLocalHost().getHostName()); }
        // catch (Exception e){ MDC.put(MDC_SERVER_FQDN, e.getMessage());}
        // try{ MDC.put(MDC_SERVER_IP_ADDRESS,
        // InetAddress.getLocalHost().getHostAddress()); } catch (Exception e){
        // MDC.put(MDC_SERVER_FQDN, e.getMessage());}
        // MDC.put(MDC_SERVICE_INSTANCE_ID, "UNKNOWN");
        // MDC.put(MDC_SERVICE_NAME, "cmso");
        // MDC.put(MDC_STATUS_CODE, "");
        setPartherTargetFromUri(url);
        // MDC.put(MDC_TARGET_ENTITY, "");
        // MDC.put(MDC_TARGET_SERVICE_NAME, "");
        // MDC.put(MDC_TARGET_VIRTUAL_ENTITY, "");
        MDC.put(Enum.ClassName.name(), getCaller(3));
        // MDC.put(MdcEnum.CustomField1.name(), "");
        // MDC.put(MdcEnum.CustomField2.name(), "");
        // MDC.put(MdcEnum.CustomField3.name(), "");
        // MDC.put(MdcEnum.CustomField4.name(), "");
        // MDC.put(MdcEnum.ErrorCode.name(), "");
        // MDC.put(MdcEnum.ErrorDescription.name(), "");
        // MDC.put(MdcEnum.Timer.name(), "");
        // MDC.put(MdcEnum.Unused.name(), "");
        // MDC.put(MdcEnum.VirtualServerName.name(), "");

    }

    public static void metricEnd(Response response) {

        // MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));
        try {
            Long then = Stopwatch.isoFormatter.parse(MDC.get(MDC_BEGIN_TIMESTAMP)).getTime();
            Long now = System.currentTimeMillis();
            MDC.put(MDC_ELAPSED_TIME, String.valueOf(now - then));
        } catch (Exception e) {
            MDC.put(MDC_ELAPSED_TIME, "");
        }
        MDC.put(MDC_END_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));

        // MDC.put(MDC_INSTANCE_UUID, UUID.randomUUID().toString());
        // MDC.put(MDC_KEY_REQUEST_ID, requestId);
        // MDC.put(MDC_PARTNER_NAME, invocationBuilder.ge.getRemoteUser());
        // MDC.put(MDC_PROCESS_KEY, "");
        // MDC.put(MDC_REMOTE_HOST, response.getLocation().toString());
        MDC.put(MDC_RESPONSE_CODE, String.valueOf(response.getStatus()));
        MDC.put(MDC_RESPONSE_DESC, response.getStatusInfo().getReasonPhrase());
        // MDC.put(MDC_SERVICE_NAME, "");
        // try{ MDC.put(MDC_SERVER_FQDN, InetAddress.getLocalHost().getHostName()); }
        // catch (Exception e){ MDC.put(MDC_SERVER_FQDN, e.getMessage());}
        // try{ MDC.put(MDC_SERVER_IP_ADDRESS,
        // InetAddress.getLocalHost().getHostAddress()); } catch (Exception e){
        // MDC.put(MDC_SERVER_FQDN, e.getMessage());}
        // MDC.put(MDC_SERVICE_INSTANCE_ID, "UNKNOWN");
        // MDC.put(MDC_SERVICE_NAME, "cmso");
        MDC.put(MDC_STATUS_CODE, "COMPLETE");
        if (response.getStatus() == 500)
            MDC.put(MDC_STATUS_CODE, "ERROR");
        // MDC.put(MDC_TARGET_ENTITY, "");
        // MDC.put(MDC_TARGET_SERVICE_NAME, "");
        // MDC.put(MDC_TARGET_VIRTUAL_ENTITY, "");
        MDC.put(Enum.ClassName.name(), getCaller(3));
        // MDC.put(MdcEnum.CustomField1.name(), "");
        // MDC.put(MdcEnum.CustomField2.name(), "");
        // MDC.put(MdcEnum.CustomField3.name(), "");
        // MDC.put(MdcEnum.CustomField4.name(), "");
        // MDC.put(MdcEnum.ErrorCode.name(), "");
        // MDC.put(MdcEnum.ErrorDescription.name(), "");
        // MDC.put(MdcEnum.Timer.name(), "");
        // MDC.put(MdcEnum.Unused.name(), "");
        // MDC.put(MdcEnum.VirtualServerName.name(), "");

    }

    private static void setPartherTargetFromUri(String url) {
        try {
            URI uri = new URI(url);
            MDC.put(MDC_PARTNER_NAME, uri.getHost());
            MDC.put(MDC_TARGET_ENTITY, uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort());
            MDC.put(MDC_TARGET_SERVICE_NAME, uri.getPath());
        } catch (Exception e) {
            MDC.put(MDC_PARTNER_NAME, "UNKNOWN");
            MDC.put(MDC_TARGET_ENTITY, "UNKNOWN");
            MDC.put(MDC_TARGET_SERVICE_NAME, url);
        }
    }
}
