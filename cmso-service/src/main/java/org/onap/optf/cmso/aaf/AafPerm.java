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
package org.onap.optf.cmso.aaf;

import java.util.HashSet;
import java.util.Set;

import org.onap.aaf.cadi.aaf.AAFPermission;


public class AafPerm 
{
	private String type;
	private String instance;
	private String action;
	private Set<String> actions = new HashSet<>();
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
		String list[] = action.split(",");
		for (String a : list)
			actions.add(a);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	
	public Set<String> getActions() {
		return actions;
	}
	public void setActions(Set<String> actions) {
		this.actions = actions;
	}
	public boolean matches(AAFPermission userPerm)
	{
		if (type.equals(userPerm.getType()))
		{
			if (userPerm.getInstance().equals("*") || instance.equals("*") || userPerm.getInstance().equals(instance))
			{
				for (String userAction : userPerm.getAction().split(","))
				{
					if (userAction.equals("*") || actions.contains("*") || actions.contains(userAction))
						return true;
				}
			}
		}
		return false;
	}
}
