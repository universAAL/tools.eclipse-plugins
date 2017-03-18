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

import java.net.URI;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class ApplicationRequirements implements Serializable {

	private List<Requirement> requirementsList;
	public String deploymentUnitType = DeploymentUnit.CONTAINER;
	public String OS_Requirements = "";
	public String Platform_Requirement = "";
	public String Container_Name = "";
	public String embedding = Embedding.anyContainer.toString();
	public Android android = new Android("", "", URI.create(""));
	
	public List<Requirement> getRequirementsList() {

		if(requirementsList == null)
			requirementsList = new ArrayList<Requirement>();

		/* CAUTION - REQUIRED BECAUSE OF METHODS THAT OVERRIDE VALUES WITH NULL */
		List<Requirement> ret = new ArrayList<Requirement>();
		for(int i = 0; i < requirementsList.size(); i++)
			if(requirementsList.get(i) != null){
				ret.add(requirementsList.get(i));
			}

		requirementsList.clear();
		for(int i = 0; i < ret.size(); i++)
			requirementsList.add(ret.get(i));
		/* CAUTION - REQUIRED BECAUSE OF METHODS THAT OVERRIDE VALUES WITH NULL */

		return requirementsList;
	}
	
	public void clear(){
		requirementsList = null;
	}
	
	public String getXML(){

		String r = "";

		for(int i = 0; i < getRequirementsList().size(); i++)
			r = r.concat("<requirement>"+getRequirementsList().get(i).getXML()+"</requirement>");

		return r;
	}

	/*
	 * <xs:element minOccurs="0" name="applicationRequirements">
					<xs:complexType>
						<xs:sequence>
							<xs:element name="requirement" maxOccurs="unbounded"
								type="uapp:reqType" />
						</xs:sequence>
					</xs:complexType>
				</xs:element>
	 */
}