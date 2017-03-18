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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.universaal.tools.packaging.tool.impl.PageImpl;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class EndPage extends PageImpl {

	protected EndPage(String pageName) {
		super(pageName, "This is the ending page for the Application Packager");
	}

	public void createControl(Composite parent) { 

		container = new Composite(parent, SWT.NULL);
		setControl(container);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.numColumns = 1;
		gd = new GridData(GridData.FILL_HORIZONTAL);

		Label end = new Label(container, SWT.NULL);
		end.setText("Congratulations, you have packaged your universAAL application in the UAPP container, press Finish to save it." +
				"\nIf you also want to upload it automatically to the uStore please follow the next wizard." +
				"\nIf not, the file will be available at the location you chosed initially.");		

		FontData[] fD = end.getFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		end.setFont(new Font(container.getDisplay(), fD[0]));		

		Label end0 = new Label(container, SWT.NULL);
		end0.setText("");	

		setPageComplete(true);
	}
}