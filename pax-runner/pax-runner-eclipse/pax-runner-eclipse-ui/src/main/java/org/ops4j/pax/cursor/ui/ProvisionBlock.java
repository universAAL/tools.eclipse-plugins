/*
 * Copyright 2007 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.cursor.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.ops4j.pax.cursor.shared.Attribute;

/**
 * Provisioning Composite of Pax Runner Eclipse Plugin (bottom group).
 * 
 * @author Alin Dreghiciu
 * @since 0.2.0, December 16, 2007
 */
public class ProvisionBlock extends CursorTabBlock {

	private final CheckboxTreeViewer m_treeViewer;
	private final Button m_editButton;
	private final Button m_deleteButton;
	private List expandedNodes = new ArrayList();
	private File m_lastUsedDir;
	// list used for drag&drop
	private List<ProvisionURL> lst;

	/**
	 * @see Composite#Composite(Composite, int)
	 */
	public ProvisionBlock(Composite parent, int style) {
		super(parent, style);
		final GridLayout gridLayout = new GridLayout();
		setLayout(gridLayout);

		final Group provisioningGroup = new Group(this, SWT.NONE);

		provisioningGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		provisioningGroup.setText("Provisioning:");
		final GridLayout provisioningGridLayout = new GridLayout();
		provisioningGridLayout.marginWidth = 0;
		provisioningGridLayout.marginHeight = 0;
		provisioningGridLayout.numColumns = 2;
		provisioningGroup.setLayout(provisioningGridLayout);

		final Composite treeComposite = new Composite(provisioningGroup,
				SWT.NONE);

		final GridData treeGridData = new GridData(SWT.FILL, SWT.FILL, true,
				true);

		treeComposite.setLayoutData(treeGridData);

		final GridLayout tableGridLayout = new GridLayout();
		tableGridLayout.marginWidth = 0;
		tableGridLayout.marginHeight = 0;
		treeComposite.setLayout(tableGridLayout);

		m_treeViewer = new CheckboxTreeViewer(treeComposite, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);
		// m_treeViewer.getTree().getVerticalBar().setEnabled(true);
		m_treeViewer.setContentProvider(new ProvisionContentProvider());
		m_treeViewer.setLabelProvider(new ProvisionLabelProvider());
		m_treeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(final CheckStateChangedEvent event) {
				// if(event.getChecked()){
				// m_treeViewer.setSubtreeChecked(event.getElement(), true);
				// }
				getExpandedNodes();
				ProvisionURL provisionURL = (ProvisionURL) event.getElement();
				provisionURL.setSelected(event.getChecked());
				ProvisionURL parent = provisionURL.getParent();
				if (parent != null) {
					ProvisionURL[] children = parent.getChildren();
					if (children != null) {
						boolean allChecked = true;
						boolean allUnchecked = true;
						for (int i = 0; i < children.length; i++) {
							if (children[i] != provisionURL
									&& children[i].isSelected()) {
								allUnchecked = false;
							} else if (children[i] != provisionURL) {
								allChecked = false;
							}
						}
						if (provisionURL.isSelected()) {
							allUnchecked = false;
						} else {
							allChecked = false;
						}
						if (allChecked) {
							parent.setSelected(true);
							m_treeViewer.setChecked(((ProvisionURL) event
									.getElement()).getParent(), true);
						}
						if (allUnchecked) {
							parent.setSelected(false);
							m_treeViewer.setChecked(((ProvisionURL) event
									.getElement()).getParent(), false);
						}
					}
				} else {
					provisionURL.setSelected(event.getChecked());
					if (provisionURL.getChildren() != null) {
						for (int i = 0; i < provisionURL.getChildren().length; i++) {
							provisionURL.getChildren()[i].setSelected(event
									.getChecked());

							// m_treeViewer.setChecked(provisionURL.getChildren()[i],
							// provisionURL.isSelected());
						}
						m_treeViewer.setSubtreeChecked(event.getElement(),
								event.getChecked());
					}
				}
				// updateSelectedItems();
				updateStartImages();
				updateExpandedNodes();
				notifyUpdate();
			}
		});
		m_treeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						onTableSelectionChanged(event);
					}
				});
		m_treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (!m_treeViewer.getSelection().isEmpty()) {
					TreeSelection treeSel = (TreeSelection) m_treeViewer
							.getSelection();
					if (((ProvisionURL) treeSel.getPaths()[treeSel.getPaths().length - 1]
							.getLastSegment()).getParent() == null) {
						return;
					} else {
						onEditButtonSelected();
						updateStartImages();
					}
				}
			}
		});

		m_treeViewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				int cat1 = category(e1);
				int cat2 = category(e2);
				if (cat1 != cat2)
					return cat1 - cat2;
				String name1 = "", name2 = "";
				if (viewer == null || !(viewer instanceof ContentViewer)) {
					name1 = e1.toString();
					name2 = e2.toString();
				} else {
					IBaseLabelProvider prov = ((ContentViewer) viewer)
							.getLabelProvider();
					if (prov instanceof ILabelProvider) {
						ILabelProvider lprov = (ILabelProvider) prov;

						name1 = lprov.getText(e1);
						name2 = lprov.getText(e2);
						if (name1.startsWith("Level")
								&& name2.startsWith("Level")) {
							name1 = lprov.getText(e1).split(" ")[1];
							name2 = lprov.getText(e2).split(" ")[1];
						}
					} else {
						name1 = e1.toString();
						name2 = e2.toString();
					}
				}

				if (name1 == null)
					name1 = "";
				if (name2 == null)
					name2 = "";
				try {
					int a = Integer.parseInt(name1);
					int b = Integer.parseInt(name2);
					if (a < b)
						return (int) -1;
					if (a == b)
						return (int) 0;
					else
						return (int) 1;
				} catch (Exception ex) {
					return collator.compare(name1, name2);
				}
			}
		});

		final Tree tree = m_treeViewer.getTree();

		// add drag and drop functionality

		DragSource ds = new DragSource(tree, DND.DROP_MOVE);
		ds.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		ds.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
				lst = new ArrayList<ProvisionURL>();
				TreeItem[] sel = tree.getSelection();

				// System.out.println("");
				add(lst, sel);
				// event.data = lst;
				// System.out.println(" - dragging " + lst.size() +
				// " elements");
				// just a fake, without the line below, the drop-method is
				// never called (data needs to be a String, why?)
				event.data = "fakeString";
			}

			private void add(List<ProvisionURL> lst, TreeItem[] sel) {
				for (TreeItem item : sel) {
					if (item.getText().startsWith("Level ")) {
						add(lst, item.getItems());
					} else {
						lst.add((ProvisionURL) item.getData());
						// System.out.println(" -- add: " + ((ProvisionURL)
						// item.getData()).getUrl());
					}
				}
			}
		});

		DropTarget dt = new DropTarget(tree, DND.DROP_MOVE);

		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				//System.out.println(" -- drop:_" + event.item);
				if (event.item != null) {
					// &&
					// !tree.getSelection()[0].getText().startsWith("Level ")) {

					// List<ProvisionURL> lst = (List<ProvisionURL>) event.data;
					TreeItem item = (TreeItem) event.item;
					TreeItem parent = item.getParentItem();
					if (parent == null)
						parent = item;
					String newLevel = parent.getText().split(" ")[1];
					//System.out.println(" - drop to level " + newLevel);
					for (ProvisionURL prov : lst) {
						//System.out.println(" --- processing " + prov.getUrl());
						boolean isSelected = prov.isSelected();
						findAndDeleteProvisionUrl(prov);
						addURL(prov.getUrl(), newLevel, prov.isStart(),
								prov.isUpdate(), isSelected);
					}
				}
			}
		});

		tree.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				for (TreeItem item : tree.getSelection()) {
					if (item.getImage() != null) {
						if ((e.x > item.getImageBounds(0).x)
								&& (e.x < (item.getImageBounds(0).x + item
										.getImage().getBounds().width / 2))) {
							if ((e.y > item.getImageBounds(0).y)
									&& (e.y < (item.getImageBounds(0).y + item
											.getImage().getBounds().height))) {
								if (item.getParentItem() != null) {
									if (((ProvisionURL) item.getData())
											.isStart()) {
										((ProvisionURL) item.getData())
												.setStart(false);
									} else {
										((ProvisionURL) item.getData())
												.setStart(true);
									}
									updateStartImages();
								} else {
									if (((ProvisionURL) item.getData())
											.isStart()) {
										((ProvisionURL) item.getData())
												.setStart(false);
									} else {
										((ProvisionURL) item.getData())
												.setStart(true);
									}
									if (((ProvisionURL) item.getData())
											.getChildren() != null) {
										for (int i = 0; i < ((ProvisionURL) item
												.getData()).getChildren().length; i++) {
											ProvisionURL provisionURL = ((ProvisionURL) item
													.getData()).getChildren()[i];
											provisionURL
													.setStart(((ProvisionURL) item
															.getData())
															.isStart());
										}
										updateStartImages();
									}
								}
								notifyUpdate();
							}
						} else if ((e.x > item.getImageBounds(0).x
								+ item.getImage().getBounds().width / 2)
								&& (e.x < (item.getImageBounds(0).x + item
										.getImage().getBounds().width))) {
							if ((e.y > item.getImageBounds(0).y)
									&& (e.y < (item.getImageBounds(0).y + item
											.getImage().getBounds().height))) {
								if (item.getParentItem() != null) {
									if (((ProvisionURL) item.getData())
											.isUpdate()) {
										((ProvisionURL) item.getData())
												.setUpdate(false);
									} else {
										((ProvisionURL) item.getData())
												.setUpdate(true);
									}
									updateStartImages();
								} else {
									if (((ProvisionURL) item.getData())
											.isUpdate()) {
										((ProvisionURL) item.getData())
												.setUpdate(false);
									} else {
										((ProvisionURL) item.getData())
												.setUpdate(true);
									}
									if (((ProvisionURL) item.getData())
											.getChildren() != null) {
										for (int i = 0; i < ((ProvisionURL) item
												.getData()).getChildren().length; i++) {
											ProvisionURL provisionURL = ((ProvisionURL) item
													.getData()).getChildren()[i];
											provisionURL
													.setUpdate(((ProvisionURL) item
															.getData())
															.isUpdate());
										}
										updateStartImages();
									}
								}
								notifyUpdate();
							}
						}
					}
				}
			}

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
			}
		});

		// D N D

		// DragSource ds = new DragSource(tree, DND.DROP_MOVE);
		// ds.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		// ds.addDragListener(new DragSourceAdapter() {
		// public void dragSetData(DragSourceEvent event) {
		// // Set the data to be the first selected item's text
		// event.data = ((TreeItem)tree.getSelection()[0]);
		// // System.out.println(event.data);
		// }
		// });
		//
		//
		// // Create the drop target on the button
		// DropTarget dt = new DropTarget(tree, DND.DROP_MOVE);
		// dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		// dt.addDropListener(new DropTargetAdapter() {
		// public void drop(DropTargetEvent event) {
		// // Set the buttons text to be the text being dropped
		// System.out.println();
		// }
		// });

		tree.addListener(SWT.Expand, new Listener() {
			public void handleEvent(Event event) {
				updateStartImages();
			}
		});
		// table.setLinesVisible(true);

		// table.setHeaderVisible(true);

		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// final TableColumn urlTableColumn = new TableColumn( table, SWT.NONE
		// );
		//
		// urlTableColumn.setWidth( 425 );
		// urlTableColumn.setText( "Provision from" );
		//
		// final TableColumn startTableColumn = new TableColumn( table,
		// SWT.CENTER );
		// startTableColumn.setWidth( 50 );
		// startTableColumn.setText( "Start" );
		//
		// final TableColumn levelTableColumn = new TableColumn( table,
		// SWT.CENTER );
		// levelTableColumn.setWidth( 70 );
		// levelTableColumn.setText( "Level" );
		//
		// final TableColumn updateTableColumn = new TableColumn( table,
		// SWT.CENTER );
		// updateTableColumn.setWidth( 50 );
		// updateTableColumn.setText( "Update" );

		// final ProvisionCellModifier cellModifier = new
		// ProvisionCellModifier();
		// cellModifier
		// .setModifyListener(new ProvisionCellModifier.ModifyListener() {
		// public void modified(Object element) {
		// m_treeViewer.update(element, null);
		// notifyUpdate();
		// }
		// });
		// m_treeViewer.setCellModifier(cellModifier);

		// m_treeViewer.setCellEditors(new CellEditor[] { null,
		// new CheckboxCellEditor(table), new TextCellEditor(table),
		// new CheckboxCellEditor(table) });
		// m_treeViewer.setColumnProperties(new String[] { "url", "start",
		// "level", "update" });

		final Composite buttonsComposite = new Composite(provisioningGroup,
				SWT.NONE);
		final GridData butonsGridData = new GridData(SWT.LEFT, SWT.TOP, false,
				false);
		buttonsComposite.setLayoutData(butonsGridData);
		buttonsComposite.setLayout(new GridLayout());

		final Button addLevel = new Button(buttonsComposite, SWT.NONE);
		addLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addLevel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddLevelButtonSelected();
			}
		});
		addLevel.setText("Add Level");

		final Button addButton = new Button(buttonsComposite, SWT.NONE);
		addButton
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddButtonSelected();
			}
		});
		addButton.setText("Add URL");

		final Button addBundleButton = new Button(buttonsComposite, SWT.NONE);
		addBundleButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		addBundleButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddSingleFileButtonSelected("scan-bundle", new String[] {
						"*.jar", "*.*" },
						new String[] { "Any Jar", "Any File" });
			}
		});
		addBundleButton.setText("Add Bundle...");

		final Button addFileButton = new Button(buttonsComposite, SWT.NONE);
		addFileButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		addFileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddSingleFileButtonSelected("scan-file", new String[] {
						"*.bundles", "*.txt", "*.*" }, new String[] {
						"Provision File", "TXT Provision File", "Any File" });
			}
		});
		addFileButton.setText("Add File...");

		final Button addPomButton = new Button(buttonsComposite, SWT.NONE);
		addPomButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		addPomButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddSingleFileButtonSelected("scan-pom", new String[] {
						"pom.xml", "*.pom", "*.*" }, new String[] {
						"Maven POM", "Maven Repository POM", "Any File" });
			}
		});
		addPomButton.setText("Add POM...");

		final Button addDirButton = new Button(buttonsComposite, SWT.NONE);
		addDirButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		addDirButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddDirButtonSelected();
			}
		});
		addDirButton.setText("Add Dir...");

		final Button addMavenButton = new Button(buttonsComposite, SWT.NONE);
		addMavenButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		addMavenButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAddMavenButtonSelected();
			}
		});
		addMavenButton.setText("Add Maven...");

		m_editButton = new Button(buttonsComposite, SWT.NONE);
		m_editButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		m_editButton.setEnabled(false);
		m_editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onEditButtonSelected();
			}
		});
		m_editButton.setText("Edit...");

		m_deleteButton = new Button(buttonsComposite, SWT.NONE);
		m_deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		m_deleteButton.setEnabled(false);
		m_deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onRemoveButtonSelected();

			}
		});
		m_deleteButton.setText("Delete");
		m_treeViewer.expandAll();
	}

	private void getExpandedNodes() {
		expandedNodes = new ArrayList();
		final TreeItem[] items = m_treeViewer.getTree().getItems();
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].getExpanded()) {
					expandedNodes.add(items[i].getText());
				}
			}
		}
	}

	private void updateExpandedNodes() {
		final TreeItem[] items = m_treeViewer.getTree().getItems();
		// expand nodes

		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				if (expandedNodes.contains(items[i].getText())) {
					items[i].setExpanded(true);
				} else {
					items[i].setExpanded(false);
				}
			}
		}
	}

	private void updateSelectedItems() {
		List provisionURLs = new ArrayList();
		List selectedURLs = new ArrayList();
		List expandedNodes = new ArrayList();

		final TreeItem[] items = m_treeViewer.getTree().getItems();
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].getExpanded()) {
					expandedNodes.add(items[i]);
				}
				final ProvisionURL provisionURL1 = (ProvisionURL) items[i]
						.getData();
				if (provisionURL1.isSelected()) {
					selectedURLs.add(provisionURL1);
				}
				provisionURLs.add(provisionURL1);
				if (provisionURL1.getUrl().startsWith("Level ")) {
					if (provisionURL1.getChildren() != null) {
						ProvisionURL[] tmp = new ProvisionURL[provisionURL1
								.getChildren().length + 1];
						for (int j = 0; j < provisionURL1.getChildren().length; j++) {
							tmp[j] = provisionURL1.getChildren()[j];
							if (tmp[j].isSelected()) {
								selectedURLs.add(tmp[j]);
							}
						}
					} else {
					}
				} else {
					if (provisionURL1.getChildren() != null) {
						for (int j = 0; j < provisionURL1.getChildren().length; j++) {
							if (provisionURL1.getChildren()[j].isSelected()) {
								selectedURLs
										.add(provisionURL1.getChildren()[j]);
							}
						}
					}
				}
			}
		}
		m_treeViewer.setInput(provisionURLs);
		// m_treeViewer.update(provisionURL, null);
		// m_treeViewer.expandAll();
		updateStartImages();
		// m_treeViewer.add( provisionURL );
		m_treeViewer.setCheckedElements(selectedURLs.toArray());
		notifyUpdate();
	}

	/**
	 * Handles add level button push.
	 */

	protected void onAddLevelButtonSelected() {
		InputDialog levelDialog = new InputDialog(getShell(),
				"Add level into OSGi framework", "Level number (e.g. 10):",
				null, null);
		if (levelDialog.open() != Window.OK) {
			return;
		}

		String levelValue = levelDialog.getValue().trim();
		try {
			// check for integer value
			if (Integer.parseInt(levelValue) >= 0) {

				// check for existing level
				if (m_treeViewer.getTree().getItems() != null
						&& m_treeViewer.getTree().getItems().length > 0) {
					boolean foundLevel = false;
					for (int i = 0; i < m_treeViewer.getTree().getItems().length; i++) {
						final ProvisionURL provisionURL1 = (ProvisionURL) m_treeViewer
								.getTree().getItems()[i].getData();
						if (provisionURL1.getUrl().startsWith("Level")
								&& provisionURL1.getUrl().split(" ")[1]
										.equals(levelValue)) {
							foundLevel = true;
							break;
						}
					}
					if (!foundLevel) {
						addLevel(levelValue);
					} else {
						Status status = new Status(IStatus.ERROR,
								"org.universaal.tools.uAALRunner", 0,
								"Level already exists", null);
						ErrorDialog.openError(Display.getCurrent()
								.getActiveShell(), "Error", "", status);
					}
				}
			} else {
				Status status = new Status(IStatus.ERROR,
						"org.universaal.tools.uAALRunner", 0,
						"Level should be positive number", null);
				ErrorDialog.openError(Display.getCurrent().getActiveShell(),
						"Error", "", status);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Status status = new Status(IStatus.ERROR,
					"org.universaal.tools.uAALRunner", 0, ex.getMessage(), null);
			ErrorDialog.openError(Display.getCurrent().getActiveShell(),
					"Error", "Please enter an integer value.", status);
		}
	}

	/**
	 * Handles add button push.
	 */

	protected void onAddButtonSelected() {
		// get default value for 'level'
		String lvl = "10";
		try {
			TreeItem[] items = m_treeViewer.getTree().getSelection();
			if (items != null && items.length == 1) {
				if (!items[0].getText().startsWith("Level "))
					items[0] = items[0].getParentItem();

				lvl = items[0].getText().split(" ")[1];
			}
		} catch (Exception e) {
		}
		
		// show dialog
		AddEditUrlDialog d = new AddEditUrlDialog(getShell(), lvl);
		if (d.open() != Window.OK)
			return;
		String url = d.getURL();
		String levelValue = d.getLevel();
		
		// add url
		try {
			// check for integer value
			if (Integer.parseInt(levelValue) >= 0) {
				if (!levelExists(levelValue)) {
					addLevel(levelValue);
				}
				addURL(url, levelValue);
			} else {
				Status status = new Status(IStatus.ERROR,
						"org.universaal.tools.uAALRunner", 0,
						"Level should be positive number", null);
				ErrorDialog.openError(Display.getCurrent().getActiveShell(),
						"Error", "", status);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Status status = new Status(IStatus.ERROR,
					"org.universaal.tools.uAALRunner", 0, ex.getMessage(), null);
			ErrorDialog.openError(Display.getCurrent().getActiveShell(),
					"Error", "Please enter an integer value.", status);
		}
	}

	/**
	 * Handles add bundle/file/pom button push.
	 */
	private void onAddSingleFileButtonSelected(final String scanner,
			final String[] filterExtensions, final String[] filterNames) {
		final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
		fileDialog.setText("File Selection");
		fileDialog.setFilterExtensions(filterExtensions);
		fileDialog.setFilterNames(filterNames);
		if (m_lastUsedDir != null) {
			fileDialog.setFilterPath(m_lastUsedDir.getAbsolutePath());
		}
		final String selected = fileDialog.open();

		if (selected != null) {
			final File selectedFile = new File(selected);
			if (selectedFile.exists() && selectedFile.isFile()) {
				m_lastUsedDir = selectedFile.getParentFile();
				InputDialog levelDialog = new InputDialog(getShell(),
						"Enter OSGi level number", "Level number (e.g. 2):",
						null, null);
				if (levelDialog.open() != Window.OK) {
					return;
				}
				String levelValue = levelDialog.getValue().trim();
				try {
					if (Integer.parseInt(levelValue) >= 0) {
						if (!levelExists(levelValue)) {
							addLevel(levelValue);
						}
						addURL(scanner
								+ ":"
								+ selectedFile.getCanonicalFile().toURI()
										.toURL().toExternalForm(),
								levelDialog.getValue());
					} else {
						Status status = new Status(IStatus.ERROR,
								"org.universaal.tools.uAALRunner", 0,
								"Level should be positive number", null);
						ErrorDialog.openError(Display.getCurrent()
								.getActiveShell(), "Error", "", status);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					Status status = new Status(IStatus.ERROR,
							"org.universaal.tools.uAALRunner", 0,
							ex.getMessage(), null);
					ErrorDialog.openError(
							Display.getCurrent().getActiveShell(), "Error",
							"Please enter an integer value.", status);
				}
			}
		}
	}

	/**
	 * Handles add dir button push.
	 */
	protected void onAddDirButtonSelected() {
		final DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setText(PDEUIMessages.BaseBlock_dirSelection);
		dialog.setMessage(PDEUIMessages.BaseBlock_dirChoose);
		if (m_lastUsedDir != null) {
			dialog.setFilterPath(m_lastUsedDir.getAbsolutePath());
		}
		final String selected = dialog.open();

		if (selected != null) {
			final File selectedFile = new File(selected);
			if (selectedFile.exists() && selectedFile.isDirectory()) {
				m_lastUsedDir = selectedFile;
				InputDialog levelDialog = new InputDialog(getShell(),
						"Enter OSGi level number", "Level number (e.g. 2):",
						null, null);
				if (levelDialog.open() != Window.OK) {
					return;
				}
				String levelValue = levelDialog.getValue().trim();
				try {
					if (Integer.parseInt(levelValue) >= 0) {
						if (!levelExists(levelValue)) {
							addLevel(levelValue);
						}
						addURL("scan-dir:"
								+ selectedFile.getCanonicalFile().toURI()
										.toURL().toExternalForm(),
								levelDialog.getValue());
					} else {
						Status status = new Status(IStatus.ERROR,
								"org.universaal.tools.uAALRunner", 0,
								"Level should be positive number", null);
						ErrorDialog.openError(Display.getCurrent()
								.getActiveShell(), "Error", "", status);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					Status status = new Status(IStatus.ERROR,
							"org.universaal.tools.uAALRunner", 0,
							ex.getMessage(), null);
					ErrorDialog.openError(
							Display.getCurrent().getActiveShell(), "Error",
							"Please enter an integer value.", status);
				}
			}
		}
	}

	private void onAddMavenButtonSelected() {
		MessageDialog
				.openInformation(
						getShell(),
						"Information",
						"Not yet implemented, but will allow selection of a Maven artifact from local or remote repository");
	}

	/**
	 * Handles edit button push.
	 */
	protected void onEditButtonSelected() {
		final IStructuredSelection sel = (IStructuredSelection) m_treeViewer
				.getSelection();
		if (sel == null || sel.isEmpty()) {
			return;
		}
		final ProvisionURL provisionURL = (ProvisionURL) sel.iterator().next();
		InputDialog dialog = new InputDialog(getShell(), "Add",
				"Provision from", provisionURL.getUrl(), null);
		if (dialog.open() != Window.OK) {
			return;
		}
		updateURL(provisionURL, dialog.getValue());
		updateStartImages();
		notifyUpdate();
	}

	/**
	 * Handles delete button push.
	 */
	private void onRemoveButtonSelected() {
		getExpandedNodes();
		final IStructuredSelection sel = (IStructuredSelection) m_treeViewer
				.getSelection();
		if (sel == null || sel.isEmpty()) {
			return;
		}
		for (Iterator i = sel.iterator(); i.hasNext();) {
			findAndDeleteProvisionUrl((ProvisionURL) i.next());
		}
		updateExpandedNodes();
	}

	private void findAndDeleteProvisionUrl(ProvisionURL provisionURL) {
		getExpandedNodes();
		List provisionURLs = new ArrayList();
		List selectedURLs = new ArrayList();
		final TreeItem[] items = m_treeViewer.getTree().getItems();
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				final ProvisionURL provisionURL1 = (ProvisionURL) items[i]
						.getData();
				if (provisionURL != provisionURL1) {
					if (provisionURL1.isSelected()) {
						selectedURLs.add(provisionURL1);
					}
					provisionURLs.add(provisionURL1);

					if (provisionURL1.getChildren() != null) {
						for (int j = 0; j < provisionURL1.getChildren().length; j++) {
							ProvisionURL provisionURL2 = provisionURL1
									.getChildren()[j];
							if (provisionURL.equals(provisionURL2)) {
								ProvisionURL[] provisionChildren = new ProvisionURL[provisionURL1
										.getChildren().length - 1];
								int kk = 0;
								for (int k = 0; k < provisionURL1.getChildren().length; k++) {
									if (!provisionURL1.getChildren()[k]
											.equals(provisionURL)) {
										provisionChildren[kk] = provisionURL1
												.getChildren()[k];
										kk++;
										if (provisionURL1.getChildren()[k]
												.isSelected()) {
											selectedURLs.add(provisionURL1
													.getChildren()[k]);
										}
									}
								}
								provisionURL1.setChildren(provisionChildren);
								break;

							} else if (provisionURL != provisionURL2) {
								if (provisionURL2.isSelected()) {
									selectedURLs.add(provisionURL2);
								}
							} else {
								ProvisionURL[] provisionChildren = new ProvisionURL[provisionURL1
										.getChildren().length - 1];
								int kk = 0;
								for (int k = 0; k < provisionURL1.getChildren().length; k++) {
									if (provisionURL1.getChildren()[k] != provisionURL) {
										provisionChildren[kk] = provisionURL1
												.getChildren()[k];
										kk++;
										if (provisionURL1.getChildren()[k]
												.isSelected()) {
											selectedURLs.add(provisionURL1
													.getChildren()[k]);
										}
									}
								}
								provisionURL1.setChildren(provisionChildren);
								break;
							}
						}
					}
				}
			}
			m_treeViewer.setInput(provisionURLs);
			m_treeViewer.expandAll();
			updateExpandedNodes();
			updateStartImages();
			if (selectedURLs.size() > 0) {
				m_treeViewer.setCheckedElements(selectedURLs.toArray());
			}
			notifyUpdate();
		}
	}

	/**
	 * Handles a change on a selection in provision table.
	 * 
	 * @param event
	 *            selection changed event
	 */
	private void onTableSelectionChanged(final SelectionChangedEvent event) {
		try {
			int size = ((IStructuredSelection) event.getSelection()).size();
			if (!((ProvisionURL) ((IStructuredSelection) event.getSelection())
					.getFirstElement()).getUrl().startsWith("Level ")
					&& ((ProvisionURL) ((IStructuredSelection) event
							.getSelection()).getFirstElement()).getParent() != null) {
				m_editButton.setEnabled(size == 1);
			} else {
				m_editButton.setEnabled(false);
			}
			m_deleteButton.setEnabled(size > 0);
		} catch (Exception ex) {
			m_editButton.setEnabled(false);
			m_deleteButton.setEnabled(false);

		}
	}

	private void addLevel(final String level) {
		getExpandedNodes();
		ProvisionURL provisionURL = new ProvisionURL("Level " + level, false,
				true, null, false);
		List provisionURLs = new ArrayList();
		List selectedURLs = new ArrayList();
		if (m_treeViewer.getTree().getItems() != null
				&& m_treeViewer.getTree().getItems().length > 0) {
			for (int i = 0; i < m_treeViewer.getTree().getItems().length; i++) {
				final ProvisionURL provisionURL1 = (ProvisionURL) m_treeViewer
						.getTree().getItems()[i].getData();
				provisionURLs.add(provisionURL1);
				if (provisionURL1.isSelected()) {
					selectedURLs.add(provisionURL1);
				}
				if (provisionURL1.getChildren() != null) {
					for (int j = 0; j < provisionURL1.getChildren().length; j++) {
						ProvisionURL provisionURL2 = provisionURL1
								.getChildren()[j];
						if (provisionURL2.isSelected()) {
							selectedURLs.add(provisionURL2);
						}
					}
				}
			}
		}
		provisionURLs.add(provisionURL);
		m_treeViewer.setInput(provisionURLs);
		m_treeViewer.expandAll();
		updateExpandedNodes();
		// m_treeViewer.expandAll();

		updateStartImages();
		if (selectedURLs.size() > 0) {
			m_treeViewer.setCheckedElements(selectedURLs.toArray());
		}
		notifyUpdate();
	}

	private boolean levelExists(String level) {
		final TreeItem[] items = m_treeViewer.getTree().getItems();
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				final ProvisionURL provisionURL1 = (ProvisionURL) items[i]
						.getData();
				if (provisionURL1.getUrl().startsWith("Level ")
						&& provisionURL1.getUrl().endsWith(" " + level)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Creates a provision url.
	 * 
	 * @param url
	 *            created url
	 */
	private void addURL(final String url, final String level) {
		List provisionURLs = new ArrayList();
		List selectedURLs = new ArrayList();
		getExpandedNodes();
		ProvisionURL provisionURL = new ProvisionURL(url, true, true, null,
				true);
		provisionURL.setStartLevel(Integer.parseInt(level));
		provisionURL.setSelected(true);
		final TreeItem[] items = m_treeViewer.getTree().getItems();
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				final ProvisionURL provisionURL1 = (ProvisionURL) items[i]
						.getData();
				if (provisionURL1.isSelected()) {
					selectedURLs.add(provisionURL1);
				}
				provisionURLs.add(provisionURL1);
				if (provisionURL1.getUrl().startsWith("Level ")
						&& provisionURL1.getUrl().endsWith(" " + level)) {
					// provisionURL1.add(provisionURL);
					if (provisionURL1.getChildren() != null) {
						ProvisionURL[] tmp = new ProvisionURL[provisionURL1
								.getChildren().length + 1];
						for (int j = 0; j < provisionURL1.getChildren().length; j++) {
							tmp[j] = provisionURL1.getChildren()[j];
							if (tmp[j].isSelected()) {
								selectedURLs.add(tmp[j]);
							}
						}
						tmp[provisionURL1.getChildren().length] = provisionURL;
						provisionURL.setParent(provisionURL1);
						provisionURL1.setChildren(tmp);
					} else {
						provisionURL1
								.setChildren(new ProvisionURL[] { provisionURL });
						provisionURL.setParent(provisionURL1);
						provisionURL1.setSelected(true);
						selectedURLs.add(provisionURL1);
					}
					selectedURLs.add(provisionURL);
				} else {
					if (provisionURL1.getChildren() != null) {
						for (int j = 0; j < provisionURL1.getChildren().length; j++) {
							if (provisionURL1.getChildren()[j].isSelected()) {
								selectedURLs
										.add(provisionURL1.getChildren()[j]);
							}
						}
					}
				}
			}
		}
		m_treeViewer.setInput(provisionURLs);

		// m_treeViewer.update(provisionURL, null);
		m_treeViewer.expandAll();
		updateExpandedNodes();
		updateStartImages();
		// m_treeViewer.add( provisionURL );
		if (selectedURLs.size() != 0) {
			m_treeViewer.setCheckedElements(selectedURLs.toArray());
		}
		notifyUpdate();
	}

	private void addURL(final String url, final String level,
			final boolean isStart, final boolean isUpdate,
			final boolean isSelected) {
		List provisionURLs = new ArrayList();
		List selectedURLs = new ArrayList();
		getExpandedNodes();
		ProvisionURL provisionURL = new ProvisionURL(url, isSelected, isStart,
				null, isUpdate);
		provisionURL.setStartLevel(Integer.parseInt(level));

		final TreeItem[] items = m_treeViewer.getTree().getItems();
		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				final ProvisionURL provisionURL1 = (ProvisionURL) items[i]
						.getData();
				if (provisionURL1.isSelected()) {
					selectedURLs.add(provisionURL1);
				}
				provisionURLs.add(provisionURL1);
				if (provisionURL1.getUrl().startsWith("Level ")
						&& provisionURL1.getUrl().endsWith(" " + level)) {
					// provisionURL1.add(provisionURL);
					if (provisionURL1.getChildren() != null) {
						ProvisionURL[] tmp = new ProvisionURL[provisionURL1
								.getChildren().length + 1];
						for (int j = 0; j < provisionURL1.getChildren().length; j++) {
							tmp[j] = provisionURL1.getChildren()[j];
							if (tmp[j].isSelected()) {
								selectedURLs.add(tmp[j]);
							}
						}
						tmp[provisionURL1.getChildren().length] = provisionURL;
						provisionURL.setParent(provisionURL1);
						provisionURL1.setChildren(tmp);
					} else {
						provisionURL1
								.setChildren(new ProvisionURL[] { provisionURL });
						provisionURL.setParent(provisionURL1);
						provisionURL1.setSelected(true);
						selectedURLs.add(provisionURL1);
					}
					selectedURLs.add(provisionURL);
				} else {
					if (provisionURL1.getChildren() != null) {
						for (int j = 0; j < provisionURL1.getChildren().length; j++) {
							if (provisionURL1.getChildren()[j].isSelected()) {
								selectedURLs
										.add(provisionURL1.getChildren()[j]);
							}
						}
					}
				}
			}
		}
		m_treeViewer.setInput(provisionURLs);

		// m_treeViewer.update(provisionURL, null);
		m_treeViewer.expandAll();
		updateExpandedNodes();
		updateStartImages();
		// m_treeViewer.add( provisionURL );
		if (selectedURLs.size() != 0) {
			m_treeViewer.setCheckedElements(selectedURLs.toArray());
		}
		notifyUpdate();
	}

	/**
	 * Updates a provision url after edit.
	 * 
	 * @param provisionURL
	 *            to be updated
	 * @param url
	 *            new url
	 */
	private void updateURL(final ProvisionURL provisionURL, final String url) {
		provisionURL.setUrl(url);
		m_treeViewer.update(provisionURL, null);
		notifyUpdate();
	}

	private boolean checkIfLevelExists(List provisionURLs, String level) {
		for (int i = 0; i < provisionURLs.size(); i++) {
			if (provisionURLs.get(i) != null
					&& ((ProvisionURL) provisionURLs.get(i)).getUrl()
							.startsWith("Level")
					&& ((ProvisionURL) provisionURLs.get(i)).getUrl()
							.split(" ")[1].equals(level)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Initialize block.
	 * 
	 * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */

	public void initializeFrom(final ILaunchConfiguration configuration) {
		try {
			final Map toRestore = (Map) configuration.getAttribute(
					Attribute.PROVISION_ITEMS, new HashMap());
			final List provisionURLs = new ArrayList();
			final List selectedURLs = new ArrayList();

			for (int i = 1; i < 8; i++) {
				ProvisionURL level = new ProvisionURL();
				level.setUrl("Level " + i);
				level.setParent(null);
				level.setSelected(false);
				level.setStart(false);
				provisionURLs.add(level);
			}

			// create level nodes
			for (Iterator iterator = toRestore.entrySet().iterator(); iterator
					.hasNext();) {
				final Map.Entry entry = (Map.Entry) iterator.next();
				final ProvisionURL provisionURL = new ProvisionURL();
				provisionURL.setUrl((String) entry.getKey());

				if (entry.getValue() != null) {
					final String[] options = ((String) entry.getValue())
							.split("@");
					// first option: selected - boolean
					if (options.length >= 1) {
						provisionURL.setSelected(Boolean.valueOf(options[0])
								.booleanValue());
					}
					// second option: start - boolean
					if (options.length >= 2) {
						provisionURL.setStart(Boolean.valueOf(options[1])
								.booleanValue());
					}
					// third option: start level - integer ("null" if not
					// specified)
					if (options.length >= 3 && !options[2].equals("null")) {
						try {
							provisionURL.setStartLevel(Integer
									.valueOf(options[2]));

						} catch (NumberFormatException ignore) {
							// ignore. actually it should not happen
						}
					}
					// forth option: update - boolean
					if (options.length >= 4) {
						provisionURL.setUpdate(Boolean.valueOf(options[3])
								.booleanValue());
					}
				}
				if (provisionURL.getStartLevel() != null) {
					if (!checkIfLevelExists(provisionURLs, provisionURL
							.getStartLevel().toString())) {
						ProvisionURL level = new ProvisionURL();
						level.setUrl("Level " + provisionURL.getStartLevel());
						level.setParent(null);
						level.setSelected(false);
						level.setStart(false);

						provisionURLs.add(level);

					}
				}
			}

			for (Iterator iterator = toRestore.entrySet().iterator(); iterator
					.hasNext();) {
				final Map.Entry entry = (Map.Entry) iterator.next();
				final ProvisionURL provisionURL = new ProvisionURL();
				provisionURL.setUrl((String) entry.getKey());

				if (entry.getValue() != null) {
					final String[] options = ((String) entry.getValue())
							.split("@");
					// first option: selected - boolean
					if (options.length >= 1) {
						provisionURL.setSelected(Boolean.valueOf(options[0])
								.booleanValue());
					}
					// second option: start - boolean
					if (options.length >= 2) {
						provisionURL.setStart(Boolean.valueOf(options[1])
								.booleanValue());
					}
					// third option: start level - integer ("null" if not
					// specified)
					if (options.length >= 3 && !options[2].equals("null")) {
						try {
							provisionURL.setStartLevel(Integer
									.valueOf(options[2]));

						} catch (NumberFormatException ignore) {
							// ignore. actually it should not happen
						}
					}
					// forth option: update - boolean
					if (options.length >= 4) {
						provisionURL.setUpdate(Boolean.valueOf(options[3])
								.booleanValue());
					}
				}
				// search for parent level node
				boolean levelFound = false;
				for (int i = 0; i < provisionURLs.size(); i++) {
					ProvisionURL p = (ProvisionURL) provisionURLs.get(i);
					if (p.getParent() == null) {

						if (provisionURL.getStartLevel() != null
								&& provisionURL.getStartLevel().intValue() == (Integer
										.parseInt(p.getUrl().split(" ")[1]))) {
							levelFound = true;
							p.add(provisionURL);
							provisionURL.setParent(p);
							if (p.getChildren() != null) {
								ProvisionURL[] tmp = new ProvisionURL[p
										.getChildren().length + 1];
								for (int j = 0; j < p.getChildren().length; j++) {
									tmp[j] = p.getChildren()[j];
								}
								tmp[p.getChildren().length] = provisionURL;
								p.setChildren(tmp);
							} else {
								p.setChildren(new ProvisionURL[] { provisionURL });
							}
							if (provisionURL.isSelected()) {
								selectedURLs.add(provisionURL);
								// m_treeViewer.setChecked(provisionURL, true);
							}
						}
					}
				}

				// provisionURLs.add( provisionURL );
				// if( provisionURL.isSelected() )
				// {
				// selectedURLs.add( provisionURL );
				// }
			}
			m_treeViewer.setInput(provisionURLs);
			m_treeViewer.expandAll();
			updateStartImages();

			// check if level nodes have all children checked
			for (int i = 0; i < provisionURLs.size(); i++) {
				ProvisionURL provisionURL = (ProvisionURL) provisionURLs.get(i);
				boolean allSelected = true;
				if (provisionURL.getChildren() != null) {
					for (int j = 0; j < provisionURL.getChildren().length; j++) {
						if (!provisionURL.getChildren()[j].isSelected()) {
							allSelected = false;
						}
					}
				} else {
					allSelected = false;
				}
				if (allSelected) {
					selectedURLs.add(provisionURL);
					provisionURL.setSelected(true);
				}
			}
			if (selectedURLs.size() > 0) {
				m_treeViewer.setCheckedElements(selectedURLs.toArray());
			}
		} catch (CoreException ignore) {
			// DebugUIPlugin.log(ignore.getStatus());
		}
	}

	private void updateStartImages() {
		ImageData ideaImage = new ImageData(getClass().getResourceAsStream(
				"/images/yy.jpg"));
		Image yy = new Image(Display.getCurrent(), ideaImage);
		ImageData ideaImage2 = new ImageData(getClass().getResourceAsStream(
				"/images/yn.jpg"));
		Image yn = new Image(Display.getCurrent(), ideaImage2);
		ImageData ideaImage3 = new ImageData(getClass().getResourceAsStream(
				"/images/ny.jpg"));
		Image ny = new Image(Display.getCurrent(), ideaImage3);
		ImageData ideaImage4 = new ImageData(getClass().getResourceAsStream(
				"/images/nn.jpg"));
		Image nn = new Image(Display.getCurrent(), ideaImage4);

		for (int i = 0; i < m_treeViewer.getTree().getItems().length; i++) {
			TreeItem it = m_treeViewer.getTree().getItems()[i];
			boolean allChildrenStarted = true;

			boolean allChildrenUpdated = true;

			for (int j = 0; j < it.getItemCount(); j++) {
				TreeItem it2 = it.getItems()[j];

				if (((ProvisionURL) it2.getData()) != null
						&& ((ProvisionURL) it2.getData()).isStart()
						&& ((ProvisionURL) it2.getData()).isUpdate()) {
					it2.setImage(yy);
				} else if (((ProvisionURL) it2.getData()) != null
						&& ((ProvisionURL) it2.getData()).isStart()
						&& !((ProvisionURL) it2.getData()).isUpdate()) {
					allChildrenUpdated = false;
					it2.setImage(yn);
				} else if (((ProvisionURL) it2.getData()) != null
						&& !((ProvisionURL) it2.getData()).isStart()
						&& ((ProvisionURL) it2.getData()).isUpdate()) {
					allChildrenStarted = false;
					it2.setImage(ny);
				} else {
					allChildrenStarted = false;
					allChildrenUpdated = false;
					it2.setImage(nn);
				}
			}
			if (allChildrenStarted && allChildrenUpdated) {
				it.setImage(yy);
				((ProvisionURL) it.getData()).setStart(true);
				((ProvisionURL) it.getData()).setUpdate(true);
			} else if (allChildrenStarted && !allChildrenUpdated) {
				it.setImage(yn);
				((ProvisionURL) it.getData()).setStart(true);
				((ProvisionURL) it.getData()).setUpdate(false);
			} else if (!allChildrenStarted && allChildrenUpdated) {
				it.setImage(ny);
				((ProvisionURL) it.getData()).setStart(false);
				((ProvisionURL) it.getData()).setUpdate(true);
			} else if (!allChildrenStarted && !allChildrenUpdated) {
				it.setImage(nn);
				((ProvisionURL) it.getData()).setStart(false);
				((ProvisionURL) it.getData()).setUpdate(false);
			}
			if (it.getParentItem() == null && it.getItemCount() == 0) {
				it.setImage(nn);
				((ProvisionURL) it.getData()).setStart(false);
				((ProvisionURL) it.getData()).setUpdate(false);
			}
		}
	}

	/**
	 * Saves block configurations attributes.<br/>
	 * It will save a map where the key is the url and the value are the other
	 * options as selected@start@start_level@update.
	 * 
	 * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
		// finally, save arguments list
		List arguments = null;
		try {
			arguments = configuration.getAttribute(Attribute.RUN_ARGUMENTS,
					(List) null);
		} catch (CoreException ignore) {
			// DebugUIPlugin.log(ignore.getStatus());;
		}
		final TreeItem[] items = m_treeViewer.getTree().getItems();
		Map toSave = null;
		if (items != null && items.length > 0) {
			if (arguments == null) {
				arguments = new ArrayList();
			}
			toSave = new HashMap();
			for (int i = 0; i < items.length; i++) {
				final ProvisionURL provisionURL1 = (ProvisionURL) items[i]
						.getData();
				if (provisionURL1.getChildren() != null) {
					for (int j = 0; j < provisionURL1.getChildren().length; j++) {
						ProvisionURL provisionURL = provisionURL1.getChildren()[j];
						if (!provisionURL.getUrl().startsWith("Level ")) {
							StringBuffer options = new StringBuffer()
									.append(m_treeViewer
											.getChecked(provisionURL))
									.append("@").append(provisionURL.isStart())
									.append("@")
									.append(provisionURL.getStartLevel())
									.append("@")
									.append(provisionURL.isUpdate());
							toSave.put(provisionURL.getUrl(),
									options.toString());
							if (provisionURL.isSelected()) {
								final StringBuffer provisionFrom = new StringBuffer(
										provisionURL.getUrl());
								if (provisionURL.getStartLevel() != null) {
									provisionFrom.append("@").append(
											provisionURL.getStartLevel());
								}
								if (!provisionURL.isStart()) {
									provisionFrom.append("@nostart");
								}
								if (provisionURL.isUpdate()) {
									provisionFrom.append("@update");
								}
								arguments.add(provisionFrom.toString());
							}
						}
					}
				}
			}
		}
		configuration.setAttribute(Attribute.PROVISION_ITEMS, toSave);
		configuration.setAttribute(Attribute.RUN_ARGUMENTS, arguments);
	}
}
