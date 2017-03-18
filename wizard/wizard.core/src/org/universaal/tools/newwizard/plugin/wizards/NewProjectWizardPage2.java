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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jdt.core.JavaConventions;
import org.universaal.tools.newwizard.plugin.Activator;
import org.universaal.tools.newwizard.plugin.versions.IMWVersion;
import org.universaal.tools.newwizard.plugin.versions.MWVersionFactory;

/**
 * The second wizard page allows setting the name of the root package, where all
 * wrapper classes will be created. The wrapper classes can be selected in this
 * page, depending on what buses do you want the project to connect to.
 */

public class NewProjectWizardPage2 extends WizardPage {

    private IMWVersion mwVersion;
    private Button[] checks;
    private Button checkTemp;
    private Combo dropTemp, dropMW;
    private Text textPackage;
    private Group containerClasses;

    /**
     * Default constructor with arg.
     * 
     * @param selection
     *            Used to determine working set(?).
     */
    public NewProjectWizardPage2(ISelection selection) {
	super("projectWizardPage2"); //$NON-NLS-1$
	setTitle(Messages.getString("Page2.0")); //$NON-NLS-1$
	setDescription(Messages.getString("Page2.1")); //$NON-NLS-1$
    }

    public void createControl(Composite parent) {
	Composite containerParent = new Composite(parent, SWT.NULL);

	GridLayout layoutParent = new GridLayout();
	layoutParent.numColumns = 1;
	layoutParent.verticalSpacing = 9;
	containerParent.setLayout(layoutParent);

	// Set the help
	PlatformUI.getWorkbench().getHelpSystem()
	.setHelp(parent, Activator.PLUGIN_ID + ".help_project");

	// First layout with the name of the package & template____________________________
	Composite containerInfo = new Composite(containerParent, SWT.NULL);
	GridLayout layoutInfo = new GridLayout();
	layoutInfo.numColumns = 2;
	layoutInfo.verticalSpacing = 9;
	containerInfo.setLayout(layoutInfo);
	containerInfo.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));

	// Name of the package
	Label label4 = new Label(containerInfo, SWT.NULL);
	label4.setText(Messages.getString("Page2.2")); //$NON-NLS-1$
	textPackage = new Text(containerInfo, SWT.BORDER | SWT.SINGLE);
	textPackage.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	textPackage.addModifyListener(new ModifyListener() {
	    public void modifyText(ModifyEvent e) {
		validateInput();
	    }
	});

	// Dropdown with middleware versions
	Label label6 = new Label(containerInfo, SWT.NULL);
	label6.setText(Messages.getString("Page2.3")); //$NON-NLS-1$
	dropMW = new Combo(containerInfo, SWT.READ_ONLY);
	dropMW.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	dropMW.setItems(MWVersionFactory.getAllVERnames());
	dropMW.select(MWVersionFactory.getAllVERnames().length-1);//Default: last
	mwVersion=MWVersionFactory.getMWVersion(dropMW.getSelectionIndex());
	dropMW.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		mwVersion=MWVersionFactory.getMWVersion(dropMW.getSelectionIndex());
		updateCheckClasses();
		validateInput();
	    }
	});

	// Dropdown with template of full project
	Label label5 = new Label(containerInfo, SWT.NULL);
	label5.setText(Messages.getString("Page2.4")); //$NON-NLS-1$
	dropTemp = new Combo(containerInfo, SWT.READ_ONLY);
	dropTemp.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	dropTemp.add(getAPPname(IMWVersion.APP_FULL), 0); //$NON-NLS-1$
	dropTemp.add(getAPPname(IMWVersion.APP_NOGUI), 1); //$NON-NLS-1$
	dropTemp.add(getAPPname(IMWVersion.APP_GAUGE), 2); //$NON-NLS-1$
	dropTemp.add(getAPPname(IMWVersion.APP_ACT), 3); //$NON-NLS-1$
	dropTemp.add(getAPPname(IMWVersion.APP_REASON), 4); //$NON-NLS-1$
	dropTemp.add(getAPPname(IMWVersion.APP_HANDLER), 5); //$NON-NLS-1$
	dropTemp.select(-1);//Default: none
	dropTemp.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		//TODO: This disables template when APP_HANDLER
		if (dropTemp.getSelectionIndex() < 0
			|| dropTemp.getSelectionIndex() == 5) {
		    checkTemp.setSelection(false);
		    checkTemp.setEnabled(false);
		} else {
		    checkTemp.setEnabled(true);
		}
		updateCheckClasses();
	    }
	});

	// Empty placeholder
	Label empty = new Label(containerInfo, SWT.NULL);
	empty.setText(" "); //$NON-NLS-1$

	// Checkbox for full template project
	checkTemp = new Button(containerInfo, SWT.CHECK);
	checkTemp.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));
	checkTemp.setText(Messages.getString("Page2.5")); //$NON-NLS-1$
	checkTemp.setEnabled(false);
	checkTemp.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		updateCheckClasses();
	    }
	});

	// Second layout with the checkboxes of classes____________________________________
	containerClasses = new Group(containerParent, SWT.NONE);
	containerClasses.setText(Messages.getString("Page2.6")); //$NON-NLS-1$
	GridLayout layoutClasses = new GridLayout();
	layoutClasses.numColumns = 3;
	layoutClasses.verticalSpacing = 9;
	containerClasses.setLayout(layoutClasses);
	containerClasses.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true,false));

	GridData gd = new GridData(GridData.FILL,GridData.CENTER,true,false);

	checks=new Button[10];
	// SCallee
	checks[0] = new Button(containerClasses, SWT.CHECK);
	checks[0].setLayoutData(gd);
	// SCaller
	checks[1] = new Button(containerClasses, SWT.CHECK);
	checks[1].setLayoutData(gd);
	checks[1].addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		if (checks[2] != null) {
		    if (checks[1].getSelection()) {
			checks[2].setEnabled(true);
		    } else {
			checks[2].setSelection(false);
			checks[2].setEnabled(false);
		    }
		}
	    }
	});
	// DefaultSCaller
	checks[2] = new Button(containerClasses, SWT.CHECK);
	checks[2].setLayoutData(gd);
	checks[2].setEnabled(false);
	// CSubscriber
	checks[3] = new Button(containerClasses, SWT.CHECK);
	checks[3].setLayoutData(gd);
	// CPublisher
	checks[4] = new Button(containerClasses, SWT.CHECK);
	checks[4].setLayoutData(gd);
	checks[4].addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		if (checks[5] != null) {
		    if (checks[4].getSelection()) {
			checks[5].setEnabled(true);
		    } else {
			checks[5].setSelection(false);
			checks[5].setEnabled(false);
		    }
		}
	    }
	});
	// DefaultSCaller
	checks[5] = new Button(containerClasses, SWT.CHECK);
	checks[5].setLayoutData(gd);
	checks[5].setEnabled(false);
	// OSubscriber / UIHandler
	checks[6] = new Button(containerClasses, SWT.CHECK);
	checks[6].setLayoutData(gd);
	// OPublisher / UICaller
	checks[7] = new Button(containerClasses, SWT.CHECK);
	checks[7].setLayoutData(gd);
	// Empty placeholder
	Label empty1 = new Label(containerClasses, SWT.NULL);
	empty1.setText(" "); //$NON-NLS-1$
	// ISubscriber
	checks[8] = new Button(containerClasses, SWT.CHECK);
	checks[8].setLayoutData(gd);
	// IPublisher
	checks[9] = new Button(containerClasses, SWT.CHECK);
	checks[9].setLayoutData(gd);
	// Empty placeholder
	Label empty2 = new Label(containerClasses, SWT.NULL);
	empty2.setText(" "); //$NON-NLS-1$

	// Start and validate
	setControl(containerParent);
	updateCheckClasses();
	validateInput();
    }

    /**
     * Ensures that package field is set and compliant with Java, and MW version is selected. Uses
     * deprecated validatePackageName method. Didnt find the new alternative.
     */
    private void validateInput() {
	String packageName = textPackage.getText();
	//Check package name
	if (packageName.trim().length() != 0) {
	    // @SuppressWarnings("deprecation")
	    IStatus status = JavaConventions.validatePackageName(packageName);
	    // TODO: Use new method to check package naming.
	    if (!status.isOK()) {
		setErrorMessage(status.getMessage());
		setPageComplete(false);
		return;
	    }
	} else {
	    setMessage(Messages.getString("Page2.7")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}
	//Check MW version
	if (dropMW.getSelectionIndex() < 0) {
	    setMessage(Messages.getString("Page2.8")); //$NON-NLS-1$
	    setPageComplete(false);
	    return;
	}
	setPageComplete(true);
	setErrorMessage(null);
	setMessage(null);
    }

    /**
     * Reloads the available classes checkboxes depending on MW version.
     */
    private void updateCheckClasses() {
	int app = dropTemp.getSelectionIndex();
	String[] name = mwVersion.getChecksLabels();
	boolean[] visible = mwVersion.getChecksVisible();
	boolean[] active = mwVersion.getChecksActiveByApp(app);
	for (int i = 0; i < checks.length; i++) {
	    checks[i].setText(Messages.getString(name[i]));
	    checks[i].setVisible(visible[i]);
	    checks[i].setEnabled(!checkTemp.getSelection() && (((i!=2) && (i!=5)) || (i==2 && active[1]) || (i==5 && active[4])) );
	    checks[i].setSelection(active[i]);
	}
    }

    /**
     * Gets the name of the template application to display.
     * 
     * @param version
     *            Index of the dropdown with the templates.
     * @return The String with the name.
     */
    private static String getAPPname(int app){
	switch (app) {
	case IMWVersion.APP_FULL:
	    return Messages.getString("Page2.40");
	case IMWVersion.APP_NOGUI:
	    return Messages.getString("Page2.41");
	case IMWVersion.APP_GAUGE:
	    return Messages.getString("Page2.42");
	case IMWVersion.APP_ACT:
	    return Messages.getString("Page2.43");
	case IMWVersion.APP_REASON:
	    return Messages.getString("Page2.44");
	case IMWVersion.APP_HANDLER:
	    return Messages.getString("Page2.45");
	default:
	    return "Unknown";
	}
    }

    //_______GETTERS_______

    /**
     * Getter for CheckClasses.
     * 
     * @return The array of button objects for checked classes.
     */
    public Button[] getCheckClasses(){
	return checks;
    }

    /**
     * Getter for TextPackage.
     * 
     * @return The text object for the package.
     */
    public Text getTextPackage() {
	return textPackage;
    }

    /**
     * Getter for CheckTemp.
     * 
     * @return The Button object for the template.
     */
    public Button getCheckTemp() {
	return checkTemp;
    }

    /**
     * Getter for DropTemp.
     * 
     * @return The Drop object for the template.
     */
    public Combo getDropTemp() {
	return dropTemp;
    }

    /**
     * Getter for MWVersion.
     * 
     * @return The IMWversion with the version.specific commands.
     */
    public IMWVersion getMWVersion(){
	return mwVersion;
    }
}