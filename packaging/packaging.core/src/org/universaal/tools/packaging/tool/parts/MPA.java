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

import org.universaal.tools.packaging.tool.api.Page;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class MPA implements Serializable {

	private Application aal_uapp;
	
	public MPA(){
		aal_uapp = new Application();
	}
	
	public void setApplication(Application app){
	    aal_uapp = app;
	}

	public String getXML(){

		String r = "";

		r = r.concat(Page.HEADER_DESCRIPTOR);

		r = r.concat(aal_uapp.getXML());
		r = r.concat("</aal-uapp>");

		return r;
	}

	public Application getAAL_UAPP() {
		return aal_uapp;
	}

	public void setAAL_UAPP(Application application) {
		this.aal_uapp = application;
	}
	
}