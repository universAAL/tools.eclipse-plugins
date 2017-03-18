/*
	Copyright 2011 FZI, http://www.fzi.de

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
package org.universaal.tools.configurationEditor.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.universaal.tools.configurationEditor.Activator;


public class NewConfigurationWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private NewConfigurationWizardPage newFileWizardPage;
    private IWorkbench workbench;
 

    public NewConfigurationWizard() {
        setWindowTitle("New Configuration");
        this.selection = new TreeSelection();     
    } 


    @Override
    public void addPages() {
    	super.addPages();
        newFileWizardPage = new NewConfigurationWizardPage(selection);
        addPage(newFileWizardPage);
    }
   
    @Override
    public boolean performFinish() {
       
        IFile file = newFileWizardPage.createNewFile();
        if (file != null){
        	try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(file), Activator.PLUGIN_ID);
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return true;
        } else {
            return false;
        }
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;

    }
    
}
