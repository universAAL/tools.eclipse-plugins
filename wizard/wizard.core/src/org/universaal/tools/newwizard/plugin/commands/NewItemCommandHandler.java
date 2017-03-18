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
package org.universaal.tools.newwizard.plugin.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.universaal.tools.newwizard.plugin.wizards.Messages;

public class NewItemCommandHandler extends AbstractHandler {

    // Executed when command is called
    public Object execute(ExecutionEvent event) throws ExecutionException {
	// Get the wizard by its name
	IWizardDescriptor descriptor = PlatformUI
		.getWorkbench()
		.getNewWizardRegistry()
		.findWizard(
			"org.universaal.tools.newwizard.plugin.wizards.NewItemWizard");

	try {
	    // Get the current (the main) window and start the wizard
	    IWorkbenchWindow window = HandlerUtil
		    .getActiveWorkbenchWindowChecked(event);
	    if (descriptor != null) {
		IWizard wizard = descriptor.createWizard();
		WizardDialog wd = new WizardDialog(window.getShell(), wizard);
		wd.setTitle(wizard.getWindowTitle());
		wd.open();
	    } else {
		MessageDialog.openInformation(window.getShell(), Messages.getString("Command.2"),
			Messages.getString("Command.3"));
	    }
	} catch (CoreException e) {
	    e.printStackTrace();
	}

	return null;
    }

}
