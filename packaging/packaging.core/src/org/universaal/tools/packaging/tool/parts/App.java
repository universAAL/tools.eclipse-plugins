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
public class App implements Serializable {

	private String name, appID, description, applicationProfile = "org.universAAL.ontology.profile.AALAppSubProfile";
	private Version version;
	private boolean multipart;
	private String tags;
	private Contact applicationProvider;
	//private List<LicenseSet> licenses;
	private LicenseSet licenses;
	private MenuEntry menuEntry;
	
	public App(){
		name  = Application.defaultString;
		appID = Application.defaultString;
		description = Application.defaultString;
		tags = Application.defaultString;

		version = new Version();
		applicationProvider = new Contact();
		menuEntry = new MenuEntry();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApplicationProfile() {
		return applicationProfile;
	}

	public void setApplicationProfile(String applicationProfile) {
		this.applicationProfile = applicationProfile;
	}

	public Version getVersion() {
		return version;
	}

	public boolean isMultipart() {
		return multipart;
	}

	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Contact getApplicationProvider() {
		return applicationProvider;
	}
	
	public MenuEntry getMenuEntry(){
		return menuEntry;
	}
	
	public LicenseSet getLicenses() {
		if(licenses == null)
			licenses = new LicenseSet();
		return licenses;
	}
	
	public void setLicenses(LicenseSet licenses) {
		this.licenses = licenses;
	}
	
	public String getXML(){

		String r = "";
		//r = r.concat("<app>");
		r = r.concat("<name>"+name+"</name>");
		r = r.concat("<version>"+version.getXML()+"</version>");
		r = r.concat("<appId>"+appID+"</appId>");
		r = r.concat("<description>"+description+"</description>");
		r = r.concat("<multipart>"+multipart+"</multipart>");
		r = r.concat("<tags>"+tags+"</tags>");
		r = r.concat("<applicationProvider>"+applicationProvider.getXML()+"</applicationProvider>");
		/*for(int i = 0; i < getLicenses().size(); i++)
			r = r.concat(licenses.get(i).getXML());
			*/
		r = r.concat(licenses.getXML());
		r = r.concat("<applicationProfile>"+applicationProfile+"</applicationProfile>");
		if(menuEntry.getMenuName().trim().length() > 0) r = r.concat("<menuEntry>"+menuEntry.getXML()+"</menuEntry>");
		//r = r.concat("</app>");

		return r;
	}
}