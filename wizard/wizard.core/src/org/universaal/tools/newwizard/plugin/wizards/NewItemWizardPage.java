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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage; //import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.universaal.tools.newwizard.plugin.Activator;
import org.universaal.tools.newwizard.plugin.versions.IMWVersion;
import org.universaal.tools.newwizard.plugin.versions.MWVersionFactory;

/**
 * The only wizard page allows setting the basic details for the new item, like
 * type, package and name.
 */
public class NewItemWizardPage extends NewTypeWizardPage {
    private Combo dropClass,dropMW;
    private Text textClass;
    private IMWVersion mwVersion;

    /**
     * Default constructor with arg.
     * 
     * @param selection
     *            Used to determine where to create item.
     */
    public NewItemWizardPage(ISelection selection) {
	super(true, "projectItemPage1"); //$NON-NLS-1$
	setTitle(Messages.getString("PageI.0")); //$NON-NLS-1$
	setDescription(Messages.getString("PageI.1")); //$NON-NLS-1$
    }

    /**
     * Used to setup initial content of page
     * 
     * @param selection
     *            Used to determine where to create item.
     */
    public void init(IStructuredSelection selection) {
	IJavaElement jelem = getInitialJavaElement(selection);
	initContainerPage(jelem);
	initTypePage(jelem);
	validateInput();
	setPageComplete(false);
    }

    /**
     * Used to update and validate content, because of extending
     * NewTypeWizardPage.
     */
    private void doStatusUpdate() {
	IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus };
	updateStatus(status);
    }

    public void createControl(Composite parent) {
	//Set default package thanks to NewTypeWizardPage
	initializeDialogUnits(parent);
	
	Composite containerParent = new Composite(parent, SWT.NULL);
	
	GridLayout layoutParent = new GridLayout();
	layoutParent.numColumns = 1;
	layoutParent.verticalSpacing = 9;
	containerParent.setLayout(layoutParent);
	
	// Set the help
	PlatformUI.getWorkbench().getHelpSystem()
		.setHelp(parent, Activator.PLUGIN_ID + ".help_item");
	
	// First layout with the name of the package___________________
	Composite containerInfo = new Composite(containerParent, SWT.NULL);
	GridLayout layoutInfo = new GridLayout();
	layoutInfo.numColumns = 4;
	layoutInfo.verticalSpacing = 9;
	containerInfo.setLayout(layoutInfo);
	containerInfo.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	
	// this is provided by NewTypeWizardPage
	createContainerControls(containerInfo, 4);
	createPackageControls(containerInfo, 4);
	
	// Dropdown with middleware versions
	Label label6 = new Label(containerInfo, SWT.NULL);
	label6.setText(Messages.getString("PageI.2")); //$NON-NLS-1$
	dropMW = new Combo(containerInfo, SWT.READ_ONLY);
	dropMW.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	dropMW.setItems(MWVersionFactory.getAllVERnames());
	dropMW.select(MWVersionFactory.getAllVERnames().length-1);//Default: last
	mwVersion=MWVersionFactory.getMWVersion(dropMW.getSelectionIndex());
	dropMW.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		mwVersion=MWVersionFactory.getMWVersion(dropMW.getSelectionIndex());
		updateDropClass();
		validateInput();
	    }
	});
	
	//TODO: Remove this when we auto-select compliance
	Label label7 = new Label(containerParent, SWT.NULL);
	label7.setText(Messages.getString("PageI.3")); //$NON-NLS-1$

	// Second layout with class selector_____________________________
	Group containerClass = new Group(containerParent, SWT.NONE);
	containerClass.setText(Messages.getString("PageI.4")); //$NON-NLS-1$
	GridLayout layoutClass = new GridLayout();
	layoutClass.numColumns = 3;
	layoutClass.verticalSpacing = 9;
	containerClass.setLayout(layoutClass);
	containerClass.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));

	// Name of the class
	Label label4 = new Label(containerClass, SWT.NULL);
	label4.setText(Messages.getString("PageI.5")); //$NON-NLS-1$
	textClass = new Text(containerClass, SWT.BORDER | SWT.SINGLE);
	textClass.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	textClass.addPaintListener(new PaintListener() {
	    public void paintControl(PaintEvent arg0) {
		validateInput();
	    }
	});
	textClass.addModifyListener(new ModifyListener() {
	    public void modifyText(ModifyEvent e) {
		validateInput();
	    }
	});

	// Dropdown with type of item
	dropClass = new Combo(containerClass, SWT.READ_ONLY);
	dropClass.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	dropClass.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		validateInput();
	    }
	});
	
	// Start and validate
	setControl(containerParent);
	updateDropClass();
	validateInput();
    }
    
    private void validateInput() {
	//Check selectors not null
	if (textClass == null || dropClass == null) {
	    setMessage(Messages.getString("PageI.6")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}
	//Check class name not empty
	if (textClass.getText().isEmpty()) {
	    setErrorMessage(Messages.getString("PageI.7")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}
	//Check valid class name
	String clsName = textClass.getText() + ".class"; //$NON-NLS-1$
	if (clsName.trim().length() != 0) {
	    IStatus status = JavaConventions.validateClassFileName(clsName);
	    // TODO: Use new method to check class naming.
	    if (!status.isOK()) {
		setErrorMessage(status.getMessage());
		setPageComplete(false);
		return;
	    }
	} else {
	    setErrorMessage(Messages.getString("PageI.8")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}
	//Check selected class
	if (dropClass.getSelectionIndex() < 0) {
	    setErrorMessage(Messages.getString("PageI.9")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}
	//Check selected MW
	if(dropMW.getSelectionIndex() < 0){
	    setMessage(Messages.getString("PageI.10")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}
	setPageComplete(true);
	setErrorMessage(null);
	setMessage(null);
	doStatusUpdate();
    }
    
    /**
     * Reload available types of classes when MW is changed.
     */
    private void updateDropClass() {
	dropClass.deselectAll();
	dropClass.clearSelection();
	dropClass.removeAll();
	String[] clas = mwVersion.getClassesLabels();
	for (int i = 0; i < clas.length; i++) {
	    dropClass.add(Messages.getString(clas[i]));
	}
    }
    
    protected void handleFieldChanged(String fieldName) {
	super.handleFieldChanged(fieldName);
	validateInput();
    }
    
    //________GETTERS________

    /**
     * Getter for DropClass.
     * 
     * @return The Dropdown object for class selector.
     */
    public Combo getDropClass() {
	return dropClass;
    }

    /**
     * Getter for TextClass.
     * 
     * @return The Text object with the name of the class.
     */
    public Text getTextClass() {
	return textClass;
    }
    
    /**
     * Getter for the MWVersion
     * 
     * @return The IMWVersion controlling the version-specific commands
     */
    public IMWVersion getMWVersion() {
	return mwVersion;
    }
    

}
