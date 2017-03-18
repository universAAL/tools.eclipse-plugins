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
package org.universaal.tools.packaging.tool.api;

import org.universaal.tools.packaging.tool.parts.MPA;

/**
 * The Page interface
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public interface Page {

	public final String PAGE_START = "universAAL Application Packager";
	public final String PAGE1 = "Application details";
	public final String PAGE2 = "Contacts";
	public final String PAGE3 = "Application capabilities";
	public final String PAGE4 = "Application requirements";
	public final String PAGE5 = "Application management";
	public final String PAGE_DU = "Deployment Unit";
	public final String PAGE_APP_RESOURCES = "Application Resources";
	public final String PAGE_LICENSE = "SLA and licenses";
	public final String PAGE_PART_BUNDLE = "Application Part (Bundle Id and version - 1/5): ";
	public final String PAGE_PART_DU = "Application Part (Deployment Unit - 2/5): ";
	public final String PAGE_PART_EU = "Application Part (Execution Unit - 3/5): ";
	public final String PAGE_PART_PC = "Application Part (Part Capabilities - 4/5): ";
	public final String PAGE_PART_PR = "Application Part (Part Requirements - 5/5): ";
	public final String PAGE_END = "universAAL Application Packager";
	
	public final String KARAF_NAMESPACE = "krf";
	
	//public final String XSD = "'http://www.universaal.org/aal-uapp/v1.0.2'"; // "'http://www.universaal.org/aal-uapp/v1.0.0'" - //"http://www.universaal.org/aal-uapp/v1.0.0/AAL-UAPP.xsd";
	public final String XSD_REPOSITORY = "http://www.universaal.org/aal-uapp/";
	public final String XSD_VERSION = "1.0.2";
	public final String Karaf = "'http://karaf.apache.org/xmlns/features/v1.0.0'";
	public final String w3c = "'http://www.w3.org/2001/XMLSchema'";
	
	public final String HEADER_DESCRIPTOR = "<?xml version='1.0' encoding='UTF-8'?>" +
				"<aal-uapp xmlns='http://www.universaal.org/aal-uapp/v"+XSD_VERSION+"' " +
				"xmlns:"+KARAF_NAMESPACE+"="+Karaf+" " +
				"xmlns:xsi="+w3c+" " +
				"xsi:schemaLocation='http://www.universaal.org/aal-uapp/v"+XSD_VERSION+"'>";
	
	public final String PAGE_ERROR = "Error Page";
	
	public final String DESCRIPTOR_FILENAME_SUFFIX = "uapp.xml";

	public void setMPA(MPA mpa);

	public boolean validate();
}