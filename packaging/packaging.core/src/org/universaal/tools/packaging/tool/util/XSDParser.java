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
package org.universaal.tools.packaging.tool.util;
  
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;    

import org.universaal.tools.packaging.tool.api.Page;
import org.w3c.dom.Document;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class XSDParser{

	private static XSDParser instance = null;
	
    private Document document = null;
    private XPathFactory xpf = null;
    private XPath xp = null;
    private static boolean online = true;
    
    private XSDParser(String XSD){
    	try{
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            if(Page.XSD_REPOSITORY.contains("http")){
            	if(checkOnline(XSD)){
            		document = db.parse(Page.XSD_REPOSITORY+"v"+XSD+"/AAL-UAPP.xsd");
            	}
            	else {
            		InputStream is = getClass().getResourceAsStream("/org/universaal/tools/packaging/tool/schemas/"+XSD+"/AAL-UAPP.xsd");
		            document = db.parse(is);
            	}
            } else {
            	InputStream is = getClass().getResourceAsStream(Page.XSD_REPOSITORY+XSD+"/AAL-UAPP.xsd");
	            document = db.parse(is);
            }
            xpf = XPathFactory.newInstance();
            xp = xpf.newXPath();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private boolean checkOnline(String XSD){
    	try {
    		URL url = new URL(Page.XSD_REPOSITORY+"v"+XSD+"/AAL-UAPP.xsd");
        	URLConnection con = url.openConnection();
    	    con.setReadTimeout( 5000 ); //5 seconds
    	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    	    in.close();
    	    online = true;
    	} catch (IOException e) {
    		online = false;
    	}
    	return online;
    }
    
    public static synchronized XSDParser get(String XSD) {
		if (instance == null) {
			instance = new XSDParser(XSD);
		} else {
			if(online == false){
				if(instance.checkOnline(XSD))
					instance = new XSDParser(XSD);
			}
		}
		
		return instance;
	}
    
    public String find(String what){
    	if(instance != null){
    		String model = "";
	    	String[] segments = what.split("\\.");
	    	if(segments.length <= 1){
	    		model = model + "//element[@name='"+what+"']//annotation/documentation/text()";
	    		model = model + "|//complexType[@name='"+what+"']//annotation/documentation/text()";
	    		model = model + "|//simpleType[@name='"+what+"']//annotation/documentation/text()";
				
			} else {
		    	model = model + "//element[@name='"+segments[0]+"']//element[@name='"+segments[1]+"']//annotation/documentation/text()";
		    	model = model + "|//complexType[@name='"+segments[0]+"']//element[@name='"+segments[1]+"']//annotation/documentation/text()";
		    	model = model + "|//simpleType[@name='"+segments[0]+"']//element[@name='"+segments[1]+"']//annotation/documentation/text()";
			}
	    	
	    	try {
				String text = xp.evaluate(model, document.getDocumentElement());
				return text;
			} catch (XPathExpressionException e) {
				e.printStackTrace();
				return "";
			}

    	} else return "";
    }
    
}