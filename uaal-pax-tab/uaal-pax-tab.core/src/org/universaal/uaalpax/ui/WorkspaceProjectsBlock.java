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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.universaal.uaalpax.model.BundleEntry;
import org.universaal.uaalpax.model.BundlePresenter;
import org.universaal.uaalpax.model.BundleSet;
import org.universaal.uaalpax.model.LaunchURL;
import org.universaal.uaalpax.model.UnknownBundleFormatException;

public class WorkspaceProjectsBlock extends Composite implements LaunchChangeListener, BundlePresenter {
	private final UniversAALTab uAALTab;
	
	private Button toRight, toLeft, allToRight, allToLeft;
	private ProjectTable leftTable, rightTable;
	
	public WorkspaceProjectsBlock(UniversAALTab uAALTab, Composite parent, int style) {
		super(parent, style);
		this.uAALTab = uAALTab;
		
		setLayout(new GridLayout());
		setLayoutData(GridData.FILL_BOTH);
		initBlock(this);
	}
	
	public UniversAALTab getUAALTab() {
		return uAALTab;
	}
	
	// public abstract void initBlock(Composite parent);
	
	public void notifyChanged() {
		uAALTab.updateLaunchConfigurationDialog();
	}
	
	public void initBlock(Composite parent) {
		// parent is the Group of UIBlock, so setting it's layout here is appropriate
		// parent.setLayout(new GridLayout(3, false));
		
		final Group leftGroup = new Group(parent, SWT.NONE);
		leftGroup.setLayout(new GridLayout());
		leftGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		leftGroup.setText("Available workspace projects");
		
		// order left table, buttons, right table is intended, since buttons should be in the middle between both tables
		leftTable = new ProjectTable(leftGroup, SWT.NONE);
		leftTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		leftTable.setChangeListener(this);
		leftTable.setModel(getUAALTab().getModel());
		
		final Composite buttonContainer = new Composite(parent, SWT.NONE);
		buttonContainer.setLayout(new GridLayout());
		
		toLeft = new Button(buttonContainer, SWT.PUSH);
		toLeft.setText("<");
		toLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		allToLeft = new Button(buttonContainer, SWT.PUSH);
		allToLeft.setText("<<");
		allToLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toRight = new Button(buttonContainer, SWT.PUSH);
		toRight.setText(">");
		toRight.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		allToRight = new Button(buttonContainer, SWT.PUSH);
		allToRight.setText(">>");
		allToRight.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Group rightGroup = new Group(parent, SWT.NONE);
		rightGroup.setLayout(new GridLayout());
		rightGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		rightGroup.setText("Selected projects");
		
		rightTable = new ProjectTable(rightGroup, SWT.NONE);
		rightTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		rightTable.setChangeListener(this);
		rightTable.setModel(getUAALTab().getModel());
		
		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == toLeft) {
					moveItemsToLeft((IStructuredSelection) rightTable.getSelection());
				} else if (e.widget == toRight) {
					moveItemsToRight((IStructuredSelection) leftTable.getSelection());
				} else if (e.widget == allToLeft) {
					moveAllToLeft();
				} else if (e.widget == allToRight) {
					moveAllToRight();
				}
			}
		};
		
		toLeft.addSelectionListener(listener);
		toRight.addSelectionListener(listener);
		allToLeft.addSelectionListener(listener);
		allToRight.addSelectionListener(listener);
		
		parent.setLayout(new Layout() {
			@Override
			protected void layout(Composite composite, boolean flushCache) {
				Rectangle rect = composite.getClientArea();
				Point buttonsSize = buttonContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
				int w1 = (rect.width - buttonsSize.x) / 2;
				
				leftGroup.setBounds(rect.x, rect.y, w1, rect.height);
				buttonContainer.setBounds(rect.x + w1, rect.y + (rect.height - buttonsSize.y) / 2, buttonsSize.x, rect.height);
				rightGroup.setBounds(rect.x + w1 + buttonsSize.x, rect.y, rect.width - buttonsSize.x - w1, rect.height);
			}
			
			@Override
			protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
				return new Point(wHint, hHint);
			}
		});
	}
	
	private void moveItemsToLeft(IStructuredSelection sel) {
		if (sel == null || sel.isEmpty())
			return;
		
		Set<BundleEntry> bundles = new HashSet<BundleEntry>();
		for (Iterator<?> i = sel.iterator(); i.hasNext();) {
			Object pu = i.next();
			System.out.println("moving " + pu + "from right to left");
			bundles.add((BundleEntry) pu);
		}
		
		getUAALTab().removeAllBundles(bundles);
	}
	
	private void moveItemsToRight(IStructuredSelection sel) {
		if (sel == null || sel.isEmpty())
			return;
		
		Set<BundleEntry> bundles = new HashSet<BundleEntry>();
		for (Iterator<?> i = sel.iterator(); i.hasNext();) {
			Object pu = i.next();
			bundles.add((BundleEntry) pu);
		}
		
		getUAALTab().addAllBundles(bundles);
	}
	
	public void moveAllToLeft() {
		Set<BundleEntry> pus = new HashSet<BundleEntry>(rightTable.getElements());
		getUAALTab().getModel().removeAll(pus);
		
		notifyChanged();
	}
	
	public void moveAllToRight() {
		if (new MessageDialog(this.getShell(), "Really add all projects", null,
				"Do you really want to add all projects in workspace to the run configuration?\n"
						+ "To add selected projects only, press the > button", MessageDialog.QUESTION, new String[] { "Yes", "No" }, 0)
				.open() != 0)
			return;
		
		List<BundleEntry> pus = new ArrayList<BundleEntry>(leftTable.getElements());
		getUAALTab().addAllBundles(pus);
		
		notifyChanged();
	}
	
	public BundleSet updateProjectList(BundleSet launchProjects) {
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		IProject[] projects = myWorkspaceRoot.getProjects();
		
		Set<BundleEntry> leftSet = new HashSet<BundleEntry>(projects.length), rightSet = new HashSet<BundleEntry>(projects.length);
		
		for (IProject p : projects) {
			// leftProjects.add(new ProjectURL(p.getName(), 5, true));
			
			IResource pom = p.findMember("pom.xml");
			if (pom != null && pom.exists() && pom.getType() == IResource.FILE) {
				IFile pomFile = (IFile) pom;
				
				try {
					Model model = null;
					MavenXpp3Reader mavenreader = new MavenXpp3Reader();
					model = mavenreader.read(pomFile.getContents());
					
					model.setPomFile(pomFile.getFullPath().toFile());
					
					MavenProject project = new MavenProject(model);
					
					LaunchURL launchUrl = new LaunchURL("mvn:" + project.getGroupId() + "/" + project.getArtifactId() + "/"
							+ project.getVersion());
					
					BundleEntry pu = new BundleEntry(launchUrl, p.getName(), 12, true);
					leftSet.add(pu);
				} catch (CoreException e) {
					System.out.println("Failed to parse " + p.getName() + ": " + e);
				} catch (IOException e) {
					System.out.println("Failed to parse " + p.getName() + ": " + e);
				} catch (XmlPullParserException e) {
					System.out.println("Failed to parse " + p.getName() + ": " + e);
				}
			}
		}
		
		BundleSet remainingProjects = new BundleSet(launchProjects);
		
		// now all workspace projects are in leftTable
		// put items to right table if they are contained in launch config
		
		for (BundleEntry e : launchProjects) {
			try {
				String launchURL = e.getArtifactUrl().url;
				
				// check if this launch url corresponds to a project in workspace
				for (Iterator<BundleEntry> iter = leftSet.iterator(); iter.hasNext();) {
					BundleEntry pu = iter.next();
					
					// startsWith ensures that the test passes if the version is not entered in launchUrl
					
					try {
						if (pu.getArtifactUrl().url.startsWith(launchURL)) {
							iter.remove();
							rightSet.add(new BundleEntry(pu.getProjectName(), e.getLaunchUrl(), e.getOptions()));
							remainingProjects.remove(e);
						}
					} catch (UnknownBundleFormatException e1) {
						// should never happen since bundles from left list should always be maven bundles
					}
					
				}
			} catch (UnknownBundleFormatException e1) {
				// ignore launch bundle if not artifact
			}
		}
		
		leftTable.removeAll();
		leftTable.addAll(leftSet);
		
		rightTable.removeAll();
		rightTable.addAll(rightSet);
		
		return remainingProjects;
	}
}
