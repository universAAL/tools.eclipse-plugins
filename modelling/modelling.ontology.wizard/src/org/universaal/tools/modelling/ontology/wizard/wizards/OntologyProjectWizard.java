package org.universaal.tools.modelling.ontology.wizard.wizards;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.ui.internal.actions.OpenMavenConsoleAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.progress.IProgressConstants;
import org.universaal.tools.modelling.ontology.wizard.Activator;
import org.universaal.tools.modelling.ontology.wizard.versions.IOntologyProjectGenerator;
import org.universaal.tools.modelling.ontology.wizard.versions.OntologyProjectGeneratorFactory;

public class OntologyProjectWizard extends Wizard implements INewWizard {
	
	OntologyProjectModel ontologyProjectModel = new OntologyProjectModel();
	
	OntologyMainPage mainPage;
	OntologyImportPage importPage;
	//MavenDetailsPage mavenPage;
	IWorkbench workbench;
	

	public OntologyProjectWizard() {
		setWindowTitle("New Wizard");
	}

	@Override
	public void addPages() {
		mainPage = new OntologyMainPage();
		mainPage.setModel(ontologyProjectModel);
		importPage = new OntologyImportPage();
		importPage.setModel(ontologyProjectModel);
		//mavenPage = new MavenDetailsPage();
		//mavenPage.setModel(ontologyProjectModel.mavenModel);
		addPage(mainPage);
		addPage(importPage);
		//addPage(mavenPage);
	}

	@Override
	public boolean performFinish() {
/* Possible code to check project validity		
	    String projectName = getProjectName(model); 
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();

	    // check if the project name is valid
	    IStatus nameStatus = workspace.validateName(projectName, IResource.PROJECT);
	    if(!nameStatus.isOK()) {
	      return nameStatus;
	    }

	    // check if project already exists
	    if(workspace.getRoot().getProject(projectName).exists()) {
	      return new Status(IStatus.ERROR, IMavenConstants.PLUGIN_ID, 0,
	          NLS.bind(Messages.importProjectExists, projectName), null); //$NON-NLS-1$
	    }
	    
	    return Status.OK_STATUS;		
*/
		//mavenPage.updateModel();
		
	    final ProjectImportConfiguration configuration;
	    final Model model = ontologyProjectModel.getMavenModel();
		configuration = new ProjectImportConfiguration();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		//final IPath location = null;
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

		// Now we are ready to start the real work
		return performProjectCreationJobs(project, model, configuration, workspace);
		
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}

	

	/** The content of this method will later be refactored to use factory class
	 * 
	 * @return
	 */
	public boolean performProjectCreationJobs(final IProject project, final Model model, final ProjectImportConfiguration configuration, IWorkspace workspace ) {
		final Job job, job2;

		// This job creates a blank maven project with the POM as defined in the
		// wizard
		job = new WorkspaceJob(
			Messages.getString("Project.8")) { //$NON-NLS-1$
		    public IStatus runInWorkspace(IProgressMonitor monitor) {
			setProperty(IProgressConstants.ACTION_PROPERTY,
				new OpenMavenConsoleAction());
			try {
				IOntologyProjectGenerator gen = OntologyProjectGeneratorFactory.getMWVersion(ontologyProjectModel.mwVersion);
				gen.createPOM(ontologyProjectModel, project, configuration, monitor);
				//gen.createUMLArtefacts(ontologyProjectModel);
				//CreateOntologyPOM.createPOM(ontologyProjectModel, project, configuration, monitor);
			    return Status.OK_STATUS;
			} catch (CoreException e) {
			    return e.getStatus();
			} finally {
			    monitor.done();
			}
		    }
		};

		// This job modifies the newly created blank maven project to be
		// universAAL-compliant
		job2 = new WorkspaceJob(
			Messages.getString("Project.9")) { //$NON-NLS-1$
		    public IStatus runInWorkspace(IProgressMonitor monitor) {
			setProperty(IProgressConstants.ACTION_PROPERTY,
				new OpenMavenConsoleAction());
			try {
				IOntologyProjectGenerator gen = OntologyProjectGeneratorFactory.getMWVersion(ontologyProjectModel.mwVersion);
				gen.createUMLArtefacts(ontologyProjectModel);
				//old OntologyUMLArtefactFactory.createUMLArtefacts(ontologyProjectModel);
				
			    // This is like refreshing, because we changed the pom
			    //MavenPlugin.getProjectConfigurationManager()
				//    .updateProjectConfiguration(project, monitor);
			    return Status.OK_STATUS;
			} catch (Exception e) {
			    e.printStackTrace();
			    return new Status(
				    Status.ERROR,
				    Activator.PLUGIN_ID,
				    e.getMessage());
			} finally {
			    monitor.done();
			}
		    }
		};
		// Listener in case job fails
		job.addJobChangeListener(new JobChangeAdapter() {
		    public void done(IJobChangeEvent event) {
			final IStatus result = event.getResult();
			if (!result.isOK()) {
			    Display.getDefault().asyncExec(new Runnable() {
				public void run() {
				    MessageDialog
					    .openError(
						    getShell(), //
						    Messages
							    .getString("Project.4"), result //$NON-NLS-1$
							    .getMessage());
				}
			    });
			}
		    }
		});
		// Listener in case job fails
		job2.addJobChangeListener(new JobChangeAdapter() {
		    public void done(IJobChangeEvent event) {
			final IStatus result = event.getResult();
			if (!result.isOK()) {
			    Display.getDefault().asyncExec(new Runnable() {
				public void run() {
				    MessageDialog
					    .openError(
						    getShell(), //
						    Messages
							    .getString("Project.5"), result //$NON-NLS-1$
							    .getMessage());
				}
			    });
			}
		    }
		});

		ProjectListener listener = new ProjectListener();
		workspace.addResourceChangeListener(listener,
			IResourceChangeEvent.POST_CHANGE);
		try {
		    // Execute the first job (create maven)
		    job.setRule(MavenPlugin.getProjectConfigurationManager().getRule());
		    job.schedule();

		    // Wait until new project is created
		    while (listener.getNewProject() == null
			    && (job.getState() & (Job.WAITING | Job.RUNNING)) > 0) {
			try {
			    Thread.sleep(100L);
			} catch (InterruptedException ex) {
			    // ignore
			}
		    }
		    // Execute the second job (modify to universAAL)
		    // job2.setRule(MavenPlugin.getProjectConfigurationManager().getRule());
		    job2.schedule();

		    // Wait until new project is created
		    while (listener.getNewProject() == null
			    && (job2.getState() & (Job.WAITING | Job.RUNNING)) > 0) {
			try {
			    Thread.sleep(100L);
			} catch (InterruptedException ex) {
			    // ignore
			}
		    }
		    
	    	try {
				listener.getNewProject().setPersistentProperty(new QualifiedName("generateJavaToOWL", "generateJavaToOWL"), ontologyProjectModel.isGenerateJavaToOWL() ? "true" : "false");
	    	} catch (CoreException e) {
				e.printStackTrace();
			}
		} finally {
		    workspace.removeResourceChangeListener(listener);
		}
		return true;	
	}

	static class ProjectListener implements IResourceChangeListener {
		private IProject newProject = null;

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

		public IProject getNewProject() {
			return newProject;
		}
	}
	
}
