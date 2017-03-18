/*	
	Copyright 2007-2016 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institute for Computer Graphics Research
	
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
package org.universaal.tools.envsetup.ui;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.universaal.tools.envsetup.core.EclipseAdapter;
import org.universaal.tools.envsetup.core.RepoMgmt;
import org.universaal.tools.envsetup.core.RepoMgmt.Repo;

/**
 * 
 * @author Carsten Stockloew
 *
 */
public class SetupPage extends WizardPage {

	public final static String title = "Development Environment Setup";

	public final static String msg = "Select which parts of the development environment should be configured.";

	Button btnImport;
	Button btnAdMaven;
	Button btnAdEclipse;
	Button btnBrowse;
	Button btnBrowseJDK;
	Button btnSelAll;
	Button btnSelNone;
	Button btnSelRecom;
	Button btnUseAgg;
	Combo cbBranch;
	Text txtDir;
	Text txtJDK;
	Table tblRepos;
	Shell shell;

	// name of repo or repo group -> checkbox
	Map<String, Button> repoItems = new HashMap<String, Button>();

	public SetupPage() {
		super(title);
		setMessage(msg);
	}

	@Override
	public void createControl(Composite parent) {
		shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		initializeDialogUnits(parent);
		Label label;

		Composite com = new Composite(parent, SWT.NULL);
		GridLayout layCom = new GridLayout();
		layCom.numColumns = 2;
		com.setLayout(layCom);
		com.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		// adapt maven settings.xml
		btnAdMaven = new Button(com, SWT.CHECK);
		btnAdMaven.setSelection(true);
		new Label(com, SWT.NONE).setText("Adapt Maven settings to include universAAL repositories");

//		// adapt eclipse
//		btnAdEclipse = new Button(com, SWT.CHECK);
//		btnAdEclipse.setSelection(true);
//		new Label(com, SWT.NONE).setText("Adapt eclipse.ini");
//
//		new Label(com, SWT.NONE).setText(""); // just a dummy to skip a cell
//		// jdk composite
//		Composite comAdEcl = new Composite(com, SWT.NULL);
//		GridLayout layAdEcl = new GridLayout();
//		layAdEcl.numColumns = 3;
//		comAdEcl.setLayout(layAdEcl);
//		comAdEcl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		// jdk select
//		label = new Label(comAdEcl, SWT.NONE);
//		label.setText("JDK:");
//		// label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
//		txtJDK = new Text(comAdEcl, SWT.BORDER | SWT.SINGLE);
//		txtJDK.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		txtJDK.setText(EclipseAdapter.getJDK());
//		txtJDK.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				if (txtJDK.getText().length() > 0) {
//					if (!EclipseAdapter.isValidJDKDir(txtJDK.getText())) {
//						setErrorMessage(
//								"Please select a valid JDK directory, e.g. 'C:\\Program Files\\Java\\jdk1.8.0_112', or leave this field empty.");
//					} else {
//						setMessage(msg);
//						setErrorMessage(null);
//					}
//				}
//			}
//		});
//		btnBrowseJDK = new Button(comAdEcl, SWT.PUSH);
//		btnBrowseJDK.setText("Browse..");
//		btnBrowseJDK.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				handleBrowse(txtJDK);
//			}
//		});

		// import
		btnImport = new Button(com, SWT.CHECK);
		btnImport.setSelection(true);
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnImport.getSelection()) {
					setImportEnabled(true);
				} else {
					setImportEnabled(false);
				}
			}
		});
		new Label(com, SWT.NONE).setText("Download and import universAAL platform projects");
		new Label(com, SWT.NONE).setText(""); // just a dummy to skip a cell
		// import composite
		Composite comImport = new Composite(com, SWT.NULL);
		GridLayout layImport = new GridLayout();
		layImport.numColumns = 3;
		comImport.setLayout(layImport);
		comImport.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		// import branch
		label = new Label(comImport, SWT.NONE);
		label.setText("Branch:");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		cbBranch = new Combo(comImport, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (String branch : RepoMgmt.branches)
			cbBranch.add(branch);
		cbBranch.select(1);
		span(cbBranch, 2);
		// import dir
		label = new Label(comImport, SWT.NONE);
		label.setText("Directory:");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		txtDir = new Text(comImport, SWT.BORDER | SWT.SINGLE);
		txtDir.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		// try {
		// txtDir.setText(new File("./").getCanonicalPath().toString());
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		// get object which represents the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// get location of workspace (java.io.File)
		txtDir.setText(workspace.getRoot().getLocation().toString());

		btnBrowse = new Button(comImport, SWT.PUSH);
		btnBrowse.setText("Browse..");
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("widgetSelected");
				handleBrowse(txtDir);
			}
		});
		// import repos
		label = new Label(comImport, SWT.NONE);
		label.setText("Repositories:");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		tblRepos = new Table(comImport, // SWT.CHECK |
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tblRepos.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		for (String group : RepoMgmt.groups.keySet()) {
			add(group, true);
			List<Repo> repos = RepoMgmt.groups.get(group);
			for (Repo r : repos) {
				add(r.name, false);
			}
		}
		selectRecom();
		// import repos select buttons
		Composite comSel = new Composite(comImport, SWT.NULL);
		GridLayout laySel = new GridLayout();
		laySel.numColumns = 1;
		comSel.setLayout(laySel);
		comSel.setLayoutData(new GridData(GridData.FILL, SWT.TOP, true, false));
		btnSelAll = new Button(comSel, SWT.PUSH);
		btnSelAll.setText("Select All");
		btnSelAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectAll();
			}
		});
		btnSelNone = new Button(comSel, SWT.PUSH);
		btnSelNone.setText("Deselect All");
		btnSelNone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectNone();
			}
		});
		btnSelRecom = new Button(comSel, SWT.PUSH);
		btnSelRecom.setText("Select Recommended");
		btnSelRecom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectRecom();
			}
		});
		// import repos note
		new Label(comImport, SWT.NONE).setText("");
		label = new Label(comImport, SWT.WRAP);
		// label.setText("Note: none of the repositories is needed for
		// application development"
		// /*
		// * + System.getProperty("line.separator")
		// */ + " (project 'samples' is recommended).");
		label.setText("Note: for application development only one of the distribution repositories is required"
				/*
				 * + System.getProperty("line.separator")
				 */ + " (project 'samples' is recommended).");
		span(label, 2);

		new Label(comImport, SWT.NONE).setText("");
		Composite comTemp = new Composite(comImport, SWT.NULL);
		GridLayout layTemp = new GridLayout();
		layTemp.numColumns = 3;
		comTemp.setLayout(layTemp);
		comTemp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		btnUseAgg = new Button(comTemp, SWT.CHECK);
		btnUseAgg.setSelection(true);
		new Label(comTemp, SWT.NONE).setText("Use platform aggregator directory structure.");

		setControl(com);
	}

	private void add(String name, boolean title) {
		TableItem t = new TableItem(tblRepos, SWT.NONE);
		TableEditor editor = new TableEditor(tblRepos);
		Button button = new Button(tblRepos, title ? SWT.NONE : SWT.CHECK);
		button.setText(name);
		button.pack();
//		if (name.equals(RepoMgmt.samples))
//			button.setSelection(true);
		editor.minimumWidth = button.getSize().x;
		editor.horizontalAlignment = SWT.LEFT;
		editor.setEditor(button, t, 0);
		repoItems.put(name, button);
	}

	private void selectAll() {
		for (Button item : repoItems.values()) {
			item.setSelection(true);
		}
	}

	private void selectNone() {
		for (Button item : repoItems.values()) {
			item.setSelection(false);
		}
	}

	private void selectRecom() {
		for (String name : repoItems.keySet()) {
			//if (RepoMgmt.samples.equals(name))
			if (RepoMgmt.isRecom(name))
				repoItems.get(name).setSelection(true);
		}
	}

	private void setImportEnabled(boolean enabled) {
		btnBrowse.setEnabled(enabled);
		cbBranch.setEnabled(enabled);
		txtDir.setEnabled(enabled);
		tblRepos.setEnabled(enabled);
		btnSelAll.setEnabled(enabled);
		btnSelNone.setEnabled(enabled);
		btnSelRecom.setEnabled(enabled);
	}

	void span(Control ctrl, int cols) {
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_END);
		gridData.horizontalSpan = cols;
		gridData.horizontalAlignment = GridData.FILL;
		ctrl.setLayoutData(gridData);
	}

	private void handleBrowse(Text txt) {
		DirectoryDialog dirDialog = new DirectoryDialog(shell);
		dirDialog.setFilterPath(txt.getText());
		dirDialog.setMessage("Select a folder to download the repositories, ususally the workspace folder."
				+ " If a subfolder for a repository already exists, then the repository will be skipped.");
		String dir = dirDialog.open();
		System.out.println(dir);
		if (dir != null) {
			txt.setText(dir);
		}
	}

	@Override
	public void performHelp() {
		// open github page in default browser
		try {
			// TODO: change to the right URL
			URI uri = new URL("http://github.com/universAAL/tools/wiki").toURI();
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(uri);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}
}
