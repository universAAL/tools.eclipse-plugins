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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.cli.event.ExecutionEventLogger;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
//import org.codehaus.plexus.logging.Logger;
//import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.universaal.tools.packaging.tool.api.Page;
import org.universaal.tools.packaging.tool.gui.GUI;
import org.universaal.tools.packaging.tool.preferences.EclipsePreferencesConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class links the eclipse project to Maven and it used for generating the features 
 * file, by means of executing the Karaf's Maven plugin (e.g. features-maven-plugin)
 * <br>
 * <ul><b>Karaf's Maven plugin reference</b>
 * <li> Karaf 2.X: 
 * 	<a href="https://svn.apache.org/repos/asf/karaf/tags/karaf-2.3.1/tooling/features-maven-plugin/">
 * 		features-maven-plugin
 * 	</a>
 * </li>  
 * <li> Karaf 3.X: 
 * 	<a href="https://svn.apache.org/repos/asf/karaf/trunk/tooling/karaf-maven-plugin/">
 * 		karaf-maven-plugin
 * 	</a>
 * </li>
 * </ul>
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class KarafFeaturesGenerator {

    
	// add to pom.xml of project to add to Karaf container the following declaration:
	/*
	 * <plugin>
        <groupId>org.apache.karaf.tooling</groupId>
        <artifactId>features-maven-plugin</artifactId>
        <version>2.2.7</version>

        <executions>
          <!-- add execution definitions here -->
        </executions>
      </plugin>
	 */

	// run mvn:features-generate-xml

	// parse feature.xml in {project}/target/classes/ to obtain FeaturesRoot

	private GUI g = GUI.getInstance();
	private MavenExecutionResult execution_result;

	private final String GROUP_ID = EclipsePreferencesConfigurator.local.getKarafPluginGroupId();
	private final String ARTIFACT_ID = EclipsePreferencesConfigurator.local.getKarafPluginArtifactId();
	private final String VERSION = EclipsePreferencesConfigurator.local.getKarafPluginVersion(); 
	private final String GOAL_FEATURE = EclipsePreferencesConfigurator.local.getKarafPluginFeatureGoal();
	private final String GOAL_KARFILE = EclipsePreferencesConfigurator.local.getKarafPluginKarGoal();	
	private final Boolean OFFLINE_MODE = EclipsePreferencesConfigurator.local.isOfflineMode(); 
	private final int LOG_LEVEL = EclipsePreferencesConfigurator.local.getLogLevel();

	private final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	public String generate(IProject part, boolean createKar, int partNumber){		

		if(verifyPreConditions(part.getName()))
			if(generateKarafFeatures(part.getName())){	
				if(createKar)
					generateKarFile(part);
				return returnKrfFeat(part, partNumber);
			}

		String ret = "";
		if(execution_result != null && execution_result.getExceptions() != null)
			for(int i = 0; i < execution_result.getExceptions().size(); i++)
				ret = ret.concat(execution_result.getExceptions().get(i).getMessage()+"\n");
		DefaultLogger.getInstance().log("[Application Packager] - FATAL ERROR! The generation of Karaf features is failed: "+ret, 4);

		return "";
	}

	private boolean verifyPreConditions(String projectName){

		try{
			// it verifies the presence of "features-maven-plugin" in the pom.xml
			IProject project = g.getPart(projectName);
			File pom = new File(project.getFile("pom.xml").getLocation()+"");

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document parsedPom = docBuilder.parse(pom);
			parsedPom.getDocumentElement().normalize();

			Element build, plugins, maven_features_plugin, groupdID, artifactID, version;

			NodeList plug_ins = parsedPom.getElementsByTagName("plugin");
			for(int i = 0; i < plug_ins.getLength(); i++){
				NodeList plugins_children = plug_ins.item(i).getChildNodes();
				for(int j = 0; j < plugins_children.getLength(); j++){
					if(plugins_children.item(j).getNodeName().equals("artifactId"))
						if(plugins_children.item(j).getTextContent().equalsIgnoreCase(ARTIFACT_ID)){

							if(plugins_children.item(j).getNextSibling() != null){
								Node s = plugins_children.item(j).getNextSibling();
								if(s.getNodeName().equals("version") && s.getTextContent().equalsIgnoreCase(VERSION))
									return true;				
							}
						}
					//return true;
				}
			}
			// add features-maven-plugin declaration

			maven_features_plugin = parsedPom.createElement("plugin");
			groupdID = parsedPom.createElement("groupId");
			groupdID.setTextContent(GROUP_ID);
			artifactID = parsedPom.createElement("artifactId");
			artifactID.setTextContent(ARTIFACT_ID);
			version = parsedPom.createElement("version");
			version.setTextContent(VERSION);

			maven_features_plugin.appendChild(groupdID);
			maven_features_plugin.appendChild(artifactID);
			maven_features_plugin.appendChild(version);

			plugins = parsedPom.createElement("plugins");
			plugins.appendChild(maven_features_plugin);

			build = parsedPom.createElement("build");
			build.appendChild(plugins);

			NodeList pps, builds;
			if(parsedPom.getElementsByTagName("plugins") != null){
				pps = parsedPom.getElementsByTagName("plugins");
				if(pps.getLength() > 0)
					pps.item(0).appendChild(maven_features_plugin);
			}
			else if(parsedPom.getElementsByTagName("build") != null){
				builds = parsedPom.getElementsByTagName("build");
				if(builds.getLength() > 0)
					builds.item(0).appendChild(plugins);
			}
			else
				parsedPom.getDocumentElement().appendChild(build);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(parsedPom);
			StreamResult result = new StreamResult(pom);

			transformer.transform(source, result);

			return true;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}

		return false;
	}

	private boolean generateKarafFeatures(String projectName){

		try{
			IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
			IFile pomResource = g.getPart(projectName).getFile(IMavenConstants.POM_FILE_NAME);
			IMavenProjectFacade projectFacade = projectManager.create(pomResource, true, null);
			String ProjectPath = g.getPart(projectName).getLocation().toString();
			
			IMaven maven = MavenPlugin.getMaven();
			if(pomResource != null && projectFacade != null){
				
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceDescription description = workspace.getDescription();
				
				if(EclipsePreferencesConfigurator.local.runMavenEmbedded()){
					
					MavenExecutionRequest request = projectManager.createExecutionRequest(pomResource, projectFacade.getResolverConfiguration(), null);
					request.setLoggingLevel(EclipsePreferencesConfigurator.local.getLogLevel());
					
					DefaultLogger.getInstance().log(
						"Preparing to run maven, the log level was:" + request.getLoggingLevel() + 
						" but we increased to "+MavenExecutionRequest.LOGGING_LEVEL_DEBUG 
					);
					
					request.setLoggingLevel( LOG_LEVEL );
					if ( request.isOffline() && OFFLINE_MODE ) {
					    DefaultLogger.getInstance().log("Maven was configured to work OFFLINE, that is fine");
					} else if ( OFFLINE_MODE ){
					    DefaultLogger.getInstance().log("Maven was configured to work ONLINE, " +
					    		"but we are using it OFFLINE for speed it up");
					    request.setOffline(true);
					}
					
					ExecutionListener listener = request.getExecutionListener();
					if ( listener != null ) {
					    DefaultLogger.getInstance().log(
						    "The following ExecutionListener was set " + listener 
						    + " but is going to be replaced"
						    );
					} else {
					    DefaultLogger.getInstance().log("NO ExecutionListener was set, creating one");
					}
					
					Logger logger = LoggerFactory.getLogger( KarafFeaturesGenerator.class );
					//ConsoleLogger logger = new ConsoleLogger(Logger.LEVEL_DEBUG,"MavenLogger");
					ExecutionEventLogger execLogger = new ExecutionEventLogger(logger);
					request.setExecutionListener(execLogger);
	
					List<String> goals = new ArrayList<String>();
					Properties props = new Properties();
					
					DefaultLogger.getInstance().log("[Application Packager] - Preparing for Karaf features file generation...", 1);
					if (!description.isAutoBuilding()){
						DefaultLogger.getInstance().log("[Application Packager] - "+projectName+" will be compiled now because autobuilding is off.", 1);
						goals.add("compiler:compile"); // compile it if autobuilding is off
						request.setGoals(goals);
						request.setUserProperties(props);
						execution_result = maven.execute(request, null);
						DefaultLogger.getInstance().log("[Application Packager] - Compiling operation ended.", 1);
					}
	
					DefaultLogger.getInstance().log("[Application Packager] - Generating Karaf features file...", 1);
					goals.clear();
					props = new Properties();
	
					goals.add(GROUP_ID + ":" + ARTIFACT_ID + ":" + VERSION + ":" + GOAL_FEATURE);
					request.setGoals(goals);
					request.setUserProperties(props);
					execution_result = maven.execute(request, null);
	
					if(execution_result.getExceptions() == null || execution_result.getExceptions().isEmpty()){
						DefaultLogger.getInstance().log("[Application Packager] - Karaf features file generated successfully.", 1);
						return true;
					} else{
						DefaultLogger.getInstance().log("[Application Packager] - Karaf features file not generated because of errors:", 3);
						for(int i = 0; i < execution_result.getExceptions().size(); i++)
							DefaultLogger.getInstance().log("[Application Packager] - "+execution_result.getExceptions().get(i).getMessage(), 1);
					}
				
				} else {
				
					int exitLevel = 0;
					
					DefaultLogger.getInstance().log("[Application Packager] - Preparing for Karaf features file generation...", 1);
					if (!description.isAutoBuilding()){
						DefaultLogger.getInstance().log("[Application Packager] - "+projectName+" will be compiled now because autobuilding is off.", 1);
						exitLevel = ProcessExecutor.runMavenCommand("compiler:compile", ProjectPath);
						DefaultLogger.getInstance().log("[Application Packager] - Compiling operation ended.", 1);
						if(exitLevel != 0){
							DefaultLogger.getInstance().log("[WARNING] - Error occurred during compiling operation.", 3);
						}
					}
					
					DefaultLogger.getInstance().log("[Application Packager] - Generating Karaf features file...", 1);
					exitLevel = ProcessExecutor.runMavenCommand(GROUP_ID + ":" + ARTIFACT_ID + ":" + VERSION + ":" + GOAL_FEATURE,ProjectPath);
					if(exitLevel == 0){
						DefaultLogger.getInstance().log("[Application Packager] - Karaf features file generated successfully.", 1);
						return true;
					} else {
						DefaultLogger.getInstance().log("[Application Packager] - FATAL ERROR!!! Karaf features file not generated because of errors", 4);
					}
					
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}

		return false;
	}

	private void generateKarFile(IProject part){

		try{
			DefaultLogger.getInstance().log("Generate kar for "+part.getName());
			String path = part.getLocation().toString(); //ResourcesPlugin.getWorkspace().getRoot().getLocation().makeAbsolute()+"/"+part.getDescription().getName();
			DefaultLogger.getInstance().log("location:"+path);
			File target, feature;
			target = new File(path+"/target");
			feature = new File(path+"/target/feature");
			if(!target.exists())
				target.mkdir();
			if(!feature.exists())
				feature.mkdir();

			copyFile(new File(path+"/target/classes/feature.xml"), new File(path+"/target/feature/feature.xml"));

			IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
			IFile pomResource = g.getPart(part.getName()).getFile(IMavenConstants.POM_FILE_NAME);
			IMavenProjectFacade projectFacade = projectManager.create(pomResource, true, null);

			IMaven maven = MavenPlugin.getMaven();
			if(pomResource != null && projectFacade != null){
				
				if(EclipsePreferencesConfigurator.local.runMavenEmbedded()){

					MavenExecutionRequest request = projectManager.createExecutionRequest(pomResource, projectFacade.getResolverConfiguration(), null);
					request.setLoggingLevel(EclipsePreferencesConfigurator.local.getLogLevel());
					
					List<String> goals = new ArrayList<String>();
					Properties props = new Properties();
					
					goals.add(GROUP_ID + ":" + ARTIFACT_ID + ":" + VERSION + ":" + GOAL_KARFILE);
	
					request.setGoals(goals);
					request.setUserProperties(props);
					request.setOffline(true);
					maven.execute(request, null);

				} else {
					
					int exitLevel = ProcessExecutor.runMavenCommand(GROUP_ID + ":" + ARTIFACT_ID + ":" + VERSION + ":" + GOAL_KARFILE, path);
					if(exitLevel == 0){
						DefaultLogger.getInstance().log("[Application Packager] - Kar file generated successfully.", 1);
					}
					else{
						DefaultLogger.getInstance().log("[Application Packager] - FATAL ERROR!!! Kar file not generated because of errors.", 4);
					}
					
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void copyFile(File source, File destination){

		try{
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(destination);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private String returnKrfFeat(IProject part, int partNumber){

		String xml = "";
		try {

			String path = part.getLocation().toString(); // ResourcesPlugin.getWorkspace().getRoot().getLocation().makeAbsolute()+"/"+part.getDescription().getName();
			File features = new File(path+"/target/classes/feature.xml");

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document parsedPom = docBuilder.parse(features);
			parsedPom.getDocumentElement().normalize();

			Map<String, Element> dps = new HashMap<String, Element>();
			NodeList fts = parsedPom.getElementsByTagName("feature");
			for(int i = 0; i < fts.getLength(); i++){
				if(fts.item(i).getNodeType() == Node.ELEMENT_NODE){
					Element item = (Element) fts.item(i);
					if(item.getAttribute("name") != null && !item.getAttribute("name").isEmpty())
						dps.put(item.getTextContent(), item);
				}
			}

			// feature.xml modifications
			BufferedReader reader = new BufferedReader(new FileReader(features));
			String line = "", oldtext = "";
			while((line = reader.readLine()) != null)
				oldtext += line + "\r\n";

			reader.close(); 

			// add feature of current part: 
			/*
			 * <feature name="Help-when-outdoor-servlet"
								description="Servlet part of HWO Service"
								version="0.1" resolver="(obr)">
								<feature>universAAL2.0</feature>
								<bundle start-level="85" start="false">
									file://../bin/part1/hwo.servlet_1.2.1.SNAPSHOT.jar</bundle>
							</feature>
			 */
			POMParser p = new POMParser(new File(part.getFile("pom.xml").getLocation()+""));
			String fileName = p.getArtifactId()+"-"+p.getVersion()+".jar";
			partNumber++;
			String thisPart = "<feature name='"+p.getName()+"' description='"+p.getDescription()+"' version='"+p.getVersion()+"' resolver=''>";

			Iterator<Element> it = dps.values().iterator();
			while(it.hasNext()){
				Element current = it.next();
				String f = "<feature version='"+current.getAttribute("version")+"'>"+current.getAttribute("name")+"</feature>";
				thisPart = thisPart.concat(f);
			}

			thisPart = thisPart.concat(/*"<feature>"+p.getDescription()+"</feature>" +*/
					"<bundle start-level='0' start='false'>"+"file://../bin/part"+partNumber+"/"+fileName+"</bundle>" +
					"</feature>" +
					"</features>");
			oldtext = oldtext.replace("</features>", thisPart);

			xml = oldtext.substring(XML_HEADER.length(), oldtext.length()); // remove XML header
			xml = xml.replaceAll("<", "<"+Page.KARAF_NAMESPACE+":"); // add KARAF namespace
			xml = xml.replaceAll("<"+Page.KARAF_NAMESPACE+":/", "</"+Page.KARAF_NAMESPACE+":"); // correct end tags

		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return xml;
	}
}