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
package org.universaal.tools.configurationEditor.dialogs;

import java.util.LinkedList;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;

public class ValidatorDialog extends TitleAreaDialog  {

	private Composite container;
	
	private Text classText;
	private List attributeList;
	
	private String validatorClass;
	private LinkedList<String> attributes;
	
	public ValidatorDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void create() {
		
		super.create();

		// Set the title
		setTitle("Add validator");
		
		
		// Set the message
		setMessage("Add a validator to the selected config item", IMessageProvider.NONE);
		
		attributes = new LinkedList<String>();

	}

	
	 @Override
	protected Control createDialogArea(Composite parent) {
		 
	 	Composite area = (Composite) super.createDialogArea(parent);
        container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
	 
	    GridLayout layout = new GridLayout(3, false);
	    container.setLayout(layout);

	   //  The text fields will grow with the size of the dialog
	    GridData gridData = new GridData();
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	   // gridData.horizontalSpan = 2;

	    Label label1 = new Label(container, SWT.NONE);
	    label1.setText("Class:");

	    classText = new Text(container, SWT.BORDER);
	    classText.setLayoutData(gridData);
	    
	    
	    Button selectClassButton = new Button(container, SWT.PUSH);
	    selectClassButton.setText("Select class");
	    
	    selectClassButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				SelectionDialog dialog;
				try {
					dialog = JavaUI.createTypeDialog(getShell(), new ProgressMonitorDialog(getShell()), SearchEngine.createWorkspaceScope(),  IJavaElementSearchConstants.CONSIDER_CLASSES, false);
					dialog.setTitle("Validator class");
					dialog.setMessage("Select validator a class");
					dialog.open(); 
					
					Object[] types= dialog.getResult();
					
					if(types != null && types.length != 0) {
						IType selectedClass = (IType)types[0];
						classText.setText(selectedClass.getFullyQualifiedName('.'));
					}
				} catch (JavaModelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
				
			}
		});
	    
	    gridData = new GridData();
//	    gridData.grabExcessHorizontalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    selectClassButton.setLayoutData(gridData);
	    
	    
	    Label attrLabel = new Label(container, SWT.NONE);
	    attrLabel.setText("Attributes:");
	    gridData = new GridData();
	    gridData.verticalAlignment = SWT.TOP;
	    attrLabel.setLayoutData(gridData);
	    
	    attributeList = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
	    gridData = new GridData();
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    attributeList.setLayoutData(gridData);
	    
	    Button addAttributeButton = new Button(container, SWT.PUSH);
	    addAttributeButton.setText("Add attribute");
	    
	    addAttributeButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				AttributeDialog ad = new AttributeDialog(getShell());
				ad.create();
				ad.open();
	
				if(ad.getReturnCode() == Window.OK){
					attributeList.add(ad.getAttribute());
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
				
			}
		});
	    
	    gridData = new GridData();
	    gridData.verticalAlignment = SWT.TOP;
	    addAttributeButton.setLayoutData(gridData);
	    
	    return area;
	 }
	 
	 
	  @Override
	  protected void createButtonsForButtonBar(Composite parent) {
	    // Create Add button
	    // Own method as we need to overview the SelectionAdapter
	    createOkButton(parent, OK, "Add", true);
	    // Add a SelectionListener

	    // Create Cancel button
	    Button cancelButton = 
	        createButton(parent, CANCEL, "Cancel", false);
	    // Add a SelectionListener
	    cancelButton.addSelectionListener(new SelectionAdapter() {
	      @Override
		public void widgetSelected(SelectionEvent e) {
	        setReturnCode(CANCEL);
	        close();
	      }
	    });
	  }
	  
	  
	  protected Button createOkButton(Composite parent, int id, 
		      String label,
		      boolean defaultButton) {
		    // increment the number of columns in the button bar
		    ((GridLayout) parent.getLayout()).numColumns++;
		    Button button = new Button(parent, SWT.PUSH);
		    button.setText(label);
		    button.setFont(JFaceResources.getDialogFont());
		    button.setData(new Integer(id));
		    button.addSelectionListener(new SelectionAdapter() {
		      @Override
			public void widgetSelected(SelectionEvent event) {
		        //if (isValidInput()) {
		          okPressed();
		       // }
		      }
		    });
		    if (defaultButton) {
		      Shell shell = parent.getShell();
		      if (shell != null) {
		        shell.setDefaultButton(button);
		      }
		    }
		    setButtonLayoutData(button);
		    return button;
		  }

		@Override
		protected void okPressed() {
			saveInput();
			super.okPressed();
		}

		private void saveInput() {
			this.validatorClass = classText.getText();
			for (String attr : attributeList.getItems()) {
				this.attributes.add(attr);
			}

		}
		
		public String getValidatorClass(){
			return this.validatorClass;
		}
		
		public LinkedList<String> getAttributes(){
			return this.attributes;
		}

}


