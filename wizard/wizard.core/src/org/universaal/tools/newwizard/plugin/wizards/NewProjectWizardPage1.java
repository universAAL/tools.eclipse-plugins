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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.universaal.tools.newwizard.plugin.Activator;

/**
 * The first wizard page allows setting the details for the project, represented
 * by the basic data for the maven POM, the name of the project and the
 * description.
 */
public class NewProjectWizardPage1 extends WizardPage {

    private Text textGroupId;
    private Text textArtifactId;
    private Text textVersion;
    private Text textName;
    private Text textDescription;

    /**
     * Default constructor with arg.
     * 
     * @param selection
     *            Used to determine working set(?).
     */
    public NewProjectWizardPage1(ISelection selection) {
	super("projectWizardPage1"); //$NON-NLS-1$
	setTitle(Messages.getString("Page1.0")); //$NON-NLS-1$
	setDescription(Messages.getString("Page1.9")); //$NON-NLS-1$
    }

    public void createControl(Composite parent) {
	Composite containerParent = new Composite(parent, SWT.NULL);

	GridLayout layoutParent = new GridLayout();
	layoutParent.numColumns = 2;
	layoutParent.verticalSpacing = 9;
	containerParent.setLayout(layoutParent);

	// Set the help
	PlatformUI.getWorkbench().getHelpSystem()
		.setHelp(parent, Activator.PLUGIN_ID + ".help_project");

	// Group Id
	Label label1 = new Label(containerParent, SWT.NULL);
	label1.setText(Messages.getString("Page1.1")); //$NON-NLS-1$
	textGroupId = new Text(containerParent, SWT.BORDER | SWT.SINGLE);
	textGroupId.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	textGroupId.addModifyListener(new ModifyListener() {
	    public void modifyText(ModifyEvent e) {
		validate();
	    }
	});

	// Artifact Id
	Label label2 = new Label(containerParent, SWT.NULL);
	label2.setText(Messages.getString("Page1.2")); //$NON-NLS-1$
	textArtifactId = new Text(containerParent, SWT.BORDER | SWT.SINGLE);
	textArtifactId.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	textArtifactId.addModifyListener(new ModifyListener() {
	    public void modifyText(ModifyEvent e) {
		validate();
	    }
	});

	// Version
	Label label3 = new Label(containerParent, SWT.NULL);
	label3.setText(Messages.getString("Page1.3")); //$NON-NLS-1$
	textVersion = new Text(containerParent, SWT.BORDER | SWT.SINGLE);
	textVersion.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	textVersion.addModifyListener(new ModifyListener() {
	    public void modifyText(ModifyEvent e) {
		validate();
	    }
	});

	// Name
	Label label4 = new Label(containerParent, SWT.NULL);
	label4.setText(Messages.getString("Page1.4")); //$NON-NLS-1$
	textName = new Text(containerParent, SWT.BORDER | SWT.SINGLE);
	textName.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	// name.addModifyListener(new ModifyListener() {
	// public void modifyText(ModifyEvent e) {
	// // TODO: Need to validate the name?
	// }
	// });

	// Description
	Label label5 = new Label(containerParent, SWT.NULL);
	label5.setText(Messages.getString("Page1.5")); //$NON-NLS-1$
	textDescription = new Text(containerParent, SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
	GridData gd5 = new GridData(SWT.FILL, SWT.FILL, false, true);
	gd5.minimumHeight = 15;
	textDescription.setLayoutData(gd5);
	// description.addModifyListener(new ModifyListener() {
	// public void modifyText(ModifyEvent e) {
	// // TODO: Need to validate description?
	// }
	// });

	// Start and validate
	setControl(containerParent);
	validate();
    }

    /**
     * Check that needed info is not empty.
     */
    void validate() {
	// TODO: Use BindingContext like Ontology wizard? For now not here, this
	// is easier. These must not be empty
	if (textGroupId.getText().trim().length() == 0) {
	    setErrorMessage(Messages.getString("Page1.6")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}

	if (textArtifactId.getText().trim().length() == 0) {
	    setErrorMessage(Messages.getString("Page1.7")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}

	if (textVersion.getText().trim().length() == 0) {
	    setErrorMessage(Messages.getString("Page1.8")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}
	setPageComplete(true);
	setErrorMessage(null);
	setMessage(null);
    }
    
    //________GETTERS________

    /**
     * Getter for Maven Group.
     * 
     * @return The text object for Maven group.
     */
    public Text getMavenGroupId() {
	return textGroupId;
    }

    /**
     * Getter for Maven Artifact.
     * 
     * @return The text object for Maven artifact.
     */
    public Text getMavenArtifactId() {
	return textArtifactId;
    }

    /**
     * Getter for Maven Version.
     * 
     * @return The text object for Maven version.
     */
    public Text getMavenVersion() {
	return textVersion;
    }

    /**
     * Getter for Maven Name.
     * 
     * @return The text object for Maven name.
     */
    public Text getMavenName() {
	return textName;
    }

    /**
     * Getter for Maven Description.
     * 
     * @return The text object for Maven description.
     */
    public Text getMavenDescription() {
	return textDescription;
    }
}