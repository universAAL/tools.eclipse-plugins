/*

        Copyright 2007-2014 CNR-ISTI, http://isti.cnr.it
        Institute of Information Science and Technologies
        of the Italian National Research Council

        See the NOTICE file distributed with this work for additional
        information regarding copyright ownership

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */
package org.universaal.tools.packaging.tool.parts;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class KarafFeature {

	private String name, version, description, resolver, startLevel, groupID, artifactID;// bundle;
	private boolean start;

	public KarafFeature(String name, String version, String groupID, String artifactID){

		this.name = name;
		this.version = version;
		this.groupID = groupID;
		this.artifactID = artifactID;

		//this.bundle = "mvn:"+groupID+"/"+artifactID+"/"+version;

		this.description = "";
		this.resolver = "";
		this.startLevel = "0";
		this.start = false;
	}

	public KarafFeature(String name, String version, String groupID, String artifactID, String description, String resolver, String startLevel, boolean start){

		this.name = name;
		this.version = version;
		this.groupID = groupID;
		this.artifactID = artifactID;

		//this.bundle = "mvn:"+groupID+"/"+artifactID+"/"+version;

		this.description = description;
		this.resolver = resolver;
		this.start = start;
		this.startLevel = startLevel;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getResolver() {
		return resolver;
	}
	public void setResolver(String resolver) {
		this.resolver = resolver;
	}
	public String getStartLevel() {
		return startLevel;
	}
	public void setStartLevel(String startLevel) {
		this.startLevel = startLevel;
	}
	public String getGroupID() {
		return groupID;
	}
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	public String getArtifactID() {
		return artifactID;
	}
	public void setArtifactID(String artifactID) {
		this.artifactID = artifactID;
	}
	public boolean isStart() {
		return start;
	}
	public void setStart(boolean start) {
		this.start = start;
	}
//	public String getBundle(){
//		return bundle;
//	}
}