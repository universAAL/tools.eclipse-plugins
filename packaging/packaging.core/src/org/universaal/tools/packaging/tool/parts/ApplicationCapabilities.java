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

import java.io.Serializable;

import java.util.Enumeration;
import java.util.Properties;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class ApplicationCapabilities implements Serializable {

	private Properties capabilities;

	public ApplicationCapabilities(){

		capabilities = new Properties();

		//		Mandatory[] mandatory = Capability.Mandatory.values();
		//		for(int i = 0; i < mandatory.length; i++){
		//			Capability c = new Capability(mandatory[i].toString(), Application.defaultString);
		//			capabilities.put(c.getName(), c.getValue());
		//		}
		//
		//		Optional[] optional = Capability.Optional.values();
		//		for(int i = 0; i < optional.length; i++){
		//			Capability c = new Capability(optional[i].toString(), Application.defaultString);
		//			capabilities.put(c.getName(), c.getValue());
		//		}

		capabilities.put(Capability.MANDATORY_TARGET_SPACE, "");
		capabilities.put(Capability.MANDATORY_TARGET_SPACE_VERSION, "");
		capabilities.put(Capability.MANDATORY_MW_VERSION, "");
		capabilities.put(Capability.MANDATORY_ONTOLOGIES, "");
		capabilities.put(Capability.MANDATORY_TARGET_CONTAINER_NAME, "");
		capabilities.put(Capability.MANDATORY_TARGET_CONTAINER_VERSION, "");
		capabilities.put(Capability.MANDATORY_TARGET_DEPLOYMENT_TOOL, "");
		capabilities.put(Capability.OPTIONAL_OS, "");
		capabilities.put(Capability.OPTIONAL_PLATFORM, "");
		capabilities.put(Capability.OPTIONAL_DEVICE_FEATURES_AUDIO, "");
		capabilities.put(Capability.OPTIONAL_DEVICE_FEATURES_VISUAL, "");
	}

	public Properties getCapabilities() {
		return capabilities;
	}

	public void setCapability(String name, String value){
		capabilities.put(name, value);
	}

	public void setCapabilities(Properties cs){
		capabilities = cs;
	}

	public String getXML(){

		String r = "";

		try{
			Enumeration<Object> cs = capabilities.keys();
			while(cs.hasMoreElements()){
				String key = (String) cs.nextElement();
				if(key != null){
					String value = (String) capabilities.get(key);
					if(value != null && !value.isEmpty())
						r = r.concat("<capability><name>"+key+"</name>"+"<value>"+value+"</value></capability>");
				}
			}
		}
		catch(Exception ex){}

		return r;
	}

	/*
	 * <xs:complexType name="capabilityType">
		<xs:sequence>
			<xs:element name="name" type="xs:string">
			</xs:element>
			<xs:element name="value" type="xs:string">
			</xs:element>
		</xs:sequence>
	</xs:complexType>
	 */
}