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

public class RequirementsGroup implements Serializable {

	private LogicalRelation relation;
	private SingleRequirement req1, req2;

	public RequirementsGroup(SingleRequirement req1, SingleRequirement req2, LogicalRelation relation){
		this.req1 = req1;
		this.req2 = req2;
		this.relation = relation;
	}

	public LogicalRelation getRelation() {
		return relation;
	}
	public void setRelation(LogicalRelation relation) {
		this.relation = relation;
	}
	public SingleRequirement getReq1() {
		return req1;
	}
	public void setReq1(SingleRequirement req1) {
		this.req1 = req1;
	}
	public SingleRequirement getReq2() {
		return req2;
	}
	public void setReq2(SingleRequirement req2) {
		this.req2 = req2;
	}

	@Override
	public boolean equals(Object other){
		
		if(other == this)
			return true;

		if(other instanceof RequirementsGroup){

			RequirementsGroup req = (RequirementsGroup)other;
			if(req.getReq1().equals(this.req1) && req.getReq2().equals(this.req2) && req.getRelation().equals(this.relation))
				return true;
		}

		return false;
	}

	public String getXML(){
		return "<logicalRelation>"+relation.toString()+"</logicalRelation><requirement>"+req1.getXML()+"</requirement><requirement>"+req2.getXML()+"</requirement>";
	}

	/*
	 * <xs:complexType name="reqGroupType">
		<xs:sequence>
			<xs:element name="logicalRelation" type="uapp:logicalRelationType" />
			<xs:element maxOccurs="2" minOccurs="2" name="requirement"
				type="uapp:reqType" />
		</xs:sequence>
	</xs:complexType>
	 */
}