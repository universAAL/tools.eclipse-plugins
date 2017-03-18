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

import java.util.Collection;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.pde.ui.launcher.AbstractLauncherTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.universaal.uaalpax.maven.MavenDependencyResolver;
import org.universaal.uaalpax.model.BundleChangeListener;
import org.universaal.uaalpax.model.BundleEntry;
import org.universaal.uaalpax.model.BundleModel;
import org.universaal.uaalpax.model.ModelDialogProvider;
import org.universaal.uaalpax.versionprovider.UAALVersionProvider;
import org.universaal.uaalpax.versionprovider.XMLVersionProvider;

public class UniversAALTab extends AbstractLauncherTab implements BundleChangeListener, ModelDialogProvider {
	private WorkspaceProjectsBlock managerTable;
	private AllBundlesBlock additionalLibsBlock;
	private VersionBlock versionBlock;
	private FeaturesBlock featuresBlock;
	private UAALVersionProvider versionProvider;
	
	boolean deactivated = true;
	
	private BundleModel model;
	
	private boolean m_initializing;
	
	public UniversAALTab() {
		m_initializing = false;
		versionProvider = new XMLVersionProvider(); // new HardcodedConfigProvider();
		
		model = new BundleModel(versionProvider, this);
		model.addChangeListener(this);
	}
	
	public Shell getShell() {
		return super.getShell();
	}
	
	public void createControl(Composite parent) {
		MavenDependencyResolver.getResolver().setGUIParent(parent);
		MavenDependencyResolver.getResolver().clearCache();
		
		Composite container = new Composite(parent, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		versionBlock = new VersionBlock(this, container, SWT.NONE);
		versionBlock.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		featuresBlock = new FeaturesBlock(this, container, SWT.NONE);
		featuresBlock.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		SashForm sf = new SashForm(container, SWT.VERTICAL);
		sf.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		managerTable = new WorkspaceProjectsBlock(this, sf, SWT.NONE);
		additionalLibsBlock = new AllBundlesBlock(this, sf, SWT.NONE);
		
		setControl(container);
		
		// order is important
		model.addPresenter(versionBlock);
		model.addPresenter(featuresBlock);
		model.addPresenter(managerTable);
		model.addPresenter(additionalLibsBlock);
	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}
	
	public void initializeFrom(ILaunchConfiguration configuration) {
		boolean deactivated = this.deactivated;
		m_initializing = true;
		try {
			model.updateModel(configuration);
		} finally {
			m_initializing = false;
		}
		this.deactivated = deactivated;
	}
	
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (!deactivated)
			model.performApply(configuration);
	}
	
	public BundleModel getModel() {
		return model;
	}
	
	public String getName() {
		return "uAAL Runner";
	}
	
	@Override
	public void validateTab() {
		// no validation required
	}
	
	@Override
	public void updateLaunchConfigurationDialog() {
		if (!m_initializing) {
			super.updateLaunchConfigurationDialog();
		}
	}
	
	public void notifyChanged() {
		deactivated = false;
		updateLaunchConfigurationDialog();
	}
	
	public void addBundle(BundleEntry be) {
		getModel().add(be);
	}
	
	public void addAllBundles(Collection<BundleEntry> entries) {
		getModel().addAll(entries);
	}
	
	public void removeBundle(BundleEntry be) {
		getModel().remove(be);
	}
	
	public void removeAllBundles(Collection<BundleEntry> entries) {
		getModel().removeAll(entries);
	}
	
	public int openDialog(String title, String message, String... buttons) {
		return new MessageDialog(getShell(), title, null, message, MessageDialog.QUESTION, buttons, 0).open();
	}
	
	public UAALVersionProvider getVersionProvider() {
		return versionProvider;
	}
	
	public void showErrorMessage(String title, String message) {
		MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
		mb.setMessage(message);
		mb.setText(title);
		mb.open();
	}
	
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		// deactivated = false;
		initializeFrom(workingCopy);
	}
	
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		performApply(workingCopy);
		deactivated = true;
	}
}
