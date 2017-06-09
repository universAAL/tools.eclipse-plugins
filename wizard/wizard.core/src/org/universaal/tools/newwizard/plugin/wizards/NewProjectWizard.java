/*
	Copyright 2012-2014 ITACA-TSB, http://www.tsb.upv.es
	Instituto Tecnologico de Aplicaciones de Comunicacion 
	Avanzadas - Grupo Tecnologias para la Salud y el 
	Bienestar (TSB)
	
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
package org.universaal.tools.newwizard.plugin.wizards;

import org.apache.maven.model.Model;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.ui.internal.actions.OpenMavenConsoleAction;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IProgressConstants;
import org.universaal.tools.newwizard.plugin.Activator;
import org.universaal.tools.newwizard.plugin.versions.IMWVersion;

/**
 * This is a sample new wizard. Its role is to create a new file project
 */

public class NewProjectWizard extends Wizard implements INewWizard {
    // These are the folders for a maven project.
    private static final ProjectFolder JAVA = new ProjectFolder(
	    "src/main/java", "target/classes"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final ProjectFolder JAVA_TEST = new ProjectFolder(
	    "src/test/java", "target/test-classes"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final ProjectFolder RESOURCES = new ProjectFolder(
	    "src/main/resources", "target/classes"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final ProjectFolder RESOURCES_TEST = new ProjectFolder(
	    "src/test/resources", "target/test-classes"); //$NON-NLS-1$ //$NON-NLS-2$
    /**
     * List of Maven folders. It could be possible to use String[] instead of
     * own ProjectFodler class, but htis is copied from Maven plugin, and could
     * be helpful in the future for test folders.
     */
    private static final ProjectFolder[] JAR_DIRS = { JAVA, JAVA_TEST,
	    RESOURCES, RESOURCES_TEST };
    /**
     * First page with Maven info.
     */
    private NewProjectWizardPage1 page1;
    /**
     * Second page with universAAL info.
     */
    private NewProjectWizardPage2 page2;
    /**
     * Not really used yet, but could be used, in theory, to set the Working
     * Set.
     */
    private ISelection selection;
    /**
     * Maven utility to setup new projects.
     */
    ProjectImportConfiguration configuration;

    /**
     * Default constructor.
     */
    public NewProjectWizard() {
	super();
	setNeedsProgressMonitor(true);
	ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin(
		"org.universaal.tools.newwizard.plugin", //$NON-NLS-1$
		"icons/ic-uAAL-hdpi.png"); //$NON-NLS-1$
	setDefaultPageImageDescriptor(image);
	setWindowTitle(Messages.getString("Project.0"));
    }
    
    /**
     * We will accept the selection in the workbench to see if we can initialize
     * from it. Should use this for working sets...
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection sel) {
	selection = sel;
    }

    public void addPages() {
	configuration = new ProjectImportConfiguration();
	page1 = new NewProjectWizardPage1(selection);
	page2 = new NewProjectWizardPage2(selection);
	addPage(page1);
	addPage(page2);
    }
    
    public boolean canFinish() {
	return page1.isPageComplete() && page2.isPageComplete();
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish() {
	// Build up maven model from wizard p1
	final Model model = new Model();
	model.setModelVersion("4.0.0"); //$NON-NLS-1$
	model.setGroupId(page1.getMavenGroupId().getText());
	model.setArtifactId(page1.getMavenArtifactId().getText());
	model.setVersion(page1.getMavenVersion().getText());
	model.setName(page1.getMavenName().getText());
	model.setDescription(page1.getMavenDescription().getText());
	
	// This is the rest of the info coming from wizard p2
	final IMWVersion mwVersion=page2.getMWVersion();
	final boolean[] checks = new boolean[10];
	for(int i=0;i<checks.length;i++){
	    checks[i] = page2.getCheckClasses()[i].getSelection();
	}
	final String pack = page2.getTextPackage().getText();
	final boolean template=page2.getCheckTemp().getSelection();
	final int templateIndex=page2.getDropTemp().getSelectionIndex();
	
	// I use deprecated methods because I havent found the new way to
	// create a new project
	// TODO: Use the latest methods -> Latest version of Maven plugin keeps
	// using them!
	IStatus nameStatus = configuration.validateProjectName(model);
	if (!nameStatus.isOK()) {
	    MessageDialog.openError(getShell(),Messages
			    .getString("Project.1"), //$NON-NLS-1$
		    nameStatus.getMessage());
	    return false;
	}

	//Get access to workspace
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	final IPath location = null;//TODO use this
	final IWorkspaceRoot root = workspace.getRoot();
	final IProject project = configuration.getProject(root, model);

	// If there is already a pom there we cannot create the project
	boolean pomExists = (root.getLocation().append(project.getName()))
		.append(IMavenConstants.POM_FILE_NAME).toFile().exists();
	if (pomExists) {
	    MessageDialog.openError(getShell(),
		    Messages.getString("Project.2"), //$NON-NLS-1$
		    Messages.getString("Project.3")); //$NON-NLS-1$
	    return false;
	}

	// Create a blank maven project with POM as defined in wizard
	final Job job1 = new WorkspaceJob(Messages
			.getString("Project.4")) { //$NON-NLS-1$
	    public IStatus runInWorkspace(IProgressMonitor monitor) {
		setProperty(IProgressConstants.ACTION_PROPERTY,
			new OpenMavenConsoleAction());
		try {
		    // Use maven plugin to create the project
		    MavenPlugin.getProjectConfigurationManager()
			    .createSimpleProject(project, location, model,
				    getFolders(),
				    configuration, monitor);
		    return Status.OK_STATUS;
		} catch (CoreException e) {
		    return e.getStatus();
		} finally {
		    monitor.done();
		}
	    }
	};

	// Modify newly created project to be universAAL-compliant
	final Job job2 = new WorkspaceJob(Messages
			.getString("Project.5")) { //$NON-NLS-1$
	    public IStatus runInWorkspace(IProgressMonitor monitor) {
		setProperty(IProgressConstants.ACTION_PROPERTY,
			new OpenMavenConsoleAction());
		setProperty(IProgressConstants.ACTION_PROPERTY,
			    new OpenMavenConsoleAction());
		    try {
			// Create the package folders
			IFolder src = project.getFolder(JAVA.getPath());
			String[] folders = pack.replace(".", "#").split("#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			for (int i = 0; i < folders.length; i++) {
			    IFolder packFold = src.getFolder(folders[i]);
			    packFold.create(true, true, monitor);
			    src = packFold;
			}

			//Create the universAAL files
			if (template) {
			    //If it was a template, just copy the files from folder
			    mwVersion.createTemplateFiles(src, templateIndex, pack, monitor);
			} else {
			    // If not template, create files according to checks
			    mwVersion.createEmptyFiles(src, checks, pack, monitor);
			}
			
			// Now edit the POM file
			IFile pom = project.getFile("pom.xml"); //$NON-NLS-1$
			if (pom.exists()) {
			    mwVersion.modifyPOMFile(pom, checks, pack, monitor);
			} else {
			    return new Status(Status.ERROR, Activator.PLUGIN_ID,
				    Messages.getString("Project.6"));
			}
			
			// This is like refreshing, because we changed the pom
			MavenPlugin.getProjectConfigurationManager()
				.updateProjectConfiguration(project, monitor);
			return Status.OK_STATUS;
		    } catch (CoreException e) {
			return e.getStatus();
		    } catch (Exception e) {
			e.printStackTrace();
			return new Status(Status.ERROR, Activator.PLUGIN_ID,
				e.getMessage());
		    } finally {
			monitor.done();
		    }
	    }
	};
	// Listeners in case job fails
	job1.addJobChangeListener(new JobFailureListener(Messages.getString("Project.7")));
	job2.addJobChangeListener(new JobFailureListener(Messages.getString("Project.8")));

	// Now execute the Jobs
	ProjectListener listener = new ProjectListener();
	workspace.addResourceChangeListener(listener,
		IResourceChangeEvent.POST_CHANGE);
	try {
	    // Execute the first job (create maven)
	    job1.setRule(MavenPlugin.getProjectConfigurationManager().getRule());
	    job1.schedule();
	    // MNGECLIPSE-766 wait until new project is created
	    while (listener.getNewProject() == null
		    && (job1.getState() & (Job.WAITING | Job.RUNNING)) > 0) {
		try {
		    Thread.sleep(100L);
		} catch (InterruptedException ex) {
		    // ignore
		}
	    }
	    
	    // Execute the second job (modify to universAAL)
	    job2.schedule();
	    // MNGECLIPSE-766 wait until new project is created
	    while (listener.getNewProject() == null
		    && (job2.getState() & (Job.WAITING | Job.RUNNING)) > 0) {
		try {
		    Thread.sleep(100L);
		} catch (InterruptedException ex) {
		    // ignore
		}
	    }
	} finally {
	    workspace.removeResourceChangeListener(listener);
	}
	return true;
    }
    
    //________HELPERS________
    
    /**
     * Returns the Maven default folders.
     * 
     * @return Array with the names of folders
     */
    public String[] getFolders() {
	ProjectFolder[] mavenDirectories = JAR_DIRS;
	String[] directories = new String[mavenDirectories.length];
	for (int i = 0; i < directories.length; i++) {
	    directories[i] = mavenDirectories[i].getPath();
	}
	return directories;
    }

    /**
     * Helper Class for Maven folder representation
     */
    final static class ProjectFolder {
	/** Folder path */
	private String path = null;
	/** Output path */
	private String outputPath = null;

	ProjectFolder(String path, String outputPath) {
	    this.path = path;
	    this.outputPath = outputPath;
	}

	String getPath() {
	    return path;
	}

	String getOutputPath() {
	    return outputPath;
	}

	boolean isSourceEntry() {
	    return this.getOutputPath() != null;
	}
    }
    
    /**
     * Helper Class listener to detect failures when creating project.
     */
    private class JobFailureListener extends JobChangeAdapter {
	private String msg;

	JobFailureListener(String message) {
	    msg = message;
	}

	public void done(IJobChangeEvent event) {
	    final IStatus result = event.getResult();
	    if (!result.isOK()) {
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
			MessageDialog.openError(getShell(),
				msg, result //$NON-NLS-1$
					.getMessage());
		    }
		});
	    }
	}
    }

    /**
     * Helper Class listener to detect changes in projects (like finishing).
     */
    static class ProjectListener implements IResourceChangeListener {
	private IProject newProject = null;
	
	public IProject getNewProject() {
	    return newProject;
	}

	public void resourceChanged(IResourceChangeEvent event) {
	    IResourceDelta root = event.getDelta();
	    IResourceDelta[] projectDeltas = root.getAffectedChildren();
	    for (int i = 0; i < projectDeltas.length; i++) {
		IResourceDelta delta = projectDeltas[i];
		IResource resource = delta.getResource();
		if (delta.getKind() == IResourceDelta.ADDED) {
		    newProject = (IProject) resource;
		}
	    }
	}
    }
}