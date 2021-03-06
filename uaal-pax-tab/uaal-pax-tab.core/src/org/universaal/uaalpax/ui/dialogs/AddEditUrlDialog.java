/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut für Graphische Datenverarbeitung
	
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

package org.universaal.uaalpax.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.universaal.uaalpax.model.BundleEntry;
import org.universaal.uaalpax.model.LaunchURL;

public class AddEditUrlDialog extends Dialog {
	private final String title = "Add Bundle from URL";
	private final String urlMessage = "Enter bundle URL";
	private final String levelMessage = "Enter start level";
	
	private Text urlInput;
	private Text levelInput;
	private Text errorMessageText;
	private String errorMessage;
	private IInputValidator validator;
	
	private LaunchURL url;
	private int level;
	
	public AddEditUrlDialog(Shell parentShell) {
		super(parentShell);
		url = new LaunchURL("");
		level = 10;
	}
	
	public AddEditUrlDialog(Shell parentShell, BundleEntry be) {
		super(parentShell);
		
		url = be.getLaunchUrl();
		level = be.getLevel();
	}
	
	public LaunchURL getURL() {
		return url;
	}
	
	public int getLevel() {
		return level;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		validator = new IInputValidator() {
			public String isValid(String newText) {
				try {
					Integer.parseInt(newText);
					return null;
				} catch (NumberFormatException e) {
					return "Level must be an integer";
				}
			}
		};
		
		Label urlLabel = new Label(composite, SWT.WRAP);
		urlLabel.setText(urlMessage);
		GridData urlData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		urlData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		urlLabel.setLayoutData(urlData);
		urlLabel.setFont(parent.getFont());
		
		urlInput = new Text(composite, SWT.SINGLE | SWT.BORDER);
		urlInput.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		urlInput.setText(url.url);
		urlInput.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}
			
			public void focusGained(FocusEvent e) {
				urlInput.selectAll();
			}
		});
		urlInput.setFocus();
		
		Label lvlLabel = new Label(composite, SWT.WRAP);
		lvlLabel.setText(levelMessage);
		GridData lvlData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		lvlData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		lvlLabel.setLayoutData(lvlData);
		lvlLabel.setFont(parent.getFont());
		
		levelInput = new Text(composite, SWT.SINGLE | SWT.BORDER);
		levelInput.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		levelInput.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateLevel();
			}
		});
		levelInput.setText(String.valueOf(level));
		levelInput.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}
			
			public void focusGained(FocusEvent e) {
				levelInput.selectAll();
			}
		});
		
		errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		setErrorMessage(errorMessage);
		errorMessageText.setForeground(new Color(errorMessageText.getDisplay(), 255, 0, 0));
		
		applyDialogFont(composite);
		
		return composite;
	}
	
	protected void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		if (errorMessageText != null && !errorMessageText.isDisposed()) {
			errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
			// Disable the error message text control if there is no error, or
			// no error text (empty or whitespace only). Hide it also to avoid
			// color change.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
			boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
			errorMessageText.setEnabled(hasError);
			errorMessageText.setVisible(hasError);
			errorMessageText.getParent().update();
			// Access the ok button by id, in case clients have overridden button creation.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
			Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(errorMessage == null);
			}
		}
	}
	
	protected void validateLevel() {
		String errorMessage = null;
		if (validator != null) {
			errorMessage = validator.isValid(levelInput.getText());
		}
		
		setErrorMessage(errorMessage);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}
	
	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			url = new LaunchURL(urlInput.getText());
			level = Integer.parseInt(levelInput.getText());
		} else {
			url = null;
			level = -1;
		}
		super.buttonPressed(buttonId);
	}
}
