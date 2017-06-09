
/*
	Copyright 2011 SINTEF, http://www.sintef.no
	
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
package org.universaal.tools.transformationcommand.handlers;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

@SuppressWarnings("restriction")
public class TransformOntJava2OWL extends AbstractHandler {
	
	@SuppressWarnings("unused")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		IProject project = getSelectedProject(event, window);
		if (project == null) {
			MessageDialog.openInformation(
					window.getShell(),
					"Wrong selection", "Please select a project to transform to OWL!"
					);
			return null;
		}
		
		// check if flag for OWLSupport is given
		try {
			String generateOWLSupport = project.getPersistentProperty(new QualifiedName("generateJavaToOWL", "generateJavaToOWL"));
			if (generateOWLSupport == null) {
				MessageDialog.openInformation(
						window.getShell(),
						"Wrong project",
						"Project is not configured to perform a Java to OWL transformation! Make sure the project is created using the universAAL UML to Java tool!"
						);
				return null;
			}
			if (!generateOWLSupport.equals("true")) {
				if (MessageDialog.openQuestion(
						window.getShell(),
						"Java to OWL support is not enabled",
						"Currently the Java to OWL support is not enabled. Should make it active (need a new transformation from UML to Java)?"))
					project.setPersistentProperty(new QualifiedName("generateJavaToOWL", "generateJavaToOWL"), "true");
				MessageDialog.openInformation(
						window.getShell(),
						"Transformation not finished",
						"Transformation was aborted since it is not configured to be running with the tool."
						);
				return null;
			}
			
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		
		Model mavenFile = this.getModel(project);
		String projectName = mavenFile.getName();
		String projectID = mavenFile.getArtifactId().toLowerCase();
		if (projectName == null || projectID == null) {
			MessageDialog.openInformation(
					window.getShell(),
					"Wrong project", "Either the project is no universAAL project or it contains errors: pom.xml is missing"
					);
			return null;
		}
		
		// get name of the creator class
		IFile creatorClass = null;
		IFolder creatorFolder = project.getFolder("/src/main/java/org/universAAL/ontology/creator/");
		if (!creatorFolder.exists()) {
			creatorFolder = project.getFolder("/src/main/java/org/universAAL/ontology/" + projectName.toLowerCase() + "/creator/");
			if (!creatorFolder.exists()) {
				MessageDialog.openInformation(
						window.getShell(),
						"Wrong project", "Missing class with main-method for transformation! Was a transformation from UML to Java performed previously?"
						);
				return null;
			}
		}
		try {
			IResource members[] = creatorFolder.members();
			for (IResource member : members)
				if (member.getType() == IResource.FILE && member.getName().contains("Creator.java"))
					creatorClass = (IFile)member;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (creatorClass == null) {
			MessageDialog.openInformation(
					window.getShell(),
					"Missing File", "Missing class with main-method for transformation! Was a transformation from UML to Java performed previously?"
					);
			return null;
		}
		
		String creatorFullPath = creatorClass.getFullPath().toOSString().replace("\\", "/");
		String creatorPackage = creatorClass.getProjectRelativePath().toString().substring(14).replace("/", ".");
		creatorPackage = creatorPackage.substring(0, creatorPackage.length()-5);
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		try {
			for (ILaunchConfigurationType config : manager.getLaunchConfigurationTypes()) {
				System.out.println("Startconfig-Type " + config.getName() + ": " + config.getIdentifier());
			}
			for (ILaunchConfiguration config : manager.getLaunchConfigurations()) {
				System.out.println("Startconfig:" + config.getName());
				for (Object param : config.getAttributes().keySet())
					System.out.println("Param name: " + param.toString() + " = " + config.getAttributes().get(param).toString() + "("+config.getAttributes().get(param).getClass().getSimpleName()+")");
			}
		} catch (CoreException e1) {
			e1.printStackTrace();
			return null;
		}
		
		// Create and run the launch configuration
		ILaunchConfigurationType type = manager.getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
		try {
			ILaunchConfigurationWorkingCopy wc = type.newInstance(null,projectName);
			
			List<String> pathList = new ArrayList<String>();
			pathList.add(creatorFullPath);
			wc.setAttribute("org.eclipse.debug.core.MAPPED_RESOURCE_PATHS", pathList);
			List<String> typeList = new ArrayList<String>();
			typeList.add("1");
			wc.setAttribute("org.eclipse.debug.core.MAPPED_RESOURCE_TYPES", typeList);
			wc.setAttribute("org.eclipse.jdt.launching.CLASSPATH_PROVIDER", "org.eclipse.m2e.launchconfig.classpathProvider");
			wc.setAttribute("org.eclipse.jdt.launching.MAIN_TYPE", creatorPackage);
			wc.setAttribute("org.eclipse.jdt.launching.PROJECT_ATTR", project.getName());
			wc.setAttribute("org.eclipse.jdt.launching.SOURCE_PATH_PROVIDER", "org.eclipse.m2e.launchconfig.sourcepathProvider");
			
			ILaunchConfiguration config = wc.doSave();
			ILaunchGroup[] group = DebugUIPlugin.getDefault()
					.getLaunchConfigurationManager().getLaunchGroups();
			ILaunch launch = DebugUITools.buildAndLaunch(config, ILaunchManager.RUN_MODE, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}
	
	private Model getModel(IProject project) {	
		try {
			Reader reader = new FileReader(project.getLocationURI().getPath()
					+ File.separator + "pom.xml");
			System.out.println("Project is: " + project.getFullPath());
			MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
			Model model = xpp3Reader.read(reader);
			reader.close();
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private IProject getSelectedProject(ExecutionEvent event, IWorkbenchWindow window) {
		try {
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if (selection == null)
				selection = window.getSelectionService().getSelection("org.eclipse.jdt.ui.PackageExplorer");
			if (selection == null)
				selection = window.getSelectionService().getSelection("org.eclipse.ui.navigator.ProjectExplorer");
			
			
			if ((selection != null) && (selection instanceof StructuredSelection)) {
				Object selectedProject = ((StructuredSelection)selection).getFirstElement();
				if ((selectedProject instanceof IJavaProject)) {
					IJavaProject javaProject = (IJavaProject)selectedProject;
					IProject project = (IProject)javaProject.getCorrespondingResource();
					return project;
				}
			}
			
			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
