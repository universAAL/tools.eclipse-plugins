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
package org.universaal.tools.packaging.tool.gui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class RequirementsDefinitions {
	private static RequirementsDefinitions instance = null;
	
	private Document document;
	private XPathFactory xpf = null;
    private XPath xp = null;
    
    private RequirementsDefinitions(){
    	try{
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            InputStream is = getClass().getResourceAsStream("/org/universaal/tools/packaging/tool/xml/requirements.xml");
            
            document = db.parse(is);
            xpf = XPathFactory.newInstance();
            xp = xpf.newXPath();
            
    	} catch (Exception e) {
    		e.printStackTrace();
    	}    	
    }
    
    public static synchronized RequirementsDefinitions get() {
		if (instance == null) {
			instance = new RequirementsDefinitions();
		}
		return instance;
	}
   
    public List<String> listRequirements(String name){
    	List<String> returnList = new ArrayList<String>();
    	
    	try {
			NodeList nodes = (NodeList) xp.evaluate("//requirements/requirement[@name='"+name+"']/values/value",document.getDocumentElement(),XPathConstants.NODESET);
			if(nodes != null){
				for (int i = 0; i < nodes.getLength(); i++) {
					returnList.add(nodes.item(i).getTextContent());
				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return returnList;
    }
}
