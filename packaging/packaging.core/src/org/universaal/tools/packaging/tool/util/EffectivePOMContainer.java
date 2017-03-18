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
  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;    

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class EffectivePOMContainer{
	
	private static HashMap<String, Document> EffectivePOMSDocuments = new HashMap<String, Document>();
    
	private static XPathFactory xpf = XPathFactory.newInstance();
	private static XPath xp = xpf.newXPath();
    private static String currentDocument = "";
    	
    private static Document getDocument(String name){
    	if(EffectivePOMSDocuments.get(name) != null)
    		return EffectivePOMSDocuments.get(name);
    	return null;
    }
    
    public static void addDocument(String name, String xml){
    	try{
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            Document document = db.parse(xml);
            if(document != null) 
            	EffectivePOMSDocuments.put(name, document);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void setDocument(String name){
    	currentDocument = name;
    }
    
    private static String getValue(String key){
    	
    	if(getDocument(currentDocument) == null){
    		return "";
    	}
    	
        String result = "";
        
		try {
			result = (String) xp.evaluate(key, getDocument(currentDocument).getDocumentElement());
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
    	
    	return result;
    }

	private static NodeList getValues(String key){
		
	if(getDocument(currentDocument) == null) return null;
		
		NodeList result = null;
		
		try {
	    	result = (NodeList) xp.evaluate(key, getDocument(currentDocument).getDocumentElement(),XPathConstants.NODESET);
	   	} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		return result;
	}
    
    public static String getGroupId() {
		return getValue("//project/groupId/text()");
	}
	public static String getArtifactId() {
		return getValue("//project/artifactId/text()");
	}
	public static String getPackaging() {
		return getValue("//project/packaging/text()");
	}
	public static String getVersion() {
		return getValue("//project/version/text()");
	}
	public static String getClassifier(){
		return getValue("//project/classifier/text()");
	}
	public static String getName() {
		return getValue("//project/name/text()");
	}
	public static String getDescription() {
		return getValue("//project/description/text()");
	}
	public static POM_Organization getOrganization() {
		POM_Organization org = new POM_Organization();
		org.name = getValue("//project/organization/name/text()");
		org.url = getValue("//project/organization/url/text()");
		return org;
	}
	public static String getBundleId() {
		return getValue("//project//Bundle-SymbolicName/text()");
	}
	public static String getBundleVersion() {
		return getValue("//project//Bundle-Version/text()");
	}
	public static List<POM_License> getLicenses(){
		List<POM_License> returnList = new ArrayList<POM_License>(); 
		NodeList nodes = getValues("/projects/project[1]/licenses/license|/project/licenses/license");
		
		if (nodes != null){
			
			for (int i = 0; i < nodes.getLength(); i++) {
				Element el =  (Element) nodes.item(i);
				POM_License pl = new POM_License();
				try {
					pl.name = xp.evaluate("name/text()", el);
					pl.url = xp.evaluate("url/text()", el);
					pl.distribution = xp.evaluate("distribution/text()", el);
					pl.comments = xp.evaluate("comments/text()", el);
					
					returnList.add(pl);
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return returnList;
	}
	public static List<POM_Dependency> getDependencies(){
		List<POM_Dependency> returnList = new ArrayList<POM_Dependency>(); 
		NodeList nodes = getValues("//project/dependencies/dependency");
		if (nodes != null){
			
			for (int i = 0; i < nodes.getLength(); i++) {
				Element el =  (Element) nodes.item(i);
				POM_Dependency pc = new POM_Dependency();
				try {
					pc.groupId = xp.evaluate("groupId/text()", el);
					pc.artifactId = xp.evaluate("artifactId/text()", el);
					pc.version = xp.evaluate("version/text()", el);
					pc.classifier = xp.evaluate("classifier/text()", el);
					pc.type = xp.evaluate("type/text()", el);
					pc.scope = xp.evaluate("scope/text()", el);
					pc.systemPath = xp.evaluate("systemPath/text()", el);
					pc.optional = xp.evaluate("optional/text()", el);
					
					returnList.add(pc);
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return returnList;
	}
}