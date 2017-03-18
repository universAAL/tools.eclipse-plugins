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

public class LicenseSet implements Serializable{

	private SLA sla;
	private List<License> licenseList;

	public LicenseSet(){
		sla = new SLA();
	}

	public SLA getSla() {
		return sla;
	}
	public void setSla(SLA sla) {
		this.sla = sla;
	}
	public List<License> getLicenseList() {
		if(licenseList == null)
			licenseList = new ArrayList<License>();
		return licenseList;
	}

	public void setLicenseList(List<License> licenseList) {
		this.licenseList = licenseList;
	}

	public String getXML(){

		String r = "";
		r = r.concat("<licenses>");
		for(int i = 0; i< licenseList.size(); i++){
			if(!licenseList.get(i).getLink().toASCIIString().trim().isEmpty())
				r = r.concat("<license>"+licenseList.get(i).getXML()+"</license>");
		}
		r = r.concat(sla.getXML());
		r = r.concat("</licenses>");

		return r;
	}
}