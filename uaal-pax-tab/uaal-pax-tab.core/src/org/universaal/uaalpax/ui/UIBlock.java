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

package org.universaal.uaalpax.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.universaal.uaalpax.model.BundlePresenter;

public abstract class UIBlock extends Composite implements LaunchChangeListener, BundlePresenter {
	private final UniversAALTab uAALTab;
	
	public UIBlock(UniversAALTab uAALTab, Composite parent, int style) {
		super(parent, style);
		this.uAALTab = uAALTab;
		
		setLayout(new GridLayout());
		final Group group = new Group(this, SWT.NONE);
		// group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setText(getBlockName());
		
		initBlock(group);
	}
	
	public UniversAALTab getUAALTab() {
		return uAALTab;
	}
	
	public abstract String getBlockName();
	
	public abstract void initBlock(Composite parent);
	
	public void notifyChanged() {
		uAALTab.updateLaunchConfigurationDialog();
	}
}
