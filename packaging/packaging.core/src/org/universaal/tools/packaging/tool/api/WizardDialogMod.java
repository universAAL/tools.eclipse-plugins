/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Chris Gross (schtoo@schtoo.com) - patch for bug 16179
 *     Eugene Ostroukhov <eugeneo@symbian.org> - Bug 287887 [Wizards] [api] Cancel button has two distinct roles
 *     Paul Adams <padams@ittvis.com> - Bug 202534 - [Dialogs] SWT error in Wizard dialog when help is displayed and "Finish" is pressed
 *******************************************************************************/

package org.universaal.tools.packaging.tool.api;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog to show a wizard to the end user.
 * <p>
 * In typical usage, the client instantiates this class with a particular
 * wizard. The dialog serves as the wizard container and orchestrates the
 * presentation of its pages.
 * <p>
 * The standard layout is roughly as follows: it has an area at the top
 * containing both the wizard's title, description, and image; the actual wizard
 * page appears in the middle; below that is a progress indicator (which is made
 * visible if needed); and at the bottom of the page is message line and a
 * button bar containing Help, Next, Back, Finish, and Cancel buttons (or some
 * subset).
 * </p>
 * <p>
 * Clients may subclass <code>WizardDialog</code>, although this is rarely
 * required.
 * </p>
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
 
public class WizardDialogMod extends WizardDialog {

	public WizardDialogMod(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
		super.setHelpAvailable(false);
	}

	@Override
	public void buttonPressed(int buttonId) {
		switch (buttonId) {
			case IDialogConstants.HELP_ID: {
				helpPressed();
				break;
			}
			case IDialogConstants.BACK_ID: {
				WizardPageMod page = (WizardPageMod) getCurrentPage();
				if(page.backPressed()) // to handle custom events
					backPressed();
				break;
			}
			case IDialogConstants.NEXT_ID: {
	
				WizardPageMod page = (WizardPageMod) getCurrentPage();
				if(page.nextPressed()) // to handle custom events
					nextPressed();
				break;
			}
			case IDialogConstants.FINISH_ID: {
				finishPressed();
				break;
			}
		}
	}
}