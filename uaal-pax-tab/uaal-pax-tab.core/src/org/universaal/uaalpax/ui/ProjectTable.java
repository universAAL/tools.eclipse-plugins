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
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.universaal.uaalpax.model.BundleEntry;
import org.universaal.uaalpax.model.BundleModel;

public class ProjectTable extends Composite {
	private CheckboxTableViewer tableViewer;
	private ArrayList<BundleEntry> allElements = new ArrayList<BundleEntry>();
	
	private LaunchChangeListener changeListener;
	
	private BundleComparator comparator;
	
	private BundleDoubleClickListener bundleClickListener;
	
	private BundleModel model;
	
	public ProjectTable(Composite parent, int style) {
		super(parent, style);
		
		TableColumnLayout layout = new TableColumnLayout();
		setLayout(layout);
		
		tableViewer = CheckboxTableViewer.newCheckList(this, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		final Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final CheckboxTableViewer viewer = tableViewer;
		viewer.setContentProvider(new ProjectContentProvider());
		viewer.setLabelProvider(new ProjectLabelProvider(viewer));
		
		viewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(final CheckStateChangedEvent event) {
				BundleEntry be = (BundleEntry) event.getElement();
				if(tableViewer.getGrayed(be)) {
					if(!event.getChecked())
						tableViewer.setChecked(be, true); // assume grayed elements as always checked
					return;
				}
					
				be.setSelected(event.getChecked());
				notifyElementChanged(be);
			}
		});
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		comparator = new BundleComparator();
		viewer.setComparator(comparator);
		
		final TableColumn urlTableColumn = new TableColumn(table, SWT.NONE);
		urlTableColumn.setWidth(100);
		urlTableColumn.setText(BundleEntry.PROP_PROJECT);
		urlTableColumn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (comparator.getSortProperty() == BundleEntry.PROP_PROJECT)
					comparator.toggleSortDirection();
				else {
					comparator.setSortProperty(BundleEntry.PROP_PROJECT);
					comparator.setSortDirection(SWT.DOWN);
				}
				table.setSortColumn(urlTableColumn);
				table.setSortDirection(comparator.getSortDirection());
				// viewer.refresh(true);
				inputChanged();
			}
		});
		
		final TableColumn levelTableColumn = new TableColumn(table, SWT.CENTER);
		levelTableColumn.setWidth(50);
		levelTableColumn.setText(BundleEntry.PROP_LEVEL);
		levelTableColumn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (comparator.getSortProperty() == BundleEntry.PROP_LEVEL)
					comparator.toggleSortDirection();
				else {
					comparator.setSortProperty(BundleEntry.PROP_LEVEL);
					comparator.setSortDirection(SWT.DOWN);
				}
				table.setSortColumn(levelTableColumn);
				table.setSortDirection(comparator.getSortDirection());
				//viewer.refresh(true);
				inputChanged();
			}
		});
		
		final ProjectCellModifier cellModifier = new ProjectCellModifier(viewer);
		cellModifier.setModifyListener(new ProjectCellModifier.ModifyListener() {
			public void modified(BundleEntry element) {
				viewer.update(element, null);
				notifyElementChanged(element);
			}
		});
		
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				cellModifier.setEnabled(false);
			}
		});
		
		table.addListener(SWT.MouseDoubleClick, new Listener() {
			private final String[] projectProperties = new String[] { BundleEntry.PROP_PROJECT };
			
			public void handleEvent(Event event) {
				TableItem[] selection = table.getSelection();
				
				if (selection.length != 1) {
					return;
				}
				
				TableItem item = table.getSelection()[0];
				
				for (int i = 0; i < table.getColumnCount(); i++) {
					if (item.getBounds(i).contains(event.x, event.y)) {
						if (i == 0 && item.getImage() != null) {
							Rectangle r = item.getImageBounds(0);
							if (r.contains(event.x, event.y)) {
								BundleEntry be = (BundleEntry) item.getData();
								if (event.x - r.x >= r.width / 2) // update toggled
									be.setUpdate(!be.isUpdate());
								else
									// start toggled
									be.setStart(!be.isStart());
								
								viewer.update(be, projectProperties);
								notifyElementChanged(be);
								return;
							}
						}
						
						if (viewer.getColumnProperties()[i].equals(BundleEntry.PROP_PROJECT)) {
							if (bundleClickListener != null) {
								bundleClickListener.onBundleClicked((BundleEntry) item.getData());
								return;
							}
						}
						
						cellModifier.setEnabled(true);
						viewer.editElement(item.getData(), i);
						cellModifier.setEnabled(false);
					}
				}
			}
		});
		
		viewer.setCellModifier(cellModifier);
		viewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(table) });
		viewer.setColumnProperties(new String[] { BundleEntry.PROP_PROJECT, BundleEntry.PROP_LEVEL });
		
		layout.setColumnData(urlTableColumn, new ColumnWeightData(100));
		layout.setColumnData(levelTableColumn, new ColumnWeightData(0, 50));
		
		viewer.setInput(allElements);
	}
	
	void setModel(BundleModel model) {
		this.model = model;
	}
	
	public void setBundleDoubleclickListener(BundleDoubleClickListener listener) {
		bundleClickListener = listener;
	}
	
	private void notifyChanged() {
		if (changeListener != null)
			changeListener.notifyChanged();
		
		model.updatePresenters();
	}
	
	private void notifyElementChanged(BundleEntry be) {
		model.getBundles().updateBundleOptions(be);
		notifyChanged();
	}
	
	public void setChangeListener(LaunchChangeListener l) {
		changeListener = l;
	}
	
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}
	
	public void remove(Object element) {
		allElements.remove(element);
		inputChanged();
	}
	
	public void add(BundleEntry element) {
		allElements.add(element);
		inputChanged();
	}
	
	public int getItemCount() {
		return allElements.size();
	}
	
	public BundleEntry getElement(int index) {
		return allElements.get(index);
	}
	
	public List<BundleEntry> getElements() {
		return allElements;
	}
	
	public void removeAll() {
		allElements.clear();
		inputChanged();
	}
	
	public void addAll(Collection<BundleEntry> elements) {
		allElements.addAll(elements);
		inputChanged();
	}
	
	private void inputChanged() {
		Object[] grayed = tableViewer.getGrayedElements();
		tableViewer.setInput(allElements);
		for (BundleEntry pu : allElements)
			tableViewer.setChecked(pu, pu.isSelected());
		tableViewer.setGrayed(grayed, true);
		tableViewer.refresh(true);
	}
	
	public TableViewer getViewer() {
		return tableViewer;
	}
	
	public void setGrayed(BundleEntry[] bundles) {
		tableViewer.setGrayedElements(bundles);	
	}
	
	public boolean isBundleGrayed(BundleEntry be) {
		return tableViewer.getGrayed(be);
	}
	
	public interface BundleDoubleClickListener {
		public void onBundleClicked(BundleEntry be);
	}
}
