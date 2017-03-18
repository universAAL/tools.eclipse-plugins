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

public class OtherChannel implements Serializable{

	private String channelName, channelDetails;

	public OtherChannel(){
		channelName = Application.defaultString;
		channelDetails = Application.defaultString;
	}
	
	public OtherChannel(String channelName, String channelDetails){
		this.channelName = channelName;
		this.channelDetails = channelDetails;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelDetails() {
		return channelDetails;
	}

	public void setChannelDetails(String channelDetails) {
		this.channelDetails = channelDetails;
	}

	public String getXML(){
		return "<channelName>"+channelName+"</channelName>"+"<channelDetails>"+channelDetails+"</channelDetails>";
	}
}