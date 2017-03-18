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

public class Contact implements Serializable {

	private String organizationName, contactPerson, streetAddress, email, phone;
	private URI certificate, webAddress;
	private List<OtherChannel> otherChannels;

	public Contact(){

		organizationName = Application.defaultString;
		contactPerson = Application.defaultString;
		streetAddress = Application.defaultString;
		email = Application.defaultString;
		phone = Application.defaultString;

		try{
			certificate = URI.create(Application.defaultURL);
			webAddress = URI.create(Application.defaultURL);
		}
		catch(Exception ex){}
	}

	public Contact(URI certificate, URI webAddress){

		organizationName = Application.defaultString;
		contactPerson = Application.defaultString;
		streetAddress = Application.defaultString;
		email = Application.defaultString;
		phone = Application.defaultString;

		this.certificate = certificate;
		this.webAddress = webAddress;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public URI getCertificate() {
		return certificate;
	}

	public void setCertificate(URI certificate) {
		this.certificate = certificate;
	}

	public URI getWebAddress() {
		return webAddress;
	}

	public void setWebAddress(URI webAddress) {
		this.webAddress = webAddress;
	}

	public List<OtherChannel> getOtherChannels() {
		if(otherChannels == null)
			otherChannels = new ArrayList<OtherChannel>();

		return otherChannels;
	}

	public String getXML(){

		String certificate_string = "";
		if(certificate.getScheme() != null && certificate.getScheme().equalsIgnoreCase("file")){
			String[] splitted = certificate.toASCIIString().split("/"); 
			certificate_string = splitted[splitted.length-1];
		}
		else
			certificate_string = certificate.toASCIIString();

		String r = "";
		r = r.concat("<organizationName>"+organizationName+"</organizationName>");
		r = r.concat("<certificate>"+certificate_string+"</certificate>");
		r = r.concat("<contactPerson>"+contactPerson+"</contactPerson>");
		r = r.concat("<streetAddress>"+streetAddress+"</streetAddress>");
		r = r.concat("<email>"+email+"</email>");
		r = r.concat("<webAddress>"+webAddress.toASCIIString()+"</webAddress>");
		r = r.concat("<phone>"+phone+"</phone>");

		for(int i = 0; i < getOtherChannels().size(); i++)
			r = r.concat("<otherChannel>"+getOtherChannels().get(i).getXML()+"</otherChannel>");

		return r;
	}

	/*
	 * <xs:sequence>
			<xs:element minOccurs="0" name="organizationName" type="xs:string">
			</xs:element>
			<xs:element minOccurs="0" name="certificate" type="xs:anyURI" />
			<xs:element minOccurs="0" name="contactPerson" type="xs:string">
			</xs:element>
			<xs:element minOccurs="0" name="streetAddress" type="xs:string">
			</xs:element>
			<xs:element name="email" type="xs:string">
			</xs:element>
			<xs:element minOccurs="0" name="webAddress" type="xs:anyURI">
			</xs:element>
			<xs:element minOccurs="0" name="phone" type="xs:string">
			</xs:element>

			<xs:element minOccurs="0" name="otherChannel">
				<xs:complexType>
					<xs:sequence>
						<xs:element name="channelName" type="xs:string">
						</xs:element>
						<xs:element name="channelDetails" type="xs:string">
						</xs:element>
					</xs:sequence>
				</xs:complexType>
			</xs:element>
		</xs:sequence>
	 */
}