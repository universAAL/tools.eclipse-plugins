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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.universaal.uaalpax.model.BundleEntry;
import org.universaal.uaalpax.model.BundleSet;
import org.universaal.uaalpax.model.LaunchURL;
import org.universaal.uaalpax.ui.dialogs.AddEditUrlDialog;

public class AllBundlesBlock extends UIBlock implements ProjectTable.BundleDoubleClickListener {
	private ProjectTable table;
	
	public AllBundlesBlock(UniversAALTab uAALTab, Composite parent, int style) {
		super(uAALTab, parent, style);
	}
	
	@Override
	public String getBlockName() {
		return "Additional Libraries";
	}
	
	@Override
	public void initBlock(Composite parent) {
		// parent is the Group of UIBlock, so setting it's layout here is appropriate
		parent.setLayout(new GridLayout(2, false));
		
		// order left table, buttons, right table is intended, since buttons should be in the middle between both tables
		table = new ProjectTable(parent, SWT.NONE);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setChangeListener(this);
		table.setModel(getUAALTab().getModel());
		
		Composite buttonContainer = new Composite(parent, SWT.NONE);
		buttonContainer.setLayout(new GridLayout());
		buttonContainer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		
		final Button addURL = new Button(buttonContainer, SWT.PUSH);
		addURL.setText("Add URL");
		addURL.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button addBundle = new Button(buttonContainer, SWT.PUSH);
		addBundle.setText("Add Bundle");
		addBundle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button addPom = new Button(buttonContainer, SWT.PUSH);
		addPom.setText("Add POM");
		addPom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button addLib = new Button(buttonContainer, SWT.PUSH);
		addLib.setText("Add Library");
		addLib.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Button edit = new Button(buttonContainer, SWT.PUSH);
		edit.setText("Edit...");
		edit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Button remove = new Button(buttonContainer, SWT.PUSH);
		remove.setText("Delete");
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == remove)
					onRemove((IStructuredSelection) table.getSelection());
				else if (e.widget == addURL)
					onAddURL();
				else if (e.widget == edit)
					onEdit((IStructuredSelection) table.getSelection());
			}
		};
		
		addURL.addSelectionListener(listener);
		addBundle.addSelectionListener(listener);
		addPom.addSelectionListener(listener);
		addLib.addSelectionListener(listener);
		edit.addSelectionListener(listener);
		remove.addSelectionListener(listener);
		
		table.setBundleDoubleclickListener(this);
	}
	
	private void onRemove(IStructuredSelection selection) {
		if (selection == null || selection.isEmpty())
			return;
		
		for (Iterator<?> i = selection.iterator(); i.hasNext();) {
			Object be = i.next();
			if (!table.isBundleGrayed((BundleEntry) be))
				getUAALTab().getModel().remove((BundleEntry) be);
		}
		
		notifyChanged();
	}
	
	private void onAddURL() {
		AddEditUrlDialog d = new AddEditUrlDialog(getShell());
		int code = d.open();
		
		if (code == Window.OK) {
			BundleEntry be = new BundleEntry(d.getURL(), true, true, d.getLevel(), true);
			getUAALTab().addBundle(be);
			notifyChanged();
			
			StructuredSelection selection = new StructuredSelection(new Object[] { be });
			table.getViewer().setSelection(selection, true);
		}
	}
	
	private void editBundle(BundleEntry be) {
		if (table.isBundleGrayed(be))
			return;
		
		AddEditUrlDialog d = new AddEditUrlDialog(getShell(), be);
		int code = d.open();
		
		if (code == Window.OK) {
			int level = be.getLevel();
			LaunchURL url = be.getLaunchUrl();
			
			if (!url.equals(d.getURL())) {
				getUAALTab().removeBundle(be);
				BundleEntry newBe = new BundleEntry(d.getURL(), be.isSelected(), be.isStart(), d.getLevel(), be.isUpdate());
				getUAALTab().addBundle(newBe);
				be = newBe;
			} else { // url not changed
				if (level != d.getLevel()) {
					be.setLevel(d.getLevel());
					getUAALTab().getModel().getBundles().updateBundleOptions(be);
					getUAALTab().getModel().updatePresenters();
				}
			}
			
			StructuredSelection selection = new StructuredSelection(new Object[] { be });
			table.getViewer().setSelection(selection, true);
		}
	}
	
	private void onEdit(IStructuredSelection selection) {
		if (selection == null || selection.isEmpty())
			return;
		
		BundleEntry be = (BundleEntry) selection.getFirstElement();
		editBundle(be);
	}
	
	public BundleSet updateProjectList(BundleSet launchProjects) {
		// create list of all bundles and an array of bundles which should be gray out since they are already in an other
		// project list
		BundleSet allBundles = getUAALTab().getModel().getBundles();
		BundleEntry[] grayOut = new BundleEntry[allBundles.size() - launchProjects.size()];
		int i = 0;
		List<BundleEntry> projects = new ArrayList<BundleEntry>(allBundles.size());
		for (BundleEntry e : allBundles) {
			projects.add(e);
			if (!launchProjects.containsBundle(e))
				grayOut[i++] = e;
		}
		
		table.removeAll();
		table.addAll(projects);
		table.setGrayed(grayOut);
		table.getViewer().refresh(true);
		
		return new BundleSet(); // return empty map since all projects which were passed to here are additional libraries
	}
	
	public void onBundleClicked(BundleEntry be) {
		editBundle(be);
	}
}
