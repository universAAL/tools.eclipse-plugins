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
package org.universaal.tools.packaging.tool.gui;

import java.net.URI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.universaal.tools.packaging.tool.impl.PageImpl;
import org.universaal.tools.packaging.tool.parts.DeploymentUnit;
import org.universaal.tools.packaging.tool.parts.Embedding;
import org.universaal.tools.packaging.tool.validators.AlphabeticV;
import org.universaal.tools.packaging.tool.validators.UriV;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class PageDU extends PageImpl {

	private Combo os1, platform1, cu1, emb1;
	private Text andN, andD, andURI;
	private Button ckbOS1, ckbPL1, ckbCU1, ckbKar;

	protected PageDU(String pageName) {
		super(pageName, "Specify deployment requirements for the Application (the parts will inherit them)");
	}

	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NULL);
		setControl(container);	

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.numColumns = 2;
		gd = new GridData(GridData.FILL_HORIZONTAL);

		Label exp = new Label(container, SWT.NULL);
		exp.setText("You can choose alternatively an OS, a Platform or a Container.");
		Label blk = new Label(container, SWT.NULL);
		blk.setText("(only Karaf container is now fully supported)");

		FontData[] fD = exp.getFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		exp.setFont(new Font(container.getDisplay(), fD[0]));		

		Label os = new Label(container, SWT.NULL);
		os.setText("Select this checkbox to add an OS as deployment unit");
		ckbOS1 = new Button(container, SWT.CHECK);
		ckbOS1.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				enableOS();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label l1 = new Label(container, SWT.NULL);
		l1.setText("Select this checkbox to add a Platform as deployment unit");
		ckbPL1 = new Button(container, SWT.CHECK);
		ckbPL1.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				enablePlatform();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label l2 = new Label(container, SWT.NULL);
		l2.setText("Select this checkbox to add a specific Container as deployment unit");
		ckbCU1 = new Button(container, SWT.CHECK);
		ckbCU1.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				enableCU();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});		
		
		Label label1 = new Label(container, SWT.NULL);
		os1 = new Combo(container, SWT.READ_ONLY);
		label1.setText("OS requirement");
		for(int i = 0; i < RequirementsDefinitions.get().listRequirements("OS_Requirement").size(); i++)
			os1.add(RequirementsDefinitions.get().listRequirements("OS_Requirement").get(i));
		os1.setLayoutData(gd);

		Label label2 = new Label(container, SWT.NULL);
		platform1 = new Combo(container, SWT.READ_ONLY);
		label2.setText("Platform requirement");
		for(int i = 0; i < RequirementsDefinitions.get().listRequirements("Platform_Requirement").size(); i++)
			platform1.add(RequirementsDefinitions.get().listRequirements("Platform_Requirement").get(i));
		platform1.setLayoutData(gd);

		Label label3 = new Label(container, SWT.NULL);
		cu1 = new Combo(container, SWT.READ_ONLY);
		label3.setText("Container requirement");
		for(int i = 0; i < RequirementsDefinitions.get().listRequirements("Container_Name").size(); i++)
			cu1.add(RequirementsDefinitions.get().listRequirements("Container_Name").get(i));
		cu1.setLayoutData(gd);
		cu1.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {

				enableControls(new ArrayList<Control>(Arrays.asList(emb1, ckbKar, andN, andD, andURI)));

				if(cu1.getText().equals("android"))
					disableControls(new ArrayList<Control>(Arrays.asList(emb1, ckbKar)));

				else if(cu1.getText().equals("karaf"))
					disableControls(new ArrayList<Control>(Arrays.asList(andN, andD, andURI)));

				else
					disableControls(new ArrayList<Control>(Arrays.asList(emb1, ckbKar, andN, andD, andURI)));
			}

			public void widgetDefaultSelected(SelectionEvent e) {				
			}
		});

		// embedding only applicable for KARAF value
		Label l3 = new Label(container, SWT.NULL);
		emb1 = new Combo(container, SWT.READ_ONLY);
		l3.setText("Embedding");
		for(int i = 0; i < Embedding.values().length; i++)
			emb1.add(Embedding.values()[i].toString());
		emb1.setLayoutData(gd);

		// ANDROID part
		Label l4 = new Label(container, SWT.NULL);
		andN = new Text(container, SWT.BORDER | SWT.SINGLE);
		l4.setText("Android part name");
		andN.addVerifyListener(new AlphabeticV());
		andN.setLayoutData(gd);

		Label l5 = new Label(container, SWT.NULL);
		andD = new Text(container, SWT.BORDER | SWT.SINGLE);
		l5.setText("Android part description");
		andD.addVerifyListener(new AlphabeticV());
		andD.setLayoutData(gd);

		Label l6 = new Label(container, SWT.NULL);
		andURI = new Text(container, SWT.BORDER | SWT.SINGLE);
		l6.setText("Android part URI");
		andURI.addVerifyListener(new UriV());
		andURI.setLayoutData(gd);

		Label empty1 = new Label(container, SWT.NULL);
		empty1.setText("");

		Label empty2 = new Label(container, SWT.NULL);
		empty2.setText("");
		//default configuration
		os1.select(0);
		platform1.select(0);
		cu1.select(0);
		emb1.setText(Embedding.anyContainer.toString());
		
		disableControls(new ArrayList<Control>(Arrays.asList(os1, platform1, /*cu1, emb1,*/ ckbKar, andN, andD, andURI)));
		setPageComplete(validate());
	}

	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		if(visible) loadData();
	}
	
	private void loadData(){
		if(app.getAppRequirements().deploymentUnitType.equals(DeploymentUnit.OS)) ckbOS1.notifyListeners(SWT.Selection, new Event());
		else if(app.getAppRequirements().deploymentUnitType.equals(DeploymentUnit.PLATFORM)) ckbPL1.notifyListeners(SWT.Selection, new Event());
		else if(app.getAppRequirements().deploymentUnitType.equals(DeploymentUnit.CONTAINER)) ckbCU1.notifyListeners(SWT.Selection, new Event());
		
		if(!app.getAppRequirements().OS_Requirements.isEmpty())  os1.setText(app.getAppRequirements().OS_Requirements);
		if(!app.getAppRequirements().Platform_Requirement.isEmpty())  platform1.setText(app.getAppRequirements().Platform_Requirement);
		if(!app.getAppRequirements().Container_Name.isEmpty())  cu1.setText(app.getAppRequirements().Container_Name);
		if(!app.getAppRequirements().embedding.isEmpty())  emb1.setText(app.getAppRequirements().embedding);
	}
	
	protected void enableCU() {
		ckbPL1.setSelection(false);
		ckbOS1.setSelection(false);
		ckbCU1.setSelection(true);

		enableControls(new ArrayList<Control>(Arrays.asList(os1, platform1, cu1, emb1, ckbKar, andN, andD, andURI)));
		disableControls(new ArrayList<Control>(Arrays.asList(os1, platform1, andN, /*ckbKar,*/ andD, andURI)));
	}

	protected void enablePlatform() {
		ckbPL1.setSelection(true);
		ckbOS1.setSelection(false);
		ckbCU1.setSelection(false);

		enableControls(new ArrayList<Control>(Arrays.asList(os1, platform1, cu1, emb1, ckbKar, andN, andD, andURI)));
		disableControls(new ArrayList<Control>(Arrays.asList(os1, cu1, emb1, ckbKar, andN, andD, andURI)));
	}

	private void enableOS() {
		ckbPL1.setSelection(false);
		ckbOS1.setSelection(true);
		ckbCU1.setSelection(false);

		enableControls(new ArrayList<Control>(Arrays.asList(os1, platform1, cu1, emb1, ckbKar, andN, andD, andURI)));
		disableControls(new ArrayList<Control>(Arrays.asList(platform1, cu1, emb1, andN, ckbKar, andD, andURI)));
	}
	public void setArtifact(IProject part){
	}

	@Override
	public boolean nextPressed() {

		if(ckbOS1.getSelection())
			app.getAppRequirements().deploymentUnitType = DeploymentUnit.OS;
		else if(ckbPL1.getSelection())
			app.getAppRequirements().deploymentUnitType = DeploymentUnit.PLATFORM;
		else if(ckbCU1.getSelection())
			app.getAppRequirements().deploymentUnitType = DeploymentUnit.CONTAINER;

		app.getAppRequirements().OS_Requirements = os1.getText();
		app.getAppRequirements().Platform_Requirement = platform1.getText();
		app.getAppRequirements().Container_Name = cu1.getText();
		app.getAppRequirements().embedding = emb1.getText();
		app.getAppRequirements().android.setName(andN.getText());
		app.getAppRequirements().android.setDescription(andD.getText());
		app.getAppRequirements().android.setLocation(URI.create(andURI.getText()));
		
		//serializeMPA();
		return true;
	}
	
	private void disableControls(List<Control> list){
		if(list != null && !list.isEmpty())
			for(int i = 0; i < list.size(); i++)
				if(list.get(i) != null)
					list.get(i).setEnabled(false);
	}

	private void enableControls(List<Control> list){
		if(list != null && !list.isEmpty())
			for(int i = 0; i < list.size(); i++)
				if(list.get(i) != null)
					list.get(i).setEnabled(true);
	}
}