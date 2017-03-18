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

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class DeploymentUnit implements Serializable {
	
	public final static String OS = "OS";
	public final static String PLATFORM = "Platform";
	public final static String CONTAINER = "Container";
	
	private String id;
	private String unit;
	private String type;
	private ContainerUnit cu;

	public DeploymentUnit(){		
		this.id = null;
		this.type = null;
		this.unit = null;
		this.cu = null;
	}
	
	public DeploymentUnit(String id, String unit, String type){		

		this.id = id;
		this.type = type;
		this.unit = unit;
		this.cu = null;
	}

	public void setDeploymentUnit(String id, String unit, String type){		

		this.id = id;
		this.type = type;
		this.unit = unit;
		this.cu = null;
	}

	public DeploymentUnit(String id, ContainerUnit cu){		

		this.id = id;
		this.type = CONTAINER;
		this.unit = null;
		this.cu = cu;
	}

	public void setDeploymentUnit(String id, ContainerUnit cu){		

		this.id = id;
		this.type = CONTAINER;
		this.unit = null;
		this.cu = cu;
	}

	public String getId() {
		return id;
	}

	public String getUnit() {
		return unit;
	}

	public String getType() {
		return type;
	}

	public ContainerUnit getCu() {
		return cu;
	}

	public String getXML(){

		String r = "";

		//r = r.concat("<deploymentUnit><id>"+id+"</id>");
		r = r.concat("<deploymentUnit id='"+id+"'>");
		if(cu != null)
			r = r.concat("<containerUnit>"+cu.getXML()+"</containerUnit>");
		else if(type == OS)
			r = r.concat("<osUnit>"+unit+"</osUnit>");
		else if(type == PLATFORM)
			r = r.concat("<platformUnit>"+unit+"</platformUnit>");

		r = r.concat("</deploymentUnit>");
		return r;
	}

	/*
	 * <xs:element name="deploymentUnit">
		<xs:complexType>
			<xs:choice>
				<xs:element name="osUnit" type="uapp:osType">
				</xs:element>
				<xs:element name="platformUnit" type="uapp:platformType">
				</xs:element>
				<xs:element name="containerUnit">
					<xs:complexType>
						<xs:choice>
							<xs:element name="karaf">
								<xs:complexType>
									<xs:sequence>
										<xs:element name="embedding" type="uapp:embeddingType" />
										<xs:element ref="krf:features" />
									</xs:sequence>
								</xs:complexType>
							</xs:element>
							<xs:element name="android">
								<xs:complexType>
									<xs:sequence>
										<xs:element name="name" type="xs:string" />
										<xs:element minOccurs="0" name="description" type="xs:string" />
										<xs:element maxOccurs="unbounded" name="location"
											type="xs:anyURI" />
									</xs:sequence>
								</xs:complexType>
							</xs:element>
							<xs:element name="tomcat" />
							<xs:element name="equinox" />
							<xs:element name="felix" />
							<xs:element name="osgi-android" />
						</xs:choice>
					</xs:complexType>
				</xs:element>
			</xs:choice>
			<xs:attribute name="id" type="xs:ID" />
		</xs:complexType>
	</xs:element>
	 */
}