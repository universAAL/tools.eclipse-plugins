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

public class Version implements Serializable {

	private String major, minor, micro, build;

	public Version(){
		major = Application.defaultString;
		minor = Application.defaultString;
		micro = Application.defaultString;
		build = Application.defaultString;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	public String getMicro() {
		return micro;
	}

	public void setMicro(String micro) {
		this.micro = micro;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getVersion(){
		if(!major.isEmpty() && !minor.isEmpty() && !micro.isEmpty() && !build.isEmpty())
			return major+"."+minor+"."+micro+"-"+build;

		return Application.defaultVersion;
	}

	public void setVersion(String v){

		try{
			if(v != null && !v.isEmpty()){
				String[] vs = v.split("\\.");
				if(vs.length > 0){
					major = vs[0];
					if(vs[1] != null)
						minor = vs[1];
					if(vs[2] != null){
						micro = vs[2];
						String[] snapshot = vs[2].split("-");
						if(snapshot != null && snapshot.length > 0){
							micro = snapshot[0];
							if(snapshot[1] != null)
								build = snapshot[1];
						}
						else if(vs[3] != null)
							build = vs[3];
					}
				}
				else
					major = v;
			}
		}
		catch(Exception ex){}
	}

	public String getXML(){
		return "<major>"+major+"</major>"+"<minor>"+minor+"</minor>"+"<micro>"+micro+"</micro>"+"<build>"+build+"</build>";
	}

	/*
	 * <xs:element default="0" name="major" type="xs:int" />
			<xs:element default="0" name="minor" type="xs:int" />
			<xs:element default="0" name="micro" type="xs:int" />
			<xs:element minOccurs="0" name="build" type="xs:string">
				<xs:annotation>
					<xs:documentation>e.g. major.minor.micro-build</xs:documentation>
				</xs:annotation>
			</xs:element>
	 * 
	 */
}