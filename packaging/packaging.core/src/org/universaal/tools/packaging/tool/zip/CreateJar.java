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
package org.universaal.tools.packaging.tool.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;

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

import org.universaal.tools.packaging.tool.gui.GUI;
import org.universaal.tools.packaging.tool.parts.Artifact;
import org.universaal.tools.packaging.tool.preferences.EclipsePreferencesConfigurator;
import org.universaal.tools.packaging.tool.util.DefaultLogger;
import org.universaal.tools.packaging.tool.util.EffectivePOMContainer;
import org.universaal.tools.packaging.tool.util.POMParser;
import org.universaal.tools.packaging.tool.util.ProcessExecutor;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class CreateJar {

	public boolean create(IProject part, int partNumber){

		GUI g = GUI.getInstance();
		String destination_path = g.getTempDir()+"/bin/part"+partNumber+"/";
		POMParser p = new POMParser(new File(part.getFile("pom.xml").getLocation()+""));			

		String sourcePath = part.getLocation().toString();
		
		EffectivePOMContainer.setDocument(part.getName());
		
		try{							
			String fileName = EffectivePOMContainer.getArtifactId()+"-"+EffectivePOMContainer.getVersion()+".jar";
			IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
			IFile pomResource = g.getPart(part.getName()).getFile(IMavenConstants.POM_FILE_NAME);
			IMavenProjectFacade projectFacade = projectManager.create(pomResource, true, null);
			String ProjectPath = g.getPart(part.getName()).getLocation().toString();
			
			IMaven maven = MavenPlugin.getMaven();
			if(pomResource != null && projectFacade != null){
				DefaultLogger.getInstance().log("[Application Packager] - Preparing for packaging "+part.getName()+" project...", 1);
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceDescription description = workspace.getDescription();
				
				if(EclipsePreferencesConfigurator.local.runMavenEmbedded()){
					
					MavenExecutionRequest request = projectManager.createExecutionRequest(pomResource, projectFacade.getResolverConfiguration(), null);
					request.setLoggingLevel(EclipsePreferencesConfigurator.local.getLogLevel());
					
					List<String> goals = new ArrayList<String>();
					Properties props = new Properties();
	
					if (!description.isAutoBuilding()){
						goals.add("compiler:compile"); // compile it if autobuilding is off
					}
					DefaultLogger.getInstance().log("[Application Packager] - Packaging "+part.getName()+" project...", 1);
					goals.add("package");
	
					request.setGoals(goals);
					request.setUserProperties(props);
					MavenExecutionResult execution_result = maven.execute(request, null);
					if(execution_result.getExceptions() != null && !execution_result.getExceptions().isEmpty()){
						for(int i = 0; i < execution_result.getExceptions().size(); i++){
							DefaultLogger.getInstance().log("[Application Packager] - Packaging ended with errors:.", 1);
							DefaultLogger.getInstance().log("[Application Packager] - ERROR: "+execution_result.getExceptions().get(i).getMessage(), 3);
						}
						return false;
					} else
						DefaultLogger.getInstance().log("[Application Packager] - Packaging ended successfully.", 1);
				} else {
					int exitLevel = 0;
					if (!description.isAutoBuilding()){
						DefaultLogger.getInstance().log("[Application Packager] - "+part.getName()+" will be compiled now because autobuilding is off.", 1);
						exitLevel = ProcessExecutor.runMavenCommand("compiler:compile", ProjectPath);
						DefaultLogger.getInstance().log("[Application Packager] - Compiling operation ended.", 1);
						if(exitLevel != 0){
							DefaultLogger.getInstance().log("[WARNING] - Error occurred during compiling operation.", 2);
							return false;
						}
					}
					
					DefaultLogger.getInstance().log("[Application Packager] - Packaging "+part.getName()+" project...", 1);
					
					exitLevel = ProcessExecutor.runMavenCommand("package",ProjectPath);
					if(exitLevel == 0){
						DefaultLogger.getInstance().log("[Application Packager] - Packaging ended successfully.", 1);
					} else {
						DefaultLogger.getInstance().log("[Application Packager] - Packaging ended with errors.", 1);
						return false;
					}
				}
				DefaultLogger.getInstance().log("[Application Packager] - Copying: "+sourcePath+"/target/"+fileName+" -> " + destination_path+fileName, 1);
				copyFile(new File(sourcePath+"/target/"+fileName), new File(destination_path+fileName));
			}	
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}

		try{
			//if file KAR is present, add it to partX folder

			String fileName = EffectivePOMContainer.getArtifactId()+"-"+EffectivePOMContainer.getVersion()+".kar";
			File kar = new File(sourcePath+"/target/"+fileName);
			if(kar.exists()){
				DefaultLogger.getInstance().log("[Application Packager] - Copying: "+sourcePath+"/target/"+fileName+" -> " + destination_path+fileName, 1);
				copyFile(kar, new File(destination_path+fileName));
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}

	//	private void add(File source, JarOutputStream target, String startFrom){
	//
	//		BufferedInputStream in = null;
	//		try{			
	//			if (source.isDirectory()){
	//
	//				String name = source.getPath().replace("\\", "/");
	//				name = name.replace(startFrom, "");
	//
	//				if (!name.isEmpty()){
	//
	//					if (!name.endsWith("/"))
	//						name += "/";
	//
	//					JarEntry entry = new JarEntry(name);
	//					entry.setTime(source.lastModified());
	//					target.putNextEntry(entry);
	//					target.closeEntry();
	//				}
	//				for (File nestedFile: source.listFiles())
	//					add(nestedFile, target, startFrom);
	//				return;
	//			}
	//
	//			JarEntry entry = new JarEntry(source.getPath().replace("\\", "/").replace(startFrom, ""));
	//			entry.setTime(source.lastModified());
	//			target.putNextEntry(entry);
	//			in = new BufferedInputStream(new FileInputStream(source));
	//
	//			byte[] buffer = new byte[1024];
	//			while (true)
	//			{
	//				int count = in.read(buffer);
	//				if (count == -1)
	//					break;
	//				target.write(buffer, 0, count);
	//			}
	//			target.closeEntry();
	//		}
	//		catch(Exception ex){
	//			ex.printStackTrace();
	//		}
	//		finally{
	//			if (in != null)
	//				try {
	//					in.close();
	//				} catch (IOException e) {
	//					e.printStackTrace();
	//				}
	//		}
	//	}

	private void copyFile(File source, File destination) throws Exception{

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
}