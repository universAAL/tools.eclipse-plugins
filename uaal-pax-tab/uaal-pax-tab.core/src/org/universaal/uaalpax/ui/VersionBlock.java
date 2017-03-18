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

import java.util.Arrays;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.universaal.uaalpax.model.ArtifactURL;
import org.universaal.uaalpax.model.BundleEntry;
import org.universaal.uaalpax.model.BundleSet;
import org.universaal.uaalpax.model.BundleModel;

public class VersionBlock extends UIBlock {
	private Combo versionCombo;
	private String[] versions;
	
	public VersionBlock(UniversAALTab uAALTab, Composite parent, int style) {
		super(uAALTab, parent, style);
	}
	
	@Override
	public String getBlockName() {
		return "Middleware version";
	}
	
	@Override
	public void initBlock(Composite parent) {
		// parent is the Group of UIBlock, so setting it's layout here is appropriate
		parent.setLayout(new GridLayout());
		
		versionCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		versionCombo.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		versionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onVersionSelected(versionCombo.getSelectionIndex());
			}
		});
	}
	
	public BundleSet updateProjectList(BundleSet launchProjects) {
		BundleModel model = getUAALTab().getModel();
		String version = model.getCurrentVersion();
		Set<String> available = model.getAvailableVersion();
		
		if (available.contains(version)) {
			versions = new String[available.size()];
			versions = available.toArray(versions);
		} else {
			versions = new String[available.size() + 1];
			int i = 0;
			for (String v : available)
				versions[i++] = v;
			versions[i++] = version;
		}
		
		Arrays.sort(versions);
		versionCombo.setItems(versions);
		int index = 0;
		while (!versions[index].equals(version))
			index++;
		
		versionCombo.select(index);
		
		BundleSet bundles = model.getMiddlewareBundles();
		if (bundles == null)
			return launchProjects;
		
		BundleSet remainingProjects = new BundleSet(launchProjects);
		for (BundleEntry be : bundles)
			if (launchProjects.containsBundle(be))
				remainingProjects.remove(be);
		
		return remainingProjects;
	}
	
	private void onVersionSelected(int index) {
		String newVersion = null;
		if (versionCombo.getItemCount() == versions.length)
			newVersion = versions[index];
		else
			newVersion = versions[index - 1];
		
		// reset version to old value on fail
		if (newVersion == null || !tryChangeToVersion(newVersion))
			getUAALTab().getModel().updatePresenters();
		getUAALTab().getModel().updatePresenters();
	}
	
	public boolean tryChangeToVersion(String newVersion) {
		BundleModel model = getUAALTab().getModel();
		// find out which projects have to be checked for compatibility
		Set<ArtifactURL> toCheck = model.getIncompatibleProjects(newVersion);
		
		// now to check only contains incompatible projects
		if (toCheck.isEmpty()) { // everything ok, no incompatible projects
			model.changeToVersion(newVersion);
			return true;
		} else { // ask user what to do
			StringBuilder sb = new StringBuilder();
			sb.append("Following projects are dependent from some bundles of the old version: \n\n");
			for (ArtifactURL url : toCheck)
				sb.append("\t").append(url).append("\n");
			sb.append("\nWhat do do?");
			
			MessageDialog md = new MessageDialog(getShell(), "Compatibility issues", null, sb.toString(), MessageDialog.WARNING,
					new String[] { "Ignore", "Remove those projects", "Cancel" }, 0);
			
			int ret = md.open();
			if (ret == 0) {// ignore
				model.changeToVersion(newVersion);
				return true;
			} else if (ret == 1) { // remove incompatible
				for (ArtifactURL url : toCheck)
					getUAALTab().getModel().removeNoUpdate(getUAALTab().getModel().getBundles().find(url));
				
				// model will be updated here
				model.changeToVersion(newVersion);
				return true;
			}
			// else cancel -> do nothing
			return false;
		}
	}
}
