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

import java.util.ArrayList;
import java.util.List;

public class AafUserRole 
{
	private String url = "";
	private String pathParts[] = {};
	private String perm  = "";
	private String method = "";
	private List<AafPerm>  aafPerms = new ArrayList<>(); 
	
	public AafUserRole(String url, String perm)
	{
		this.setUrl(url);
		this.setPerm(perm);
		pathParts = url.split("\\/");
		
		String[] perms = perm.split(",");
		for (String p : perms)
		{
			String parts[] = p.split(" ");
			if (parts.length == 2)
				method = parts[1];
			else
				method = "ALL";
			
			String[] list = parts[0].split("\\|");
			if (list.length == 3)
			{
				AafPerm aafPerm = new AafPerm();
				aafPerm.setAction(list[2]);
				aafPerm.setInstance(list[1]);
				aafPerm.setType(list[0]);
				aafPerms.add(aafPerm);
			}
		}
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPerm() {
		return perm;
	}
	public void setPerm(String perm) {
		this.perm = perm;
	}
	public List<AafPerm> getAafPerms() {
		return aafPerms;
	}
	public void setAafPerms(List<AafPerm> aafPerms) {
		this.aafPerms = aafPerms;
	}

	public boolean matches(String path, String matchMethod)
	{
		if (!this.method.equalsIgnoreCase("ALL") 
			&& !this.method.equals("*")
			&& !this.method.equals(matchMethod))
			return false;
		List<String> inNodes = new ArrayList<>();
		List<String> matchNodes = new ArrayList<>();
		String[] pathList = path.split("\\/");
		for (String n : pathList)
		{
			inNodes.add(n);
		}
		for (String n : pathParts)
		{
			matchNodes.add(n);
		}
		
		while (!inNodes.isEmpty() && !matchNodes.isEmpty())
		{
			String inNode = inNodes.remove(0);
			String matchNode = matchNodes.get(0);
			if (matchNode.equals(inNode) || matchNode.equals("*"))
			{
				matchNodes.remove(0);
			}
			else
			{	
				if (!matchNode.equals("**"))
				{
					return false;
				}
			}
		}
		
		// 
		if (inNodes.isEmpty() && matchNodes.isEmpty())
			return true;
		
		// We have incoming nodes remaining, see if we can wildcard them
		if (matchNodes.size() == 1)
		{
			if (matchNodes.get(0).equals("**"))
				return true;
			if (inNodes.size() == 1 && matchNodes.get(0).equals("*"))
				return true;
		}
		return false;
	}
}
