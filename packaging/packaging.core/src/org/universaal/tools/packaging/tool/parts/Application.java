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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class Application implements Serializable{

	public static final String defaultURL = "";
	public static final String defaultString = "";
	public static final String defaultFile = "";
	public static final String defaultVersion = "major.minor.micro.build";
	public static final String file_prefix = "file://../license/";

	private App application;
	private ApplicationCapabilities appCapabilities;
	private ApplicationRequirements appRequirements;
	private ApplicationManagement appManagement;
	private List<Part> appParts;
	private String mainPart = "";
	private File[] appResources = null;
	private String destination = "";
	private String currentPageTitle = "";
	
	public Application(){
		this.application = new App();
		this.appCapabilities = new ApplicationCapabilities();
		this.appRequirements = new ApplicationRequirements();
		this.appManagement = new ApplicationManagement();
	}

	public App getApplication() {
		return application;
	}
	public void setApplication(App application) {
		this.application = application;
	}
	public ApplicationCapabilities getAppCapabilities() {
		return appCapabilities;
	}
	public void setAppCapabilities(ApplicationCapabilities appCapabilities) {
		this.appCapabilities = appCapabilities;
	}
	public ApplicationRequirements getAppRequirements() {
		return appRequirements;
	}
	public void setAppRequirements(ApplicationRequirements appRequirements) {
		this.appRequirements = appRequirements;
	}
	public ApplicationManagement getAppManagement() {
		return appManagement;
	}
	public void setAppManagement(ApplicationManagement appManagement) {
		this.appManagement = appManagement;
	}
	
	public List<Part> getAppParts() {
		if(this.appParts == null)
			this.appParts = new ArrayList<Part>();
		return appParts;
	}

	public void setDestination(String destination){
		this.destination = destination;
	}

	public String getDestination(){
		return destination;
	}
	
	public void setAppResouces(File[] appResources){
		this.appResources = appResources;
	}
	
	public File[] getAppResouces(){
		return this.appResources;
	}
	
	public String getMainPart(){
		return mainPart;
	}
	
	public void setMainPart(String mainPart){
		this.mainPart = mainPart;
	}
	
	public void setCurrentPageTitle(String title){
		this.currentPageTitle = title;
	}
	
	public String getCurrentPageTitle(){
		return this.currentPageTitle;
	}
	
	public String getXML(){

		String r = "";

		r = r.concat("<app>"+application.getXML()+"</app>");
		r = r.concat("<applicationCapabilities>"+appCapabilities.getXML()+"</applicationCapabilities>");
		r = r.concat("<applicationRequirements>"+appRequirements.getXML()+"</applicationRequirements>");
		r = r.concat("<applicationManagement>"+appManagement.getXML()+"</applicationManagement>");
		r = r.concat("<applicationPart>");
		for(int i = 0; i < getAppParts().size(); i++)
			r = r.concat(appParts.get(i).getXML());
		r = r.concat("</applicationPart>");

		return r;
	}

	/*
	 <xs:element name="app">
	 ...
	 <xs:element minOccurs="0" name="applicationCapabilities">
	 ...
	 <xs:element minOccurs="0" name="applicationRequirements">
	 ...
	 <xs:element name="applicationManagement" minOccurs="0">
	 ...
	 <xs:element name="applicationPart">
	 ...
	 */
}