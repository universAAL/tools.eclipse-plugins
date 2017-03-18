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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.universaal.tools.packaging.tool.impl.PageImpl;
import org.universaal.tools.packaging.tool.validators.AlphabeticV;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class PagePartBundle extends PageImpl {

	private int partNumber;

	private Text bundleId, bundleVersion;
	
	protected PagePartBundle(String pageName, int pn) {
		super(pageName, "Part "+(pn+1)+"/"+GUI.getInstance().getPartsCount()+
				" - Specify Bundle Id and Version");
		this.partNumber = pn;
	}

	public void createControl(final Composite parent) {

		container = new Composite(parent, SWT.NULL);
		setControl(container);	

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.numColumns = 2;
		gd = new GridData(GridData.FILL_HORIZONTAL);

		Label l1 = new Label(container, SWT.NULL);
		bundleId = new Text(container, SWT.BORDER | SWT.SINGLE);
		mandatory.add(bundleId);
		l1.setText("* Bundle Id");
		bundleId.addVerifyListener(new AlphabeticV());
		bundleId.setLayoutData(gd);

		Label l2 = new Label(container, SWT.NULL);
		bundleVersion = new Text(container, SWT.BORDER | SWT.SINGLE);
		mandatory.add(bundleVersion);
		l2.setText("* Bundle Version");
		bundleVersion.addVerifyListener(new AlphabeticV());
		bundleVersion.setLayoutData(gd);	
		
		bundleId.addKeyListener(new FullListener());
		bundleVersion.addKeyListener(new FullListener());
		
		loadDefaultValues();
		setPageComplete(validate());
	}

	public void loadDefaultValues(){
		bundleId.setText(app.getAppParts().get(partNumber).getPartBundleId());
		bundleVersion.setText(app.getAppParts().get(partNumber).getPartBundleVersion());
	}
	
	@Override
	public boolean nextPressed() {

		try{
			app.getAppParts().get(partNumber).setPartBundle(bundleId.getText(),bundleVersion.getText());
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		//serializeMPA();
		return true;
	}

}