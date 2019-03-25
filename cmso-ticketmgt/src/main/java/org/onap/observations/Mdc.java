/*
 * Copyright © 2019 AT&T Intellectual Property.
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
package org.onap.observations;

import static com.att.eelf.configuration.Configuration.MDC_BEGIN_TIMESTAMP;
import static com.att.eelf.configuration.Configuration.MDC_END_TIMESTAMP;
import static com.att.eelf.configuration.Configuration.MDC_KEY_REQUEST_ID;
import static com.att.eelf.configuration.Configuration.MDC_PARTNER_NAME;
import static com.att.eelf.configuration.Configuration.MDC_REMOTE_HOST;
import static com.att.eelf.configuration.Configuration.MDC_RESPONSE_CODE;
import static com.att.eelf.configuration.Configuration.MDC_RESPONSE_DESC;
import static com.att.eelf.configuration.Configuration.MDC_STATUS_CODE;
import static com.att.eelf.configuration.Configuration.MDC_TARGET_ENTITY;
import static com.att.eelf.configuration.Configuration.MDC_TARGET_SERVICE_NAME;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.StatusType;

import org.onap.observations.MessageHeaders.HeadersEnum;
import org.slf4j.MDC;

import com.att.eelf.utils.Stopwatch;

/**
 * ECOMP EELF logging MDC fields not defined in the MDC Configuration (i.e.
 * MDC_ALERT_SEVERITY)
 **/
public class Mdc {
	public static final String SERVICE_NAME = "CSS-Scheduler";
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

	public static String getCaller(int back)
	{
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		return stackTraceElements[back].getClassName() + "." + stackTraceElements[back].getMethodName();
	}

	public static void setCaller(int back)
	{
		String caller = MDC.get(Enum.ClassName.name());
		if (caller == null)
			MDC.put(Enum.ClassName.name(), getCaller(back));
	}

	public static void setObservation(ObservationInterface o)
	{
		MDC.put(Enum.CustomField4.name(), o.name());
	}

	public static void clearCaller()
	{
		MDC.remove(Enum.ClassName.name());
	}

	public static Map<String, String> save() 
	{
		Map<String, String> save = MDC.getCopyOfContextMap();
		return save;
	}

	public static void restore(Map<String, String> mdcSave) 
	{
		MDC.clear();
		for (String name : mdcSave.keySet())
			MDC.put(name, mdcSave.get(name));
	}



	public static void setRequestIdIfNotSet(String requestId) {
		if (MDC.get(MDC_KEY_REQUEST_ID) == null || MDC.get(MDC_KEY_REQUEST_ID).equals(""))
	{
			setRequestId(requestId);
		}
	}
	
	public static void setRequestId(String requestId) {
		MDC.put(MDC_KEY_REQUEST_ID, requestId);
	}
		
	public static void metricStart(ClientRequestContext requestContext) {
		MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));
		MDC.put(MDC_END_TIMESTAMP, MDC.get(MDC_BEGIN_TIMESTAMP));
		setPartnerTargetFromUri(requestContext.getUri());
	}

	public static void metricEnd(ClientResponseContext response) 
	{
		
		Date now = new Date();
		//MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(now));
		MDC.put(MDC_END_TIMESTAMP, Stopwatch.isoFormatter.format(now));
		setResponseInfo(response.getStatusInfo());
		
	}
	public static void auditStart(ContainerRequestContext requestContext, HttpServletRequest servletRequest) 
	{
		MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));
		MDC.put(MDC_END_TIMESTAMP, MDC.get(MDC_BEGIN_TIMESTAMP));
		MDC.put(MDC_REMOTE_HOST, servletRequest.getRemoteHost());
		MDC.put(Enum.ClassName.name(), getCaller(4));
		MultivaluedMap<String, String> headers = requestContext.getHeaders();
		String transactionId = (String) headers.getFirst(HeadersEnum.TransactionID.toString());
		if (transactionId != null)
		{
			setRequestId(transactionId);
		}
		else
		{
			setRequestId(UUID.randomUUID().toString());
		}
		
	}

	public static void auditEnd(ContainerRequestContext requestContext, ContainerResponseContext response)
	{
		Date now = new Date();
		//MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(now));
		MDC.put(MDC_END_TIMESTAMP, Stopwatch.isoFormatter.format(now));
		MDC.put(Enum.ClassName.name(), getCaller(4));
		
		setResponseInfo(response.getStatusInfo());
		
	}
		 
	private static void setResponseInfo(StatusType statusInfo)
	{
		Integer status = statusInfo.getStatusCode();
		String completed = "ERROR";
		if (status >=200 && status < 300)
		{
			completed = "COMPLETE";
		}
		MDC.put(MDC_RESPONSE_CODE, status.toString());
		MDC.put(MDC_RESPONSE_DESC, statusInfo.getReasonPhrase());
		MDC.put(MDC_STATUS_CODE, completed);
	}
		
	public static void setEvent(String requestID) {
		MDC.put(MDC_BEGIN_TIMESTAMP, Stopwatch.isoFormatter.format(new Date()));
		MDC.put(MDC_END_TIMESTAMP, MDC.get(MDC_BEGIN_TIMESTAMP));
		setRequestId(requestID);
	}

	private static void setPartnerTargetFromUri(URI uri)
	{
		try
		{
			MDC.put(MDC_PARTNER_NAME, uri.getHost());
			MDC.put(MDC_TARGET_ENTITY, uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort());
			MDC.put(MDC_TARGET_SERVICE_NAME, uri.getPath());
		}
		catch (Exception e)
		{
			MDC.put(MDC_PARTNER_NAME, "UNKNOWN");
			MDC.put(MDC_TARGET_ENTITY, "UNKNOWN");
			MDC.put(MDC_TARGET_SERVICE_NAME, "UNKNOWN");
		}
	}


}