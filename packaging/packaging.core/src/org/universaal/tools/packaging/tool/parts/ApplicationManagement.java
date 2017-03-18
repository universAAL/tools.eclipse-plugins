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
import java.util.List;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class ApplicationManagement implements Serializable {

	private String contact;
	private List<RemoteManagement> remoteManagement;

	public ApplicationManagement(){
		contact = Application.defaultString;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public List<RemoteManagement> getRemoteManagement() {
		if(remoteManagement == null)
			remoteManagement = new ArrayList<RemoteManagement>();
		return remoteManagement;
	}

	public String getXML(){

		String r = "";
		//r = r.concat("<applicationManagement>");
		r = r.concat("<contactPoint>"+contact+"</contactPoint>");

		r = r.concat("<remoteManagement>");
		for(int i = 0; i < getRemoteManagement().size(); i++)
			r = r.concat(remoteManagement.get(i).getXML());
		r = r.concat("</remoteManagement>");

		//r = r.concat("</applicationManagement>");

		return r;
	}

	public class RemoteManagement implements Serializable{

		private List<String> protocols;
		private Artifact software;

		public RemoteManagement(){
			software = new Artifact();
		}

		public List<String> getProtocols() {
			if(protocols == null)
				protocols = new ArrayList<String>();
			return protocols;
		}
		public Artifact getSoftware() {
			return software;
		}
		public void setSoftware(Artifact software) {
			this.software = software;
		}

		public String getXML(){

			String r = "";
			for(int i = 0; i< getProtocols().size(); i++)
				r = r.concat("<protocols>"+protocols.get(i)+"</protocols>");
			r = r.concat("<software>"+software.getXML()+"</software>");

			return r;
		}
	}

	/*
	 * <xs:element name="applicationManagement" minOccurs="0">
					<xs:complexType>
						<xs:sequence>
							<xs:element name="contactPoint" type="xs:string">
							</xs:element>
							<xs:element minOccurs="0" name="remoteManagement">
								<xs:complexType>
									<xs:sequence>
										<xs:element maxOccurs="unbounded" name="protocols"
											type="xs:string" />
										<xs:element name="software" type="uapp:artifactType" />
									</xs:sequence>
								</xs:complexType>
							</xs:element>
						</xs:sequence>
					</xs:complexType>
				</xs:element>
	 */
}