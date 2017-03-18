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

public class SingleRequirement implements Serializable {

	private String requirementName, requirementValue;
	private LogicalCriteria requirementCriteria;

	public SingleRequirement(String requirementName, String requirementValue, LogicalCriteria requirementCriteria){
		this.requirementName = requirementName;
		this.requirementValue = requirementValue;
		this.requirementCriteria = requirementCriteria;
	}

	public SingleRequirement(String requirementName, String requirementValue){
		this.requirementName = requirementName;
		this.requirementValue = requirementValue;
		this.requirementCriteria = LogicalCriteria.equal;
	}

	public String getRequirementName() {
		return requirementName;
	}
	public void setRequirementName(String requirementName) {
		this.requirementName = requirementName;
	}
	public String getRequirementValue() {
		return requirementValue;
	}
	public void setRequirementValue(String requirementValue) {
		this.requirementValue = requirementValue;
	}
	public LogicalCriteria getRequirementCriteria() {
		return requirementCriteria;
	}
	public void setRequirementCriteria(LogicalCriteria requirementCriteria) {
		this.requirementCriteria = requirementCriteria;
	}

	@Override
	public boolean equals(Object other){

		if(other == this)
			return true;

		if(other instanceof SingleRequirement){

			SingleRequirement req = (SingleRequirement)other;

			if(req.getRequirementCriteria().equals(requirementCriteria) &&
					req.getRequirementName().equals(requirementName) &&
					req.getRequirementValue().equals(requirementValue))
				return true;
		}

		return false;
	}

	public String getXML(){
		return "<reqAtomName>"+requirementName+"</reqAtomName>"+"<reqAtomValue>"+requirementValue+"</reqAtomValue>"+"<reqCriteria>"+requirementCriteria.toString()+"</reqCriteria>";
	}

	/*
	 * <xs:complexType name="reqAtomType">
		<xs:annotation>
			<xs:documentation>describes a simple requirement</xs:documentation>
		</xs:annotation>
		<xs:sequence>
			<xs:element name="reqAtomName" type="xs:string" />
			<xs:element maxOccurs="unbounded" name="reqAtomValue"
				type="xs:string" />
			<xs:element default="equal" minOccurs="0" name="reqCriteria"
				type="uapp:logicalCriteriaType">
				<xs:annotation>
					<xs:documentation>should be considered as &quot;equal&quot;, when
						the element is omitted</xs:documentation>
				</xs:annotation>
			</xs:element>
		</xs:sequence>
	</xs:complexType>
	 */
}
