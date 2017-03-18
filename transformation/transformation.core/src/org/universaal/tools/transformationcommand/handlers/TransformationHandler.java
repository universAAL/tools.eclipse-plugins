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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.mofscript.MOFScriptModel.MOFScriptSpecification;
import org.eclipse.mofscript.parser.MofScriptParseError;
import org.eclipse.mofscript.parser.ParserUtil;
import org.eclipse.mofscript.runtime.ExecutionManager;
import org.eclipse.mofscript.runtime.ExecutionMessageListener;
import org.eclipse.mofscript.runtime.MofScriptExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.universaal.tools.transformationcommand.activator.Activator;
import org.universaal.tools.transformationcommand.preferences.PreferenceConstants;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public abstract class TransformationHandler extends AbstractHandler implements ExecutionMessageListener {
	String transformationFileName;
	String thisBundleName;
	private MessageConsole myConsole;
	private MessageConsoleStream stream;

	/**
	 * Finally, I am subversive
	 */
	public void setFileAndBundleName(String theTransformationFile, String theBundle) {
		transformationFileName = theTransformationFile;
		thisBundleName = theBundle;	

	}	

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
//org.eclipse.ui.internal.handlers.WizardHandler
		// First, retrieve the current selection and check whether it is a file
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		ISelection selection = window.getSelectionService().getSelection();
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection == null)
			selection = window.getSelectionService().getSelection("org.eclipse.jdt.ui.PackageExplorer");
		if (selection == null)
			selection = window.getSelectionService().getSelection("org.eclipse.ui.navigator.ProjectExplorer");
		
		
		if ((selection != null) && (selection instanceof StructuredSelection)) {
			Object selectedFile = ((StructuredSelection)selection).getFirstElement();
			if ((selectedFile instanceof IFile) && ((IFile)selectedFile).getName().endsWith(getSourceFileSuffix())){
				// If the selection is a file, start the transformation
				doTransform((IFile)selectedFile, event);
			} else {
				MessageDialog.openInformation(
					window.getShell(),
					"Transformation Command",
					"Please fist select a " + getSourceFileSuffix() + " file in the package or project explorer." );		
			}
		}			
		else {
			MessageDialog.openInformation(
					window.getShell(),
					"Transformation Command",
					"Please fist select a " + getSourceFileSuffix() + " file in the package or project explorer." );			
		}		

		return null;
	}


	public void doTransform(IFile inputFile, ExecutionEvent event) {
		IWorkbenchWindow window = null;
		try {
			 window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		}
		myConsole = findConsole("MOFScript2 Console");
		try {
			FontData data = new FontData ("Arial", 9, 9);
			data.setStyle(SWT.ITALIC);
			Font f = new Font (null, data);

			myConsole.setFont(f);
		} catch (Exception ex) {	   	
		}
		stream = myConsole.newMessageStream();
		stream.setActivateOnWrite(true);
		
		// check if OWLSupport should be generated. TODO: I do not think that persistent properties are the best solution
		String generateOWLSupport = "true";
		try {
			String tmpParamCheck = inputFile.getProject().getPersistentProperty(new QualifiedName("generateJavaToOWL", "generateJavaToOWL"));
			generateOWLSupport = tmpParamCheck != null ? tmpParamCheck : generateOWLSupport;
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		
		IPath path = new Path(transformationFileName);
		URL l = FileLocator.find(Platform.getBundle(thisBundleName), path, null);

		try {
			l = FileLocator.toFileURL(l);
		} catch (IOException e) {
			
			System.out.println("Could not locate transformation script");
			return;
		}
		if (l != null) {
			System.out.print("Running transformation script: ");
			System.out.println(l);
			
		}

		ParserUtil parserUtil = new ParserUtil();
		
		// Set path to find any imports of transformation, assuming they are located in same folder as the initial transformation 
		IPath path2 = new Path(transformationFileName).removeLastSegments(1);
		URL l2 = FileLocator.find(Platform.getBundle(thisBundleName), path2, null);
		try {
			l2 = FileLocator.toFileURL(l2);
			parserUtil.setCompilePath(l2.getPath());
			System.out.println("Set transformation path to " + l2.getPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		ExecutionManager execMgr = ExecutionManager.getExecutionManager();       
		//
		// The parserutil parses and sets the input transformation model
		// for the execution manager.
		//

		File f = null;        
		try {
			String temp = l.toString();
			temp = temp.replace(" ", "%20");
			l = new URL(temp);

			f= new File(l.toURI());
		} catch (URISyntaxException e) {
			
			System.out.println("Could not find URI for transformation script");
			return;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MOFScriptSpecification spec = parserUtil.parse(f, true);
		// check for errors:
		int errorCount = ParserUtil.getModelChecker().getErrorCount();
		Iterator errorIt = ParserUtil.getModelChecker().getErrors(); // Iterator of MofScriptParseError objects

		System.out.println ("Preparing transformation...");       
		if (errorCount > 0) {

			System.out.println ("Error parsing transformation: " + errorCount + " errors");       

			for (;errorIt.hasNext();) {
				MofScriptParseError parseError = (MofScriptParseError) errorIt.next();
				System.out.println("\t \t: Error: " + parseError.toString());
			}           
			return;           
		}



		// load source model
		XMIResourceFactoryImpl _xmiFac = new XMIResourceFactoryImpl();         
		EObject sourceModel = null;
		//        File sourceModelFile = new File(selectedFile.getLocationURI()); // new File ("SM.ecore");       
		ResourceSet rSet = new ResourceSetImpl ();
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", _xmiFac);

		//System.out.println("Converting URI for selected file: " + inputFile.getLocationURI().toString());

		URI uri = null;
		try {
			uri = URI.createURI(inputFile.getLocationURI().toString());
		}
		catch (Exception ex) {
			
			ex.printStackTrace();
			return;
		}
		System.out.println("Converted URI for selected file");

		Resource resource = rSet.getResource(uri, true);

		if (resource != null) {
			if (resource.getContents().size() > 0) {
				sourceModel = (EObject) resource.getContents().get(0);
			}
		}       

		if (sourceModel == null) {
			
			System.out.println("Source model could not be located");
			return;
		}
		System.out.println("Adding source model");

		// set the source model for the execution manager   
		execMgr.addSourceModel(sourceModel);
		if (dualMetamodel())
			execMgr.addSourceModel(sourceModel);

		// sets the root output directory, if any is desired (e.g. "c:/temp")
		IProject project = inputFile.getProject();
		execMgr.setRootDirectory(findRootDirectory(project));		
		System.setProperty("org.universaal.tools.transformationcommand.javadir", 
				getTrimmedJavaDirectoryFromPreferences() );
		System.setProperty("org.universaal.tools.transformationcommand.testdir", 
				getTrimmedTestDirectoryFromPreferences() );
		System.setProperty("org.universaal.tools.transformationcommand.javaowlsupport", 
				generateOWLSupport);

		// if true, files are not generated to the file system, but populated into a filemodel
		// which can be fetched afterwards. Value false will result in standard file generation
		execMgr.setUseFileModel(false);
		// Turns on/off system logging
		execMgr.setUseLog(false);


		execMgr.setBlockCommentTag("//");
		// Use UTF-8 as the encoding of the generated files
		execMgr.setCharset("UTF-8");

		// Adds an output listener for the transformation execution.
		execMgr.getExecutionStack().addOutputMessageListener(this);   
		try {
			System.out.println("Performing transformation");

			execMgr.executeTransformation();      
			System.out.println("Completed transformation");
			//New code
			project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
		    MavenPlugin.getProjectConfigurationManager()
		    	.updateProjectConfiguration(project, new NullProgressMonitor());	
		    // Set flag for OWL to Java support if is not given
			String tmpParamCheck = inputFile.getProject().getPersistentProperty(new QualifiedName("generateJavaToOWL", "generateJavaToOWL"));
			if (tmpParamCheck == null)
				project.setPersistentProperty(new QualifiedName("generateJavaToOWL", "generateJavaToOWL"), "false");
		} catch (MofScriptExecutionException mex) {
			
			mex.printStackTrace();
		} catch (CoreException e){
			
			e.printStackTrace();
		}     
	}

	@Override
	public void executionMessage(String arg0, String arg1) {
		// Ignore messages from MOFscript for now
		//		System.out.println(arg1);	
		if (arg0 == null || arg0.equals("") || arg0.equals("println"))
			stream.println(arg1);
		else if (arg0.equalsIgnoreCase("print"))
			stream.print(arg1);
	}

	/**
	 * Added by Federico Volpini
	 * 
	 * Return the MessageConsoleStream
	 * @return stream
	 */
	protected MessageConsoleStream getStream(){
		return this.stream;
	}
	
	/**
	 * Reads preferences and finds the correct directory to save files to.
	 * @param inputFile
	 * @return
	 */
	private String findRootDirectory(IProject project){
		if(getAbsolutePathBooleanFromPreferences()){
			return getRootDirectoryFromPreferences();
		}else{
			String result = project.getLocation().toString(); 
			String root = getRootDirectoryFromPreferences();
			if ((root != null) && (root.length() > 0)) {
				result = result + 
					(root.charAt(0)=='/' ? "" : "/")+ 
					root;
			}
			return result;
		}

	}
	private String getTrimmedJavaDirectoryFromPreferences() {
		// Strip away any leading "/" in directory name
		String javaDir = getJavaDirectoryFromPreferences();
		if (javaDir.charAt(0)=='/')
			return javaDir.substring(1);
		return javaDir;		
	}
	private String getTrimmedTestDirectoryFromPreferences() {
		// Strip away any leading "/" in directory name
		String javaDir = getTestDirectoryFromPreferences();
		if (javaDir.charAt(0)=='/')
			return javaDir.substring(1);
		return javaDir;		
	}
	
	// The following methods can be overriden in subclasses if different preferences and directories are wanted

	protected String getRootDirectoryFromPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String directory = store.getString(PreferenceConstants.P_UML2JAVA_ROOTPATH);
		return directory;
	}

	protected String getJavaDirectoryFromPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String directory = store.getString(PreferenceConstants.P_UML2JAVA_JAVAPATH);
		return directory;
	}

	protected boolean getAbsolutePathBooleanFromPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean absolutePath = store.getBoolean(PreferenceConstants.P_UML2JAVA_ABSOLUTE_BOOLEAN);
		return absolutePath;
	}	
	
	protected String getTestDirectoryFromPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String directory = store.getString(PreferenceConstants.P_UML2JAVA_TESTPATH);
		return directory;
	}

	protected abstract boolean dualMetamodel();
	protected abstract String getSourceFileSuffix();
	
	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}
}
