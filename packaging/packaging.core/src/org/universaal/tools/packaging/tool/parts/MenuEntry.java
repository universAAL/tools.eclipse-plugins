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

import java.io.File;
import java.io.Serializable;

import java.net.URI;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class MenuEntry implements Serializable {

	private String menuName;
	private URI serviceUri, iconPath;
	private File iconFile;
	private boolean iconScale = false, isCustomIcon = false;
	
	public MenuEntry(){

		menuName = Application.defaultString;
		
		try{
			serviceUri = URI.create(Application.defaultURL);
			iconPath = URI.create(Application.defaultURL);
		}
		catch(Exception ex){}
	}

	public MenuEntry(String menuName, URI serviceUri){

		menuName = this.menuName;
		serviceUri = this.serviceUri;
		
		try{
			iconPath = URI.create(Application.defaultURL);
		}
		catch(Exception ex){}
	}
	
	public MenuEntry(String menuName, URI serviceUri, URI iconPath){

		menuName = this.menuName;
		serviceUri = this.serviceUri;
		iconPath = this.iconPath;
	}

	

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		if(menuName.trim().length() > 0)
			this.menuName = menuName;
	}
	
	public URI getServiceUri() {
		return serviceUri;
	}

	public void setServiceUri(URI serviceUri) {
		if(serviceUri.toASCIIString().trim().length() > 0)
			this.serviceUri = serviceUri;
	}
	
	public URI getIconPath() {
		return iconPath;
	}

	public void setIconPath(URI iconPath) {
		if(iconPath.toASCIIString().trim().length() > 0){
			this.iconPath = iconPath;
		}
	}
	
	public File getIconFile() {
		return iconFile;
	}

	public void setIconFile(File iconFile) {
		this.iconFile = iconFile;
		String iconPath = Application.defaultURL+"bin/icon/"+iconFile.getName();
		setIconPath(URI.create(iconPath));
	}
	
	public boolean getIconScale(){
		return iconScale;
	}
	
	public void setIconScale(boolean iconScale){
		this.iconScale = iconScale;
	}
	
	public boolean isCustomIcon(){
		return isCustomIcon;
	}
	
	public void setIsCustomIcon(boolean isCustom){
		this.isCustomIcon = isCustom;
	}
	
	public String getXML(){

		String r = "";
		r = r.concat("<menuName>"+menuName+"</menuName>");
		r = r.concat("<serviceUri>"+serviceUri.toASCIIString()+"</serviceUri>");
		if(iconPath.toASCIIString().trim().length() > 0){
			if(!isCustomIcon){ 
				r = r.concat("<icon><path>"+iconPath.toASCIIString().trim()+"</path></icon>");
			} else {
				r = r.concat("<icon><name>"+iconPath.toASCIIString().trim().replace("icons.","").replace('.','/').replace("/png", ".png")+"</name></icon>");
			}
		}
		return r;
	}

	/*
	 * <xs:element name="menuEntry" minOccurs="0">
						<xs:annotation>
										<xs:documentation>basic info for the menu entries of the application</xs:documentation>
						</xs:annotation>
						<xs:complexType>
										<xs:sequence>
														<xs:element name="menuName" type="xs:string">
																		<xs:annotation>
																						<xs:documentation>name of the application to show in the menu</xs:documentation>
																		</xs:annotation>
														</xs:element>
														<xs:element name="serviceUri" type="xs:anyURI">
																		<xs:annotation>
																						<xs:documentation>service uri to start the application</xs:documentation>
																		</xs:annotation>
														</xs:element>
														<xs:element name="iconPath" type="xs:anyURI" minOccurs="0">
																		<xs:annotation>
																						<xs:documentation>path for the menu icon; please save in: /bin/icon/*.png</xs:documentation>
																		</xs:annotation>
														</xs:element>
										</xs:sequence>
						</xs:complexType>
		</xs:element>
	 */
}