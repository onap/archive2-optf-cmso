/*******************************************************************************
 * 
 *  Copyright © 2019 AT&T Intellectual Property.
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *          http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  
 *  Unless otherwise specified, all documentation contained herein is licensed
 *  under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 *  you may not use this documentation except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *          https://creativecommons.org/licenses/by/4.0/
 *  
 *  Unless required by applicable law or agreed to in writing, documentation
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/


package org.onap.optf.ticketmgt.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Level;
import org.onap.observations.ObservationInterface;

import com.att.eelf.configuration.EELFManager;
import com.att.eelf.i18n.EELFResourceManager;

public enum LogMessages implements ObservationInterface {

	FETCH_TICKET("Fetch ticket {0} : {1}: {2} : {3}", Status.OK, Level.INFO),
	CREATE_TICKET("Create Ticket {0} : {1}: {2} : {3}", Status.OK, Level.INFO),
	CANCEL_TICKET("Cancel ticket {0} : {1}: {2} : {3}", Status.OK, Level.INFO),
	UPDATE_TICKET("Update ticket {0} : {1}: {2} : {3}", Status.OK, Level.INFO),
	GET_ACTIVE_TICKETS("Get active tickets {0} : {1}: {2} : {3}", Status.OK, Level.INFO),
	SEARCH_TICKETS("Search tickets {0} : {1}: {2} : {3}", Status.OK, Level.INFO),
	
	TICKET_NOT_FOUND("Ticket not found id={0}", Status.NOT_FOUND, Level.INFO),
	INVALID_ATTRIBUTE("Invalid attribute {0}={1}", Status.BAD_REQUEST, Level.INFO),
	MISSING_REQUIRED_ATTRIBUTE("Missing required attribute '{0}'", Status.BAD_REQUEST, Level.INFO),
	INVALID_REQUEST("The input data structure is incorrect", Status.BAD_REQUEST, Level.INFO),
	REQUEST_TIMED_OUT("Request timed out.", Status.INTERNAL_SERVER_ERROR, Level.ERROR),
	UNEXPECTED_EXCEPTION("Unexpected exception encountered during processing. Please contact support : {0}", Status.INTERNAL_SERVER_ERROR, Level.ERROR),

	UNDEFINED_FILTER_ATTRIBUTE("Undefined filter attribute {0}", Status.BAD_REQUEST, Level.INFO),
	INVALID_DATE_FILTER("Invalid date filter provided {0}=(1}", Status.BAD_REQUEST, Level.INFO),
	
	INCOMING_MESSAGE("Incoming message method={0} path={1}", Status.OK, Level.INFO, true, false),
	INCOMING_MESSAGE_RESPONSE("Message response method={0} path={1} status={2}", Status.OK, Level.INFO, true, false),
	OUTGOING_MESSAGE("Outgoing message method={0} path={1}", Status.OK, Level.INFO, true, false),
	OUTGOING_MESSAGE_RETURNED("Outgoing message returned method={0} path={1} status={2}", Status.OK, Level.INFO, true, false),

	UNEXPECTED_RESPONSE("Unexpected response from {0} HTTP Status={1} : {2}", Status.OK, Level.INFO),
	INVALID_CHANGE_WINDOW("Change window end time {1} must be after start time {0}", Status.OK, Level.INFO),
	EXPECTED_EXCEPTION("Expected exception encountered during processing. {0}", Status.OK, Level.INFO),
	UNABLE_TO_UPDATE_TICKET("Unable to update change ticket in TM: Schedule ID: {0} : changeid: {1} :  Reason: {2}", Status.OK, Level.INFO),
	UNAUTHORIZED("Authorization failed.", Status.FORBIDDEN, Level.INFO),
	UNAUTHENTICATED("Authentication failed.", Status.UNAUTHORIZED, Level.INFO), 
	
	;
	private final String defaultId;
	private final String defaultMessage;
	private final String defaultResolution;
	private final String defaultAction;

	private final Status status;
	private final Level level;
	private final Boolean audit;
	private final Boolean metric;

	
	private LogMessages(String message, Status code, Level l)
	{
		defaultMessage = message;
		level=l;
		status = code;
		this.defaultId = this.name();
		this.defaultResolution = "No resolution needed";
		this.defaultAction = "No action is required";
		this.audit = false;
		this.metric = false;
	}

	private LogMessages(String message, Status code, Level l, Boolean audit, Boolean metric)
	{
		defaultMessage = message;
		level=l;
		status = code;
		this.audit = audit;
		this.metric = metric;
		this.defaultId = this.name();
		this.defaultResolution = "No resolution needed";
		this.defaultAction = "No action is required";
	}
	
	private LogMessages(String message, Status code, Level l, String id, String resolution, String action)
	{
		level=l;
		status = code;
		defaultMessage = message;
		this.defaultId = id;
		this.defaultResolution = resolution;
		this.defaultAction = action;
		this.audit = false;
		this.metric = false;
	}
	
	static {
		EELFResourceManager.loadMessageBundle("logmessages");
	}
	
	public String genProperties()
	{
		// Use this to regenerate properties file. The desire to change messages without updating code is
		// well understood, but the developer should be able to code the defaults without having to update 2 different files and 
		// get it wrong. 
		StringBuilder sb = new StringBuilder();
		sb.append("# Generated from ").append(this.getClass().getName()).append("\n");
		for (LogMessages lm : values())
		{
			sb.append(lm.name());
			sb.append(" ").append(lm.defaultId);
			sb.append("|").append(lm.defaultMessage);
			sb.append("|").append(lm.defaultResolution);
			sb.append("|").append(lm.defaultAction);
			sb.append("\n");
		}
		return sb.toString();
	}

	
	// interface methods
	@Override
	public Level getLevel() {return level;}
	@Override
	public String getMessage() {return defaultMessage;}
	@Override
	public Status getStatus() {return status;}
	@Override
	public Enum<?> getValue() {return this;}
	@Override
	public String getDomain() {return this.getClass().getSimpleName();}
	@Override
	public Boolean getAudit() { return audit; }
	@Override
	public Boolean getMetric() { return metric; }
	
	public static void main(String argv[])
	{
		System.out.println(LogMessages.UNEXPECTED_EXCEPTION.genProperties());
		try 
		{
			Files.write(Paths.get("src/main/resources/logmessages.properties"), LogMessages.UNEXPECTED_EXCEPTION.genProperties().getBytes());
		} 
		catch (IOException e) 
		{
			EELFManager.getInstance().getDebugLogger().debug("Failed to update properties file.", e);

		}
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><h1>Cell Site Selection Scheduler mS Observations</h1>\n<table border=\"1\">\n<tr>");
		sb.append("<td>Code</td> ");
		sb.append("<td>Log Level</td> ");
		sb.append("<td>Message</td> ");
		sb.append("</tr>\n");
		for (LogMessages m : LogMessages.values())
		{
			if (m.level == Level.ERROR || m.level == Level.WARN || m.level == Level.FATAL)
			{
				sb.append("<tr>");
				sb.append("<td>").append(m.name()).append("</td> ");
				sb.append("<td>").append(m.level).append("</td> ");
				sb.append("<td>").append(m.defaultMessage).append("</td> ");
				sb.append("</tr>\n");
			}
		}
		try 
		{
			Files.write(Paths.get("logmessages.html"), sb.toString().getBytes());
		} 
		catch (IOException e) 
		{
			EELFManager.getInstance().getDebugLogger().debug("Failed to update properties html file.", e);

		}
				
	}

}
