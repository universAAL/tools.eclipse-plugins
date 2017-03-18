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

public class Capability {

	private String name, value;

	public Capability(String name, String value){
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public final static String MANDATORY_TARGET_SPACE = "aal.target-space.category";
	public final static String MANDATORY_TARGET_SPACE_VERSION = "aal.target-space.version";
	public final static String MANDATORY_MW_VERSION = "org.universAAL.platform.version";
	public final static String MANDATORY_ONTOLOGIES = "aal.required-ontology"; 
	public final static String MANDATORY_TARGET_CONTAINER_NAME = "org.universAAL.container.name";
	public final static String MANDATORY_TARGET_CONTAINER_VERSION = "org.universAAL.container.version";
	public final static String MANDATORY_TARGET_DEPLOYMENT_TOOL = "aal.target.deployment-tool";

	public final static String OPTIONAL_OS = "org.universAAL.container.os"; 
	public final static String OPTIONAL_PLATFORM = "org.universAAL.container.platform";
	public final static String OPTIONAL_DEVICE_FEATURES_AUDIO = "aal.device.features.audio";
	public final static String OPTIONAL_DEVICE_FEATURES_VISUAL = "aal.device.features.visual";

	public String getXML(){
		return "<name>"+name+"</name><value>"+value+"</value>";
	}

	/*
aal.target-space.category
aal.target-space.version
aal.mw.version
aal.required-ontology
aal.target.container.name
aal.target.container.version
aal.target.deployment-tool

aal.os.name
aal.platform.name
aal.device.features.audio
aal.device.features.visual
...
	 */
}