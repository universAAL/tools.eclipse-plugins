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

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.universaal.uaalpax.model.BundleEntry;
import org.universaal.uaalpax.model.BundleModel;
import org.universaal.uaalpax.model.BundleSet;


public class FeaturesBlock extends UIBlock {
	private Composite containerComposite;
	private SelectionListener featuresListener;
	
	public FeaturesBlock(UniversAALTab uAALTab, Composite parent, int style) {
		super(uAALTab, parent, style);
	}
	
	public BundleSet updateProjectList(BundleSet launchProjects) {
		BundleModel model = getUAALTab().getModel();
		String currentVersion = model.getCurrentVersion();
		Set<String> features = getUAALTab().getVersionProvider().getAdditionalFeatures(currentVersion);
		BundleSet modelBundles = model.getBundles();
		
		Control[] toDispose = containerComposite.getChildren();
		
		BundleSet remaining = new BundleSet(launchProjects);
		
		for (String f : features) {
			Button btn = new Button(containerComposite, SWT.CHECK);
			btn.setText(f);
			
			StringBuilder sb = new StringBuilder();
			BundleSet required = getUAALTab().getVersionProvider().getBundlesOfFeature(currentVersion, f);
			if (required != null) {
				boolean notContained = false;
				for (BundleEntry be : required) {
					sb.append(be.getLaunchUrl().url).append("\n");
					
					if (!modelBundles.containsBundle(be)) {
						notContained = true;
					}
				}
				
				btn.setSelection(!notContained);
				if(!notContained)
					remaining.removeAll(required.allBundles());
			}
			
			btn.setToolTipText(sb.toString());
			btn.addSelectionListener(featuresListener);
		}
		
		for (Control ctrl : toDispose)
			ctrl.dispose();
		
		
		return remaining;
	}
	
	@Override
	public String getBlockName() {
		return "Features";
	}
	
	@Override
	public void initBlock(Composite parent) {
		// parent is the Group of UIBlock, so setting it's layout here is appropriate
		parent.setLayout(new RowLayout());
		containerComposite = parent;
		
		featuresListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.widget;
				onFeatureSelected(btn.getText(), btn.getSelection());
			}
		};
	}
	
	private void onFeatureSelected(String feature, boolean selected) {
		BundleSet required = getUAALTab().getVersionProvider().getBundlesOfFeature(getUAALTab().getModel().getCurrentVersion(), feature);
		if (required != null) {
			if (selected)
				getUAALTab().addAllBundles(required.allBundles());
			else
				getUAALTab().removeAllBundles(required.allBundles());
		}
	}
}
