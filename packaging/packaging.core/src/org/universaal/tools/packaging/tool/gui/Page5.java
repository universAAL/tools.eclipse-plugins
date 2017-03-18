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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.universaal.tools.packaging.tool.api.Page;
import org.universaal.tools.packaging.tool.impl.PageImpl;
import org.universaal.tools.packaging.tool.parts.ApplicationManagement.RemoteManagement;
import org.universaal.tools.packaging.tool.util.XSDParser;
import org.universaal.tools.packaging.tool.validators.AlphabeticV;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class Page5 extends PageImpl {

	private TextExt contact;
	private List<TextExt> artifacts;
	private List<TextExt> protocols;
	private List<TextExt> versions;

	protected Page5(String pageName) {
		super(pageName, "Specify details for assistance");

		artifacts = new ArrayList<TextExt>();
		protocols = new ArrayList<TextExt>();
		versions = new ArrayList<TextExt>();
	}

	public void createControl(Composite parent) {
		
		XSDParser XSDtooltip = XSDParser.get(XSD_VERSION);
		
		container = new Composite(parent, SWT.NULL);
		setControl(container);	

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.numColumns = 2;
		gd = new GridData(GridData.FILL_HORIZONTAL);

		List<IProject> parts = GUI.getInstance().getParts();

		List<RemoteManagement> remoteM = app.getAppManagement().getRemoteManagement();
		while(remoteM.size() < parts.size()){
			remoteM.add(app.getAppManagement().new RemoteManagement());
		}

		Label l1 = new Label(container, SWT.NULL);
		contact = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(contact);
		l1.setText("Contact Person");
		contact.setText(app.getAppManagement().getContact());		
		contact.addVerifyListener(new AlphabeticV());
		contact.setLayoutData(gd);
		contact.addTooltip(XSDtooltip.find("applicationManagement.contactPoint"));
		contact.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				app.getAppManagement().setContact(contact.getText());
			}
		});

		Label shadow_sep_h = new Label(container, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
		shadow_sep_h.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label shadow_sep_h1 = new Label(container, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
		shadow_sep_h1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for(int k = 0; k < parts.size(); k++){

			//POMParser p = new POMParser(new File(parts.get(k).getFile("pom.xml").getLocation()+""));
			//EffectivePOMContainer.setDocument(parts.get(k).getName());
			
			Label l2 = new Label(container, SWT.NULL);
			l2.setText("Artifact #"+(k+1)+" ID");
			TextExt artifact = new TextExt(container, SWT.BORDER | SWT.SINGLE);
			//mandatory.add(artifact);
			artifact.setText(app.getAppManagement().getRemoteManagement().get(k).getSoftware().getArtifactID());			
			artifacts.add(artifact);
			artifact.addVerifyListener(new AlphabeticV());
			artifact.addKeyListener(new FullListener());
			artifact.setLayoutData(gd);

			Label l3 = new Label(container, SWT.NULL);
			l3.setText("Protocols for assistance, comma separated");
			TextExt protocol = new TextExt(container, SWT.BORDER | SWT.SINGLE);
			try{
				protocol.setText(protocols.get(k).getText());
			} catch(IndexOutOfBoundsException e){
				protocol.setText("");
			}
			//mandatory.add(protocol);			
			protocols.add(protocol);
			protocol.addKeyListener(new FullListener());
			protocol.setLayoutData(gd);
			protocol.addTooltip(XSDtooltip.find("applicationManagement.remoteManagement"));
			
			Label l4 = new Label(container, SWT.NULL);
			l4.setText("Version");
			TextExt version = new TextExt(container, SWT.BORDER | SWT.SINGLE);
			//mandatory.add(version);
			version.setText(app.getAppManagement().getRemoteManagement().get(k).getSoftware().getVersion().getVersion());
			versions.add(version);
			version.addKeyListener(new FullListener());
			version.setLayoutData(gd);			

			if(k != (parts.size() - 1)){
				Label shadow_sep_h2 = new Label(container, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
				shadow_sep_h2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				Label shadow_sep_h3 = new Label(container, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);		
				shadow_sep_h3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			}
		}

		setPageComplete(validate());
	}

	@Override
	public IWizardPage getPreviousPage() {

		
		if(app.getAppRequirements().getRequirementsList().size() > 0){
			return super.getWizard().getPage(Page.PAGE4);
		}
		return super.getWizard().getPage(Page.PAGE3);
	}
	
	
	@Override
	public boolean nextPressed() {

		for(int j = 0; j < artifacts.size(); j++){

			app.getAppManagement().getRemoteManagement().get(j).getProtocols().clear();
			app.getAppManagement().getRemoteManagement().get(j).getSoftware().setArtifactID(artifacts.get(j).getText());
			app.getAppManagement().getRemoteManagement().get(j).getSoftware().getVersion().setVersion(versions.get(j).getText());

			String[] ps = protocols.get(j).getText().split(",");
			for(int i = 0; i < ps.length; i++)
				if(ps[i] != null)
					app.getAppManagement().getRemoteManagement().get(j).getProtocols().add(ps[i]);
		}
		//serializeMPA();
		return true;
	}

}