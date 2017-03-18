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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class Part implements Serializable {

	private String id; // unique
	private String name;
	private String bundleId = "";
	private String bundleVersion = "";
	private Properties partCapabilities;
	private List<Requirement> partRequirements;
	//private List<DeploymentUnit> deploymentUnits;
	private DeploymentUnit deploymentUnit;
	//private List<ExecutionUnit> executionUnits;
	private ExecutionUnit executionUnit = null;
	
	public Part(String id, String name){

		this.id = id;
		this.name = name;
		
		partCapabilities = new Properties();
		//		Mandatory[] mandatory = Capability.Mandatory.values();
		//		for(int i = 0; i < mandatory.length; i++){
		//			Capability c = new Capability(mandatory[i].toString(), Application.defaultString);
		//			partCapabilities.put(c.getName(), c.getValue());
		//		}
		//
		//		Optional[] optional = Capability.Optional.values();
		//		for(int i = 0; i < optional.length; i++){
		//			Capability c = new Capability(optional[i].toString(), Application.defaultString);
		//			partCapabilities.put(c.getName(), c.getValue());
		//		}

		partCapabilities.put(Capability.MANDATORY_TARGET_SPACE, "");
		partCapabilities.put(Capability.MANDATORY_TARGET_SPACE_VERSION, "");
		partCapabilities.put(Capability.MANDATORY_MW_VERSION, "");
		partCapabilities.put(Capability.MANDATORY_ONTOLOGIES, "");
		partCapabilities.put(Capability.MANDATORY_TARGET_CONTAINER_NAME, "");
		partCapabilities.put(Capability.MANDATORY_TARGET_CONTAINER_VERSION, "");
		partCapabilities.put(Capability.MANDATORY_TARGET_DEPLOYMENT_TOOL, "");
		partCapabilities.put(Capability.OPTIONAL_OS, "");
		partCapabilities.put(Capability.OPTIONAL_PLATFORM, "");
		partCapabilities.put(Capability.OPTIONAL_DEVICE_FEATURES_AUDIO, "");
		partCapabilities.put(Capability.OPTIONAL_DEVICE_FEATURES_VISUAL, "");
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Properties getPartCapabilities() {
		return partCapabilities;
	}
	public void setPartCapabilities(Properties partCapabilities) {
		this.partCapabilities = partCapabilities;
	}
	
	public void setPartBundle(String id, String version) {
		this.bundleId = id;
		this.bundleVersion = version;
	}
	
	public String getPartBundleId(){
		return this.bundleId;
	}
	
	public String getPartBundleVersion(){
		return this.bundleVersion;
	}
	
	public void setCapability(String name, String value){
		partCapabilities.put(name, value);
	}
	public List<Requirement> getPartRequirements() {
		if(partRequirements == null)
			partRequirements = new ArrayList<Requirement>();
		return partRequirements;
	}
	public DeploymentUnit getDeploymentUnit() {
		if(deploymentUnit == null)
			deploymentUnit = new DeploymentUnit();
		return deploymentUnit;
	}
	
	/*
	public List<ExecutionUnit> getExecutionUnits() {
		if(executionUnits == null)
			executionUnits = new ArrayList<ExecutionUnit>();
		return executionUnits;
	}
	 */
	
	public void setExecutionUnit(ExecutionUnit executionUnit){
		this.executionUnit = executionUnit;
	}
	
	public ExecutionUnit getExecutionUnit(){
		return executionUnit;
	}
	
	public String getXML(){

		String r = "";
		r = r.concat("<part partId='"+id+"'>");
		
		r = r.concat("<bundleId>"+this.bundleId+"</bundleId>");
		r = r.concat("<bundleVersion>"+this.bundleVersion+"</bundleVersion>");
		
		r = r.concat("<partCapabilities>");
		try{
			Enumeration<Object> cs = partCapabilities.keys();
			while(cs.hasMoreElements()){
				String key = (String) cs.nextElement();
				if(key != null){
					String value = (String) partCapabilities.get(key);
					if(value != null && !value.isEmpty())
						r = r.concat("<capability><name>"+key+"</name>"+"<value>"+value+"</value></capability>");
				}
			}
		}
		catch(Exception ex){}
		r = r.concat("</partCapabilities>");

		r = r.concat("<partRequirements>");
		for(int i = 0; i < getPartRequirements().size(); i++)
			r = r.concat("<requirement>"+partRequirements.get(i).getXML()+"</requirement>");
		r = r.concat("</partRequirements>");

		try{
			r = r.concat(deploymentUnit.getXML());
		} catch (Exception e){
			
		}
/*
		for(int i = 0; i < getExecutionUnits().size(); i++)
			r = r.concat(executionUnits.get(i).getXML());
*/
		if(executionUnit != null){
			r = r.concat(executionUnit.getXML());
		}
		
		r = r.concat("</part>");

		return r;
	}

	/*
	 * <xs:element name="part">
		<xs:complexType>
			<xs:sequence>
				<xs:element minOccurs="0" name="partCapabilities">
					<xs:complexType>
						<xs:sequence>
							<xs:element maxOccurs="unbounded" name="capability"
								type="uapp:capabilityType" />
						</xs:sequence>
					</xs:complexType>
				</xs:element>
				<xs:element minOccurs="0" name="partRequirements">
					<xs:complexType>
						<xs:sequence>
							<xs:element name="requirement" maxOccurs="unbounded"
								type="uapp:reqType" />
						</xs:sequence>
					</xs:complexType>
				</xs:element>
				<xs:element maxOccurs="unbounded" ref="uapp:deploymentUnit" />
				<xs:element maxOccurs="unbounded" minOccurs="0"
					ref="uapp:executionUnit" />
			</xs:sequence>
			<xs:attribute name="partId" type="xs:ID" />
		</xs:complexType>
	</xs:element>
	 */
}