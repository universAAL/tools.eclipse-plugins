package org.universaal.tools.modelling.ontology.wizard.wizards;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResourceFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;		
import org.eclipse.m2e.core.ui.internal.actions.OpenMavenConsoleAction;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;
import org.universaal.tools.modelling.ontology.wizard.Activator;

/**
 * This class is a factory that crates the UML artefact for an ontology project
 * based information entered in the Wizard.
 *  
 * @author erlend
 *
 */
public class OntologyUMLArtefactFactory {
	
	/**
	 * Creates a set of UML artefacts based on the provided model with content from the Wizard.
	 * The artefacts are added to the project identified by the Wizard. The Eclipse project must 
	 * exist before this method is called.
	 *  
	 * @param model
	 */
	public static void createUMLArtefacts(OntologyProjectModel model, URI fromUri) {
		URI toUri = URI.createPlatformResourceURI("/" + model.getMavenModel().getArtifactId() + "/" + model.getOntologyName() + ".di", true);		
	
		clonePapyrusModel(fromUri.toString(), toUri.toString(), model);	
	}
	
	/** 
	 * Create a new resource set and register the GMF resource factory 
	 * with it. The ModelSet subclass of resource set is used because it may give 
	 * better support for Papyrus models
	 * 
	 * @return A new resource set with registered resource factories
	 */
	protected static ModelSet createAndInitResourceSet() {
		ModelSet resourceSet = new ModelSet();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				".notation", new GMFResourceFactory());
		
		return resourceSet;
	}	
	
	/**
	 * This method adds the set of file extensions used by Papyrus models, and forwards this
	 * call to cloneModel in order to clones the model files identified with the inModelURI into files identified
	 * by the cloneModelURI. The provided java model object is used to
	 * customize the cloned model with values entered in the Wizard
	 * 
	 * @param inModelURI
	 * @param cloneModelURI
	 * @param fileExtensions
	 * @param model
	 * @return
	 */
	public static ResourceSet clonePapyrusModel(String inModelURI, String cloneModelURI, OntologyProjectModel model) {
		return cloneModel(inModelURI, cloneModelURI, new String[] { "uml", "notation", "di"}, model);
	}		
	
	/**
	 * This method clones the model files identified with the inModelURI into files identified
	 * by the cloneModelURI and the set of extensions. The provided java model object is used to
	 * customize the cloned model with values entered in the Wizard
	 * 
	 * @param inModelURI
	 * @param cloneModelURI
	 * @param fileExtensions
	 * @param model
	 * @return
	 */
	public static ResourceSet cloneModel(String inModelURI, String cloneModelURI, String[] fileExtensions, OntologyProjectModel model) {
		// Create clone resource set and add resources for all extensions
		ResourceSet cloneSet = createAndInitResourceSet();
		int cloneExtIndex = cloneModelURI.lastIndexOf(".") + 1;
		String cloneBaseURI = cloneModelURI.substring(0, cloneExtIndex);
		Map<String, Resource> extensionToResourceMap = new HashMap<String, Resource>();
		
		for (String ext : fileExtensions) {
			String cloneFileName = cloneBaseURI + ext;
			URI uri = URI.createURI(cloneFileName);
			Resource resource = cloneSet.createResource(uri);		
			extensionToResourceMap.put(ext, resource);
		}

		// Create the in resource set, and prepare for reading and going through it
		ResourceSet inSet = createAndInitResourceSet();		
		int inExtIndex = inModelURI.lastIndexOf(".") + 1;
		String inBaseURI = inModelURI.substring(0, inExtIndex);
		
		List<EObject> listToClone = new ArrayList<EObject>();
		Map<String,String> classNameToExtensionMap = new HashMap<String, String>();
		
		
		// Load the models and collect the root objects to be cloned
		for (int i = 0; i < fileExtensions.length; i++) {
			EList<EObject> obj = loadContent(inSet, inBaseURI + fileExtensions[i]);
			listToClone.addAll(obj);
			
			if (fileExtensions[i].equals("uml")) {
				EcoreUtil.resolveAll(inSet);
			}

			for (EObject rootContent : obj) {
				classNameToExtensionMap.put(rootContent.eClass().getName(), fileExtensions[i]);
			}
		}
		
		// Start the cloning
		Collection<EObject> clonedList = EcoreUtil.copyAll(listToClone);
		
		
		// Add the clones to the clone resource set and set the right file names
		for (Iterator<EObject> iter = clonedList.iterator(); iter.hasNext(); ) {
			EObject cr = iter.next();

			// Find the right resource to add to, and add
			Resource resource = extensionToResourceMap.get(classNameToExtensionMap.get(cr.eClass().getName()));
			resource.getContents().add(cr);			
		}
		
		replaceTemplateNames(cloneSet ,model);
		
		// Save the resources
		EList<Resource> resources = cloneSet.getResources();
		for (Iterator<Resource> resIter = resources.iterator(); resIter.hasNext(); ) {					
			try {
				resIter.next().save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return cloneSet;		
	}
	
	/**
	 * Goes through the model elements, replacing template names, tagged values and comments
	 * with values entered in the wizard
	 * 
	 * @param resourceSet
	 * @param model The Java model class containing the values from the wizard
	 */
	public static void replaceTemplateNames(ResourceSet resourceSet, OntologyProjectModel model) {
		//Collection<NamedElement> matches = UMLUtil.findNamedElements(resourceSet, "abc");
		
		
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		replaceMap.put("$$MODEL_NAME$$", model.getOntologyName());
		replaceMap.put("$$PACKAGE_NAME$$", model.getPackageName());
		
		
		for (Iterator it = resourceSet.getAllContents(); it.hasNext();) {
			Object element = it.next();
			if (element instanceof NamedElement) {
				String newName = replaceMap.get(((NamedElement) element).getName());
				if (newName != null) {
					((NamedElement) element).setName(newName);					
				}
				if (element instanceof org.eclipse.uml2.uml.Model) {
					Comment comm = ((NamedElement) element).getOwnedComments().get(0);
					if (comm != null) {
						comm.setBody(model.getMavenModel().getDescription());			
					}
				}
				if (element instanceof org.eclipse.uml2.uml.Package) {
					Stereotype s = ((NamedElement) element).getAppliedStereotype("OWL::owlOntology");
					if (s != null) {
						UMLUtil.setTaggedValue((Element) element, s, "defaultNamespace", model.getOntologyNamespace());
						UMLUtil.setTaggedValue((Element) element, s, "versionInfo", model.getMavenModel().getVersion());
					}
				}
			}
		}
		
	}
	
	
	/**
	 * Load the content of the file by getting it through the 
	 * provided resource set.
	 * 
	 * @param resourceSet The resource set used to load the resource
	 * @param fileURI
	 * @return The content of the resource, or null if no content could not be found
	 */
	public static EList<EObject> loadContent(ResourceSet resourceSet, String fileURI) {
		
		if(fileURI !=null){
			//Set the file name from the dialog
			URI uri = URI.createURI(fileURI); // createFileURI
			Resource resource = resourceSet.getResource(uri, true);
			try {
				resource.load(null);
				return resource.getContents();
			}
			catch (Exception e){
				System.out.println("failed to load content of file : " + fileURI);
			}			
		}			
		return null;
	}
	
	
	
}
