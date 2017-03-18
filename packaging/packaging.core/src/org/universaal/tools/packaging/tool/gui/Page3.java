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

import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.universaal.tools.packaging.tool.api.Page;
import org.universaal.tools.packaging.tool.impl.PageImpl;
import org.universaal.tools.packaging.tool.parts.Capability;
import org.universaal.tools.packaging.tool.util.XSDParser;
import org.universaal.tools.packaging.tool.validators.AlphabeticV;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class Page3 extends PageImpl {

	private Combo /*targetSpace,*/ mw_version, targetContainerName, targetContainerVersion;
	private TextExt /*targetSpaceVersion,*/ targetOntologies /*,targetContainerVersion, targetDeploymentTool*/;
	private Button ckbMoreReqs;
	
	protected Page3(String pageName) {
		super(pageName, "Specify capabilities of the Application");
	}

	public void createControl(Composite parent) {

		XSDParser XSDtooltip = XSDParser.get(XSD_VERSION);
		
		container = new Composite(parent, SWT.NULL);
		setControl(container);		

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.numColumns = 2;
		gd = new GridData(GridData.FILL_HORIZONTAL);

		Properties capabilities = app.getAppCapabilities().getCapabilities();
		
		Label l3 = new Label(container, SWT.NULL);
		mw_version = new Combo(container, SWT.READ_ONLY);
		mandatory.add(mw_version);
		l3.setText("* Middleware Version");
		for(int i = 0; i < RequirementsDefinitions.get().listRequirements("MW_Version").size(); i++)
			mw_version.add(RequirementsDefinitions.get().listRequirements("MW_Version").get(i));
		mw_version.setText(capabilities.getProperty(Capability.MANDATORY_MW_VERSION));			
		mw_version.setLayoutData(gd);	

		Label l4 = new Label(container, SWT.NULL);
		targetOntologies = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(targetOntologies);
		l4.setText("Ontologies web address(es), comma separated (if any)");
		targetOntologies.setText(capabilities.getProperty(Capability.MANDATORY_ONTOLOGIES));			
		targetOntologies.addVerifyListener(new AlphabeticV());
		targetOntologies.setLayoutData(gd);	
		targetOntologies.addTooltip(XSDtooltip.find("app.applicationOntology"));
		
		Label l5 = new Label(container, SWT.NULL);
		targetContainerName = new Combo(container, SWT.READ_ONLY);
		//mandatory.add(targetContainerName);
		l5.setText("Target Container Name");
		for(int i = 0; i < RequirementsDefinitions.get().listRequirements("Container_Name").size(); i++)
			targetContainerName.add(RequirementsDefinitions.get().listRequirements("Container_Name").get(i));
		targetContainerName.setText(capabilities.getProperty(Capability.MANDATORY_TARGET_CONTAINER_NAME));			
		targetContainerName.setLayoutData(gd);	

		Label l6 = new Label(container, SWT.NULL);
		targetContainerVersion = new Combo(container, SWT.READ_ONLY);
		//mandatory.add(targetContainerVersion);
		l6.setText("Target Container Version");
		for(int i = 0; i < RequirementsDefinitions.get().listRequirements("Container_Version").size(); i++)
			targetContainerVersion.add(RequirementsDefinitions.get().listRequirements("Container_Version").get(i));
		targetContainerVersion.setText(capabilities.getProperty(Capability.MANDATORY_TARGET_CONTAINER_VERSION));			
		targetContainerVersion.setLayoutData(gd);	
		
		Label l7 = new Label(container, SWT.NULL);
		l7.setText("Add custom requirements");
		ckbMoreReqs = new Button(container, SWT.CHECK);
		
		mw_version.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				app.getAppCapabilities().setCapability(Capability.MANDATORY_MW_VERSION, mw_version.getText());				
				setPageComplete(validate());
			}
		});
		
		targetOntologies.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				app.getAppCapabilities().setCapability(Capability.MANDATORY_ONTOLOGIES, targetOntologies.getText());				
			}
		});
		
		targetContainerName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				app.getAppCapabilities().setCapability(Capability.MANDATORY_TARGET_CONTAINER_NAME, targetContainerName.getText());				
			}
		});
		
		targetContainerVersion.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				app.getAppCapabilities().setCapability(Capability.MANDATORY_TARGET_CONTAINER_VERSION, targetContainerVersion.getText());				
			}
		});
		
		setPageComplete(validate());
	}
	
	/*
	@Override
	public IWizardPage getPreviousPage() {

		if(!app.getApplication().getLicenses().isEmpty())
			return super.getPreviousPage();

		return super.getPreviousPage().getPreviousPage();
	}
	*/
	
	@Override
	public IWizardPage getNextPage(){
		if (ckbMoreReqs.getSelection()){
			return super.getWizard().getPage(Page.PAGE4);
		}
		else{
			app.getAppRequirements().clear();
			return super.getWizard().getPage(Page.PAGE5);
		}
	}
	
	@Override
	public boolean nextPressed(){
		//serializeMPA();
		return true;
	}

}