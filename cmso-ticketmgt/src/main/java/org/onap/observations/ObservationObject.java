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

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Level;

import com.att.eelf.i18n.EELFResolvableErrorEnum;
import com.att.eelf.i18n.EELFResourceManager;


public class ObservationObject implements ObservationInterface
{
	
	//*************************************************************************************************
	// Interface class that matches the ObservationInteface  pattern
	// This will be used in case we decide to provide external overrides and we need to instantiate
	// For now, we'll just use the Enum itself.
	//
	// 
	private Enum<?> value = null;
	private Level level = null;;
	private String message = null;
	private Status status = null;
	private String domain = null;
	private Boolean metric = false;
	private Boolean audit = false;
	public ObservationObject(ObservationInterface o)
	{
		this.value = o.getValue();
		this.level = o.getLevel();
		this.message = o.getMessage();
		this.status = o.getStatus();
		this.domain = o.getDomain();
		this.metric = o.getMetric();
		this.audit = o.getAudit();
		
	}
	public Enum<?> getValue() {return value;}
	@Override
	public String getMessage() {return message;}
	@Override
	public Status getStatus() {return status;}
	@Override
	public String getDomain() {return domain;}

	@Override
	public Level getLevel() {
		return level;
	}
	@Override
	public String name() {
		return value.name();
	}
	@Override
	public Boolean getAudit() {
		return audit;
	}
	@Override
	public Boolean getMetric() {
		return metric;
	}

	public String getMessage(String ...arguments) {
		return EELFResourceManager.format((EELFResolvableErrorEnum)value,  arguments);
	}
	public void setValue(Enum<?> value) {
		this.value = value;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setStatus(Status status) {
		this.status = status;
	}


}
