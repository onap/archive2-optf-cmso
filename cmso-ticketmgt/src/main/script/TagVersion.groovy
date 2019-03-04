/*-
 * ============LICENSE_START=======================================================
 * ONAP CMSO
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights
 *                             reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 * 
 */

package org.onap.cmso.maven.scripts

println project.properties['cmso.project.version']; 
def versionArray; 
if ( project.properties['cmso.project.version'] != null ) { 
    versionArray = project.properties['cmso.project.version'].split('\\.'); 
} 

if ( project.properties['cmso.project.version'].endsWith("-SNAPSHOT") ) { 
    project.properties['project.docker.latesttag.version']=versionArray[0] + '.' + versionArray[1] + "-SNAPSHOT-latest";
    project.properties['project.docker.latesttagtimestamp.version']=versionArray[0] + '.' + versionArray[1] + "-SNAPSHOT-"+project.properties['cmso.build.timestamp']; 
} else { 
    project.properties['project.docker.latesttag.version']=versionArray[0] + '.' + versionArray[1] + "-STAGING-latest";
    project.properties['project.docker.latesttagtimestamp.version']=versionArray[0] + '.' + versionArray[1] + "-STAGING-"+project.properties['cmso.build.timestamp'];
} 

println 'New Tag for docker:' + project.properties['project.docker.latesttag.version'];