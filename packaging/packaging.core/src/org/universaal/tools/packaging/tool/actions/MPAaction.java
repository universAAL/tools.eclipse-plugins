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
package org.universaal.tools.packaging.tool.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import org.universaal.tools.packaging.tool.api.WizardDialogMod;
import org.universaal.tools.packaging.tool.gui.GUI;
import org.universaal.tools.packaging.tool.preferences.EclipsePreferencesConfigurator;
import org.universaal.tools.packaging.tool.util.DefaultLogger;
import org.universaal.tools.packaging.tool.util.XSDParser;


/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate 
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class MPAaction extends AbstractHandler {

	public GUI gui;
	private Boolean recovered = false;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow w = HandlerUtil.getActiveWorkbenchWindow(event);
		List<IProject> parts = new ArrayList<IProject>();
		String recFile = org.universaal.tools.packaging.tool.Activator.tempDir + EclipsePreferencesConfigurator.local.getRecoveryFileName();
		String recParts = org.universaal.tools.packaging.tool.Activator.tempDir + EclipsePreferencesConfigurator.local.getRecoveryPartsName();
		w.getShell().setCursor(new Cursor(w.getShell().getDisplay(),SWT.CURSOR_WAIT));
		
		EclipsePreferencesConfigurator.local.mEmbSet = false;
		
		if ( EclipsePreferencesConfigurator.local.isPersistanceEnabled()) {
			DefaultLogger.getInstance().log("Searching for recovery file "+ recFile);
			File recovery = new File(recFile);
			if(recovery.exists()){
				DefaultLogger.getInstance().log("Found It");
				DefaultLogger.getInstance().log("Searching for recovery parts file "+ recParts);
				File recoveryParts = new File(recParts);
				if(recoveryParts.exists()){
					DefaultLogger.getInstance().log("Found It");
					Boolean tryRecover = MessageDialog.openConfirm(w.getShell(),
							"Recovery", "A previous operation has been cancelled.\n\nWould you like to recover it ?");
					if(tryRecover){
					
						try{
							FileInputStream fis = new FileInputStream(recParts);
				            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
				         
				            String line = reader.readLine();
				            
				            while(line != null){
				                if(!line.trim().isEmpty()){
									DefaultLogger.getInstance().log("Importing part "+line);
									IContainer container = ResourcesPlugin.getWorkspace().getRoot().getProject(line);
									parts.add(container.getProject());
								}
				                line = reader.readLine();
							}
							this.recovered = true;
						} catch (IOException e){
							e.printStackTrace();
						}
					} else {
						this.recovered = false;
					}
				}
			}

		}
		
		if(!this.recovered){
			FilteredResourcesSelectionDialog dialog = new FilteredResourcesSelectionDialog(w.getShell(), true, ResourcesPlugin.getWorkspace().getRoot(), IResource.PROJECT);
			dialog.setTitle("Resources Selection");
			dialog.setMessage("Please select the universAAL projects you want to include in the UAPP container.\nUse the CTRL key for selecting multiple projects that will generate a multi-part application");
			dialog.setInitialPattern("?");
			dialog.open();
			
			String partsFileContent = "";
			
			if(dialog.getResult() != null){
				for(int i = 0; i < dialog.getResult().length; i++){
					String[] segments = dialog.getResult()[i].toString().split("/");
					DefaultLogger.getInstance().log(segments[segments.length-1]);
					IContainer container = ResourcesPlugin.getWorkspace().getRoot().getProject(segments[segments.length-1]);
					parts.add(container.getProject());
					partsFileContent = partsFileContent + segments[segments.length-1] + System.getProperty("line.separator");
				}
				
				if(EclipsePreferencesConfigurator.local.isPersistanceEnabled()){
					try {
						File f = new File(org.universaal.tools.packaging.tool.Activator.tempDir);
						if(!f.exists()) f.mkdir();
						BufferedWriter bw = new BufferedWriter(new FileWriter(recParts));
						bw.write(partsFileContent);
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			} else{
				MessageDialog.openInformation(w.getShell(),
						"Application Packager", "Please verify the selection of parts.");
			}
		}
		
		if(parts.size()>0){
			
			List<IProject> alreadyClosed = new ArrayList<IProject>();
			for(int i=0; i<parts.size(); i++)
				try {
					if(alreadyClosed.indexOf(parts.get(i)) == -1){
						if(!parts.get(i).isOpen())
							parts.get(i).open(null);
						IProject[] ref = parts.get(i).getReferencedProjects();
							if(ref.length>0){
								for(int j=0; j< ref.length; j++){
									if(ref[j].isOpen())
									{	
										DefaultLogger.getInstance().log("--> Closing referenced project "+ref[j].getName());
										ref[j].close(null);
										alreadyClosed.add(ref[j]);
									}
								}
							}
							
							DefaultLogger.getInstance().log("Closing project "+parts.get(i).getName());
						parts.get(i).close(null);
						DefaultLogger.getInstance().log("DONE!");
						
					}
				
					
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			String mainPartName = "";
			if(!recovered){
				if(parts.size() > 1){
					
					ElementListSelectionDialog dialog = new ElementListSelectionDialog(w.getShell(), new LabelProvider());
	
					dialog.setTitle("Main Part Selection");
	
					dialog.setMessage("Please select the universAAL Resource you want to be the \"Main\" Part \nwhere the Wizard can load data from");
	
					Object[] elements = new Object[parts.size()];
					for(int i=0; i<parts.size(); i++)
						elements[i] = parts.get(i).getName();
					dialog.setElements(elements);
	
					dialog.open();
					
					if(dialog.getResult() != null){
						IContainer container = ResourcesPlugin.getWorkspace().getRoot().getProject(dialog.getResult()[0].toString());
						mainPartName = container.getProject().getName();
					}
					 else{
						MessageDialog.openInformation(w.getShell(),
								"Application Packager", "Please verify the selection of the main part.");
						return null;
					}
				} else mainPartName = parts.get(0).getName();
			}
			
			gui = new GUI(parts, this.recovered, mainPartName);	
			WizardDialogMod wizardDialog = new WizardDialogMod(w.getShell(), gui);
			wizardDialog.open();

		}
		
		w.getShell().setCursor(new Cursor(w.getShell().getDisplay(),SWT.CURSOR_ARROW));
		
		return null;
	}

}