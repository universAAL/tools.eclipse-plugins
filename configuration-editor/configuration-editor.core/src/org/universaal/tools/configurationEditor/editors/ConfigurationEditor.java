/*
	Copyright 2011 FZI, http://www.fzi.de

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
package org.universaal.tools.configurationEditor.editors;

import java.io.IOException;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.ide.IDE;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.universaal.tools.configurationEditor.Activator;
import org.universaal.tools.configurationEditor.dialogs.CategoryDialog;
import org.universaal.tools.configurationEditor.dialogs.MapConfigItemDialog;
import org.universaal.tools.configurationEditor.dialogs.SPARQLConfigItemDialog;
import org.universaal.tools.configurationEditor.dialogs.SimpleConfigItemDialog;
import org.universaal.tools.configurationEditor.dialogs.ValidatorDialog;
import org.universaal.tools.configurationEditor.utils.TreeListener;
import org.universaal.tools.configurationEditor.utils.WidgetListener;
import org.universaal.tools.configurationEditor.utils.WidgetMapping;

public class ConfigurationEditor extends MultiPageEditorPart implements IResourceChangeListener {

	
	
	/** The text editor used in text editor. */
	private TextEditor editor;

	private ExpandBar mainBar;

	private Document doc;

	private WidgetListener widgetListener;

	protected boolean dirty = false;
	
	private  Group selectedItemGroup; 
	
	private  ExpandItem catListItem;
	
	private Composite catListComp;
	
	private final int treeHeight = 500;
	
	private Tree tree;
	
	

	/**
	 * Creates a multi-page editor example.
	 */
	public ConfigurationEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		widgetListener = new WidgetListener(this);
		TrayDialog.setDialogHelpAvailable(false);

	}

	// --------------------------------------------------------------------------------------------------------------------------

	void createConfigPage() {

		Composite composite = new Composite(getContainer(), SWT.NONE);
		
		FillLayout layout = new FillLayout();
		
		composite.setLayout(layout);

		mainBar = new ExpandBar(composite, SWT.V_SCROLL);
		createMainBar(editor.getDocumentProvider().getDocument(editor.getEditorInput()).get(), mainBar);
		
		//createMainBar(getEditorInput(), mainBar);
		

		int index = addPage(composite);
		setPageText(index, "Configuration Editor");

	}

	private void createMainBar(String xml, ExpandBar mainBar) {

		Composite mainComp = new Composite(mainBar, SWT.NONE);
		GridLayout mainLayout = new GridLayout(2, false);
		mainComp.setLayout(mainLayout);

		try {
			
			this.doc = new SAXBuilder().build(new StringReader(xml));
			Element config = doc.getRootElement();

			Label l1;
			Text t1;

			ExpandItem itemMain = new ExpandItem(mainBar, SWT.NONE, 0);
			itemMain.setText("Configuration");
			itemMain.setControl(mainComp);

			List<Attribute> ats = config.getAttributes();

			for (Attribute at : ats) {

				l1 = new Label(mainComp, SWT.NONE);
				l1.setText(at.getName() + ":");

				t1 = new Text(mainComp, SWT.SINGLE | SWT.BORDER);
				t1.setEditable(true);
				t1.setText(at.getValue());

				// ------------- <Listener> --------------

				WidgetMapping.put(t1, at);
				t1.addModifyListener(widgetListener);

				// ------------- </Listener> --------------

				GridData gd = new GridData();
				gd.horizontalAlignment = GridData.FILL;
				gd.grabExcessHorizontalSpace = true;
				t1.setLayoutData(gd);

			}

			itemMain.setHeight(mainComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			itemMain.setExpanded(true);

			createCategoryList(config);

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void createCategoryList(Element root){
		
		catListItem = new ExpandItem(mainBar, SWT.NONE, 1);
		catListItem.setText("Categories");
		
		catListItem.setExpanded(true);
		
		catListComp = new Composite(mainBar, SWT.NONE);
		GridLayout catLayout = new GridLayout(2, false);
		catListComp.setLayout(catLayout);
		
// ----------------------------------- Menu -----------------------------------
		
		final Menu treeMenu = new Menu (catListComp.getShell(), SWT.POP_UP);

		// ----------------------------------- Category -----------------------------------		
		MenuItem categoryItem = new MenuItem (treeMenu, SWT.PUSH);
		categoryItem.setText("New category");
		
		categoryItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				CategoryDialog cd  = new CategoryDialog(getSite().getShell());
				cd.create();
				cd.open();
				
				if(cd.getReturnCode() == Window.OK) {
					//create new category
					Element newCat = new Element("category", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
					newCat.setAttribute("id", cd.getId());
					newCat.setAttribute("label", cd.getLabel());
	
					//add to doc
					doc.getRootElement().addContent(newCat);
					
					
					//add to the tree
					TreeItem catItem = new TreeItem (tree, tree.getItemCount());
					catItem.setText(cd.getId());
					
					WidgetMapping.put(catItem, newCat);
					
					//dirty
					setDirty(true);
					}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e); 
			}
		});
		
		
		// ----------------------------------- SimpleConfigItem -----------------------------------
		MenuItem simpleConfigItem = new MenuItem (treeMenu, SWT.PUSH);
		simpleConfigItem.setText("New SimpleConfigItem");
		
		simpleConfigItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SimpleConfigItemDialog cd = new SimpleConfigItemDialog(getSite().getShell());
				cd.create();
				cd.open();
				
				if(cd.getReturnCode() == Window.OK) {
					//get selected tree item
					TreeItem selectedTreeItem  = tree.getSelection()[0];
					if(selectedTreeItem != null) {

						Element selectedElement  = WidgetMapping.getElement(selectedTreeItem);
						
						//add element to a category only
						if(selectedElement.getName().equals("category")){
							
							//create new configItem
							Element configItem = new Element("SimpleConfigItem", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
							configItem.setAttribute("cardinality", cd.getCardinality());
							configItem.setAttribute("id", cd.getId());
							configItem.setAttribute("type", cd.getType());
							
							Element label = new Element("label", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
							label.setText(cd.getLabel());
							configItem.addContent(label);
							
							Element description = new Element("description", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
							description.setText(cd.getDescription());
							configItem.addContent(description);
							
							Element defValue = new Element("defaultValue", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
							defValue.setText(cd.getDefaultValue());
							configItem.addContent(defValue);
							
							//add to doc
							selectedElement.addContent(configItem);
							
							//add to the tree
							TreeItem simpleConfigItem = new TreeItem (selectedTreeItem, selectedTreeItem.getItemCount());
							simpleConfigItem.setText(cd.getId());
							
							WidgetMapping.put(simpleConfigItem, configItem);
							
							//set drity
							setDirty(true);
						}
						
					}
				}
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);

			}
		});
	
		// ----------------------------------- MapConfigItem -----------------------------------
		MenuItem mapConfigItem = new MenuItem (treeMenu, SWT.PUSH);
		mapConfigItem.setText("New MapConfigItem");
		
		mapConfigItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				MapConfigItemDialog cd = new MapConfigItemDialog(getSite().getShell());
				cd.create();
				cd.open();
				
				if(cd.getReturnCode() == Window.OK) {
					//get selected tree item
					TreeItem selectedTreeItem  = tree.getSelection()[0];
					if(selectedTreeItem != null) {

						Element selectedElement  = WidgetMapping.getElement(selectedTreeItem);
						
						//add element to a category only
						if(selectedElement.getName().equals("category")){
							
							//create new configItem
							Element configItem = new Element("MapConfigItem", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
							configItem.setAttribute("cardinality", cd.getCardinality());
							configItem.setAttribute("id", cd.getId());
							configItem.setAttribute("active", cd.getActive());
							
							Element label = new Element("label", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
							label.setText(cd.getLabel());
							configItem.addContent(label);
							
							Element description = new Element("description", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
							description.setText(cd.getDescription());
							configItem.addContent(description);
							
							Element options = new Element("options", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
							
							//add options
							int i = 1;
							for (String attr : cd.getOptions()) {
								Element optionElement = new Element("option", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
								optionElement.setAttribute("key", ""+i++);
								optionElement.setText(attr);
								
								options.addContent(optionElement);
							}
							
							
							
							configItem.addContent(options);
							
							//add to doc
							selectedElement.addContent(configItem);
							
							//add to the tree
							TreeItem simpleConfigItem = new TreeItem (selectedTreeItem, selectedTreeItem.getItemCount());
							simpleConfigItem.setText(cd.getId());
							
							WidgetMapping.put(simpleConfigItem, configItem);
							
							//set drity
							setDirty(true);
							
							
						}
					}
				}
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
				
			}
			
		});

		// ----------------------------------- SPARQLConfigItem -----------------------------------
			MenuItem SPARQLConfigItem = new MenuItem (treeMenu, SWT.PUSH);
			SPARQLConfigItem.setText("New SPARQLConfigItem");
			
			SPARQLConfigItem.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					SPARQLConfigItemDialog cd = new SPARQLConfigItemDialog(getSite().getShell());
					cd.create();
					cd.open();
					
					if(cd.getReturnCode() == Window.OK) {
						//get selected tree item
						TreeItem selectedTreeItem  = tree.getSelection()[0];
						if(selectedTreeItem != null) {

							Element selectedElement  = WidgetMapping.getElement(selectedTreeItem);
							
							//add element to a category only
							if(selectedElement.getName().equals("category")){
								
								//create new configItem
								Element configItem = new Element("SPARQLConfigItem", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
								configItem.setAttribute("cardinality", cd.getCardinality());
								configItem.setAttribute("id", cd.getId());
								configItem.setAttribute("active", cd.getActive());
								
								Element label = new Element("label", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
								label.setText(cd.getLabel());
								configItem.addContent(label);
								
								Element description = new Element("description", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
								description.setText(cd.getDescription());
								configItem.addContent(description);
								
								Element query = new Element("query", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
								query.setText(cd.getQuery());
								configItem.addContent(query);
								
								//add to doc
								selectedElement.addContent(configItem);
								
								//add to the tree
								TreeItem simpleConfigItem = new TreeItem (selectedTreeItem, selectedTreeItem.getItemCount());
								simpleConfigItem.setText(cd.getId());
								
								WidgetMapping.put(simpleConfigItem, configItem);
								
								//set drity
								setDirty(true);
								
								
							}
						}
					}
					
				}


				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
				
			});
		// ----------------------------------- Validator -----------------------------------
		MenuItem newValidatorItem = new MenuItem (treeMenu, SWT.PUSH);
		newValidatorItem.setText("New validator");
		
		newValidatorItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ValidatorDialog cd = new ValidatorDialog(getSite().getShell());
				cd.create();
				cd.open();
				
				if(cd.getReturnCode() == Window.OK) {
					//get selected tree item
					TreeItem selectedTreeItem  = tree.getSelection()[0];
					if(selectedTreeItem != null) {

						Element selectedElement  = WidgetMapping.getElement(selectedTreeItem);
						//add element to a configItem only
						if(selectedElement.getName().contains("ConfigItem")){
							//validator element
							Element validator = new Element("validator", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
							validator.setAttribute("class", cd.getValidatorClass());
							
							//add attributes
							for (String attr : cd.getAttributes()) {
								Element attrElement = new Element("attribute", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
								attrElement.setText(attr);
								
								validator.addContent(attrElement);
							}
							
							//check validators node
							Element validators = selectedElement.getChild("validators", doc.getRootElement().getNamespace());
							if(validators == null){
								validators = new Element("validators", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
								selectedElement.addContent(validators);
							}
							
							//add validator
							validators.addContent(validator);
							
							//add to the tree
							TreeItem validatorItem = new TreeItem (selectedTreeItem, selectedTreeItem.getItemCount());
							validatorItem.setText("Validator");
							
							WidgetMapping.put(validatorItem, validator);
							
							//set drity
							setDirty(true);
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);

			}
		
		});
		
		//----------------------- Add Element ----------------------
		
		final ToolBar toolBar = new ToolBar (catListComp, SWT.NONE);
		
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		
		toolBar.setLayoutData(gd);
		
		final ToolItem addItem = new ToolItem (toolBar, SWT.DROP_DOWN);
		
		//addItem.setText("Add...");

		ImageDescriptor image = Activator.getImageDescriptor("icons/new_con.gif");
		addItem.setImage(image.createImage());
	
		addItem.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event event) {
				if (event.detail == SWT.ARROW) {
					Rectangle rect = addItem.getBounds ();
					Point pt = new Point (rect.x, rect.y + rect.height);
					pt = toolBar.toDisplay (pt);
					treeMenu.setLocation (pt.x, pt.y);
					treeMenu.setVisible (true);
				} else {
					//default click
					CategoryDialog cd  = new CategoryDialog(getSite().getShell());
					cd.create();
					cd.open();
					
					//create new category
					Element newCat = new Element("category", doc.getRootElement().getNamespacePrefix(), doc.getRootElement().getNamespaceURI());
					newCat.setAttribute("id", cd.getId());
					newCat.setAttribute("label", cd.getLabel());
					//newCat.setNamespace(Namespace.getNamespace("universaal"));
					
					//add to doc
					doc.getRootElement().addContent(newCat);
					
					
					//add to the tree
					TreeItem catItem = new TreeItem (tree, tree.getItemCount());
					catItem.setText(cd.getId());
					
					WidgetMapping.put(catItem, newCat);
					
					//dirty
					setDirty(true);
				}
			}
		});
		
		
		
	
		
		final ToolItem removeItem = new ToolItem (toolBar, SWT.PUSH);
		
//		removeItem.setText("Remove");

		image = Activator.getImageDescriptor("icons/delete_obj.gif");
		removeItem.setImage(image.createImage());
		
		removeItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				TreeItem selectedItem = tree.getSelection()[0];
				selectedItem.dispose();

				
				
				//remove element and set drity
				Element el = WidgetMapping.removeElement(selectedItem);
				el.detach();
				//doc.removeContent(el);
				editorChanged();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
				
			}
		});

		//tree
		tree = new Tree (catListComp, SWT.BORDER | SWT.SINGLE);
		
		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
//		gd.horizontalSpan = 2;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		
		tree.setLayoutData(gd);
		
	
		
		//categories
		int i = 0;
		for (Element catEl : root.getChildren()) {
			TreeItem catItem = new TreeItem (tree, i++);
			catItem.setText(catEl.getAttributeValue("id"));
			
			WidgetMapping.put(catItem, catEl);
			
			//configItems
			int j = 0;
			for(Element catElChild : catEl.getChildren()){
				TreeItem catChildItem = new TreeItem(catItem, j++);
				catChildItem.setText(catElChild.getAttributeValue("id"));
				
				WidgetMapping.put(catChildItem, catElChild);
				
				//validators
				int k = 0;
				for(Element validators : catElChild.getChildren()){
					if(validators.getName().equals("validators")){
						for (Element validator : validators.getChildren()) {
							TreeItem validatorItem = new TreeItem(catChildItem, k++);
							//validatorItem.setText(validator.getAttributeValue("class"));
							validatorItem.setText("Validator");
							
							WidgetMapping.put(validatorItem, validator);
						}
				
					}
				}
			
				
			}
			
		}
		
		
		
		 tree.addListener(SWT.Selection, new TreeListener(this));
		
		
		
		
		selectedItemGroup = new Group(catListComp, SWT.NONE);
		
		
		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		
		selectedItemGroup.setLayoutData(gd);
		
		selectedItemGroup.setLayout(new GridLayout(2, false));
		
		selectedItemGroup.setVisible(false);
		
		
		//height & control
		
		catListItem.setControl(catListComp);


		catListItem.setHeight(treeHeight);
		
	}
	
	public void showSelectedElement(Element el) {
		
//		System.out.println(el.getName());
		
		if(el.getName().equals("category")) {
			showCategory(el);
			selectedItemGroup.setText("Category");
			
		} else if(el.getName().equals("SimpleConfigItem") ) {
			showSimpleConfigItem(el);
			selectedItemGroup.setText("SimpleConfigItem");
			
		} else if(el.getName().equals("MapConfigItem") ) {
			showMapConfigItem(el);
			selectedItemGroup.setText("MapConfigItem");
			
		}else if(el.getName().equals("SPARQLConfigItem") ) {
			showSimpleConfigItem(el);
			selectedItemGroup.setText("SPARQLConfigItem");
			
		}else if(el.getName().equals("validator")) {
			showValidator(el);
			selectedItemGroup.setText("Validator");
			
		}
		selectedItemGroup.layout(true);
		selectedItemGroup.setVisible(true);
		
	
	}
	
	
	private void showCategory(Element el){
	
		for (Control wi : selectedItemGroup.getChildren()) {
			wi.dispose();
		}
		
		Label l1;
		Text t1;

		List<Attribute> cAts = el.getAttributes();

		for (Attribute at : cAts) {
			
			l1 = new Label(selectedItemGroup, SWT.NONE);
			l1.setText(at.getName() + ":");

			t1 = new Text(selectedItemGroup, SWT.SINGLE | SWT.BORDER);
			t1.setEditable(true);
			t1.setText(at.getValue());

			// ------------- <Listener> --------------

			WidgetMapping.put(t1, at);
			t1.addModifyListener(widgetListener);

			// ------------- </Listener> --------------

			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			t1.setLayoutData(gd);
			
			

		}
			
	}
	
	private void showSimpleConfigItem(Element el) {
		for (Control wi : selectedItemGroup.getChildren()) {
			wi.dispose();
		}
		
		List<Attribute> tiAtts = el.getAttributes();
		Label l1;
		Text t1;

		for (Attribute att : tiAtts) {
			l1 = new Label(selectedItemGroup, SWT.NONE);
			l1.setText(att.getName() + ":");

			t1 = new Text(selectedItemGroup, SWT.SINGLE | SWT.BORDER);
			t1.setEditable(true);
			t1.setText(att.getValue());

			// ------------- <Listener> --------------

			WidgetMapping.put(t1, att);
			t1.addModifyListener(widgetListener);

			// ------------- </Listener> --------------

			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			t1.setLayoutData(gd);
		}

		// TI's children
		List<Element> tiEls = el.getChildren();

		for (Element tiEl : tiEls) {
				
			if (tiEl.getName().equals("validators")) {
				//DO NOTHING!
			} else {	
				
				l1 = new Label(selectedItemGroup, SWT.NONE);
				l1.setText(tiEl.getName() + ":");

				t1 = new Text(selectedItemGroup, SWT.SINGLE | SWT.BORDER);
				t1.setEditable(true);
				t1.setText(tiEl.getValue());

				// ------------- <Listener> --------------

				WidgetMapping.put(t1, tiEl);
				t1.addModifyListener(widgetListener);

				// ------------- </Listener> --------------

				GridData gd = new GridData();
				gd.horizontalAlignment = GridData.FILL;
				gd.grabExcessHorizontalSpace = true;
				t1.setLayoutData(gd);
			}
		}
		
	}
	
	private void showMapConfigItem(Element el) {
		for (Control wi : selectedItemGroup.getChildren()) {
			wi.dispose();
		}
		
		List<Attribute> tiAtts = el.getAttributes();
		Label l1;
		Text t1;

		for (Attribute att : tiAtts) {
			l1 = new Label(selectedItemGroup, SWT.NONE);
			l1.setText(att.getName() + ":");

			t1 = new Text(selectedItemGroup, SWT.SINGLE | SWT.BORDER);
			t1.setEditable(true);
			t1.setText(att.getValue());

			// ------------- <Listener> --------------

			WidgetMapping.put(t1, att);
			t1.addModifyListener(widgetListener);

			// ------------- </Listener> --------------

			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			t1.setLayoutData(gd);
		}

		// TI's children
		List<Element> tiEls = el.getChildren();

		for (Element tiEl : tiEls) {

			// mapConfigItem
			if (tiEl.getName().equals("options")) {
//				l1 = new Label(selectedItemGroup, SWT.NONE);
//				l1.setText(tiEl.getName() + ":");

//				Combo combo = new Combo(selectedItemGroup, SWT.NONE | SWT.READ_ONLY);

				List<Element> opt = tiEl.getChildren();
				for (Element e : opt) {
					l1 = new Label(selectedItemGroup, SWT.NONE);
					l1.setText(e.getName() + ":");
					t1 = new Text(selectedItemGroup, SWT.SINGLE | SWT.BORDER);
					t1.setEditable(true);
					t1.setText(e.getValue());
					
					// ------------- <Listener> --------------
					WidgetMapping.put(t1, e);
					t1.addModifyListener(widgetListener);
					// ------------- </Listener> --------------
					
					GridData gd = new GridData();
					gd.horizontalAlignment = GridData.FILL;
					gd.grabExcessHorizontalSpace = true;
					t1.setLayoutData(gd);
				}

//				GridData gd = new GridData();
//				gd.horizontalAlignment = GridData.FILL;
//				gd.grabExcessHorizontalSpace = true;
//				combo.setLayoutData(gd);

			// Validators
			} else if (tiEl.getName().equals("validators")) {
				//DO NOTHING!
			} else {	
				
				l1 = new Label(selectedItemGroup, SWT.NONE);
				l1.setText(tiEl.getName() + ":");

				t1 = new Text(selectedItemGroup, SWT.SINGLE | SWT.BORDER);
				t1.setEditable(true);
				t1.setText(tiEl.getValue());

				// ------------- <Listener> --------------

				WidgetMapping.put(t1, tiEl);
				t1.addModifyListener(widgetListener);

				// ------------- </Listener> --------------

				GridData gd = new GridData();
				gd.horizontalAlignment = GridData.FILL;
				gd.grabExcessHorizontalSpace = true;
				t1.setLayoutData(gd);
			}
		}

	}
	
	
	private void showValidator(Element e) {
		for (Control wi : selectedItemGroup.getChildren()) {
			wi.dispose();
		}
		Label l1;
		Text t1;
		
			l1 = new Label(selectedItemGroup, SWT.NONE);
			l1.setText("class:");
			l1.pack();

			t1 = new Text(selectedItemGroup, SWT.SINGLE | SWT.BORDER);
			t1.setEditable(true);
			t1.setText(e.getAttributeValue("class"));
			t1.pack();

			// ------------- <Listener> --------------

			WidgetMapping.put(t1, e.getAttribute("class"));
			t1.addModifyListener(widgetListener);

			// ------------- </Listener> --------------

			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			t1.setLayoutData(gd);

			// Validator attributs
			List<Element> vAtts = e.getChildren();

			for (Element vAt : vAtts) {
				l1 = new Label(selectedItemGroup, SWT.NONE);
				l1.setText(vAt.getName() + ":");

				t1 = new Text(selectedItemGroup, SWT.SINGLE | SWT.BORDER);
				t1.setEditable(true);
				t1.setText(vAt.getValue());

				// ------------- <Listener> --------------

				WidgetMapping.put(t1, vAt);
				t1.addModifyListener(widgetListener);

				// ------------- </Listener> --------------

				gd = new GridData();
				gd.horizontalAlignment = GridData.FILL;
				gd.grabExcessHorizontalSpace = true;
				t1.setLayoutData(gd);
			}
	}
	

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createEditorPage() {
		try {
			editor = new TextEditor();
			
			

			int index = addPage(editor, getEditorInput());
			setPageText(index, editor.getTitle());
			
			 IDocumentProvider dp = editor.getDocumentProvider();
			 IDocument doc = dp.getDocument(editor.getEditorInput());
			 
			 doc.addDocumentListener(new IDocumentListener() {
				
				@Override
				public void documentChanged(DocumentEvent event) {
					setDirty(true);
					
				}
				
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
					setDirty(true);
				}
			});
			
			
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	@Override
	protected void createPages() {
		createEditorPage();
		createConfigPage();

		setActivePage(1);

	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {

		// xml -> config
		if (getActivePage() == 0) {
			getEditor(0).doSave(monitor);
			removePage(1);
			createConfigPage();

			// config -> xml
		} else if (getActivePage() == 1) {
			XMLOutputter outp = new XMLOutputter();

			outp.setFormat(Format.getPrettyFormat());

			StringWriter sw = new StringWriter();
			try {
				outp.output(doc.getContent(), sw);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringBuffer sb = sw.getBuffer();
			String editorText = sb.toString();

			editor.getDocumentProvider().getDocument(editor.getEditorInput()).set(editorText);
			getEditor(0).doSave(monitor);
		}
		setDirty(false);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	@Override
	public void doSaveAs() {
		doSave(null);
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Closes all project files on project close.
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) editor.getEditorInput())
								.getFile().getProject()
								.equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(editor
									.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	protected void setDirty(boolean value) {
		dirty = value;
		firePropertyChange(PROP_DIRTY);
	}

	public void editorChanged() {
		setDirty(true);
	}


}
