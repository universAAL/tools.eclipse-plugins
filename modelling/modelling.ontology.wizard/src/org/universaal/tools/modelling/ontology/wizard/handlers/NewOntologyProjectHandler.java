package org.universaal.tools.modelling.ontology.wizard.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class NewOntologyProjectHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public NewOntologyProjectHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get the wizard by its name
		IWizardDescriptor descriptor = PlatformUI
			.getWorkbench()
			.getNewWizardRegistry()
			.findWizard(
				"org.universaal.tools.modelling.ontology.wizard.ontologyprojectwizard");

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
			MessageDialog.openInformation(window.getShell(), "New Ontology Project",
				"Could not find the New Ontology Project Wizard");
			// TODO Externalize this?
		    }
		} catch (CoreException e) {
		    e.printStackTrace();
		}

		return null;
	}
}
