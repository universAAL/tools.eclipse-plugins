/*
	Copyright 2011-2014 CERTH-ITI, http://www.iti.gr
	Information Technologies Institute (ITI)
	Centre For Research and Technology Hellas (CERTH)
	
	
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
package org.universaal.tools.externalserviceintegrator.actions;

import java.awt.Event;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Tree;
import swing2swt.layout.BoxLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.universAAL.ri.wsdlToolkit.invocation.Axis2WebServiceInvoker;
import org.universAAL.ri.wsdlToolkit.invocation.InvocationResult;
import org.universAAL.ri.wsdlToolkit.ioApi.ComplexObject;
import org.universAAL.ri.wsdlToolkit.ioApi.NativeObject;
import org.universAAL.ri.wsdlToolkit.ioApi.ParsedWSDLDefinition;
import org.universAAL.ri.wsdlToolkit.ioApi.WSOperation;

public class TestInvocation extends Dialog {

	protected Object result;
	protected Shell shell;
	private ParsedWSDLDefinition theParsedDefinition;
	private String operationName;
	private WSOperation wsOperation;
	private Text text;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public TestInvocation(Shell parent, int style,
			ParsedWSDLDefinition theParsedDefinition, String operationName) {
		super(parent, style);
		this.operationName = operationName;
		this.theParsedDefinition = theParsedDefinition;
		setText("Invoke \"" + operationName + "\" operation");
		for (int i = 0; i < theParsedDefinition.getWsdlOperations().size(); i++) {
			if (((WSOperation) theParsedDefinition.getWsdlOperations().get(i))
					.getOperationName().equals(operationName)) {
				wsOperation = (WSOperation) theParsedDefinition
						.getWsdlOperations().get(i);
			}
		}
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.SHELL_TRIM | SWT.BORDER);
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new BorderLayout(0, 0));

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.SOUTH);
		composite_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		Button btnNewButton = new Button(composite_1, SWT.NONE);

		btnNewButton.setText("Invoke");

		Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.CENTER);
		composite_2.setLayout(new BorderLayout(0, 0));

		final Composite composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setLayoutData(BorderLayout.SOUTH);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_3.setVisible(false);
		Composite composite_5 = new Composite(composite_3, SWT.NONE);
		composite_5.setLayout(new GridLayout(3, false));

		final Label lblMandatory = new Label(composite_5, SWT.NONE);
		lblMandatory.setText("Mandatory: ");
		new Label(composite_5, SWT.NONE);

		final Label mandatoryLabel = new Label(composite_5, SWT.NONE);
		mandatoryLabel.setText("New Label");

		final Label inputName = new Label(composite_5, SWT.NONE);
		inputName.setText("New Label");

		Composite composite_6 = new Composite(composite_5, SWT.NONE);
		composite_6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		text = new Text(composite_5, SWT.BORDER);

		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite composite_4 = new Composite(composite_2, SWT.NONE);
		composite_4.setLayoutData(BorderLayout.CENTER);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Tree inputsTree = new Tree(composite_4, SWT.BORDER);
		inputsTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event e) {
				
				TreeItem[] selection = inputsTree.getSelection();
				if (selection[0] != null) {
					if (selection[0].getData() instanceof NativeObject) {
						lblMandatory.setText("Mandatory:");
						
						NativeObject no = (NativeObject) selection[0].getData();
						text.setText(no.getHasValue());
						mandatoryLabel.setText(String.valueOf(!no
								.isIsOptional()));
						inputName.setText(no.getObjectName().getLocalPart()
								+ ":");
						composite_3.setVisible(true);
					} else {
						composite_3.setVisible(false);
					}
				}
			}
		});

		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				TreeItem[] selection = inputsTree.getSelection();
				if (selection[0] != null) {
					if (selection[0].getData() instanceof NativeObject) {
						NativeObject no = (NativeObject) selection[0].getData();
						no.setHasValue(text.getText());
					}
				}

			}
		});
		final Tree outputsTree = new Tree(composite_4, SWT.BORDER);
		
		outputsTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event e) {
				
				TreeItem[] selection = outputsTree.getSelection();
				if (selection[0] != null) {
					if (selection[0].getData() instanceof NativeObject) {
						lblMandatory.setText("");
						NativeObject no = (NativeObject) selection[0].getData();
						text.setText(no.getHasValue());
						mandatoryLabel.setText("");
						inputName.setText(no.getObjectName().getLocalPart()
								+ ":");
						composite_3.setVisible(true);
					} else {
						composite_3.setVisible(false);
					}
				}
			}
		});
		
		
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					InvocationResult invocationResult = Axis2WebServiceInvoker
							.invokeWebService(
									theParsedDefinition.getWsdlURL(),
									new QName(theParsedDefinition
											.getTargetNamespaceURI(),
											wsOperation.getOperationName()),
									wsOperation.getHasInput(), wsOperation,
									theParsedDefinition);
					if(invocationResult==null){
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Invocation Error occured");
						return;
					}
					calculateOperationTree(outputsTree, false);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Invocation Error occured");
				}
			}
		});
		calculateOperationTree(inputsTree, true);
	}

	private void calculateOperationTree(Tree tree, boolean isInput) {
		tree.removeAll();
		if (isInput) {
			TreeItem inputs = new TreeItem(tree, 0);
			inputs.setText("Inputs");
			inputs.setData(wsOperation.getHasInput());
			calculateChildren(inputs, wsOperation.getHasInput()
					.getHasNativeOrComplexObjects());
		} else {
			TreeItem outputs = new TreeItem(tree, 0);
			outputs.setText("Outputs");
			outputs.setData(wsOperation.getHasOutput());
			calculateChildren(outputs, wsOperation.getHasOutput()
					.getHasNativeOrComplexObjects());
		}
	}

	private void calculateChildren(TreeItem treeItem, Vector vec) {
		for (int i = 0; i < vec.size(); i++) {
			if (vec.get(i) instanceof NativeObject) {
				NativeObject no = (NativeObject) vec.get(i);
				TreeItem item = new TreeItem(treeItem, 0);
				if (no.isIsOptional()) {
					item.setText(no.getObjectName().getLocalPart());
					item.setData(no);
				} else {
					item.setText(no.getObjectName().getLocalPart() + "*");
					item.setData(no);
				}
			} else if (vec.get(i) instanceof ComplexObject) {
				ComplexObject co = (ComplexObject) vec.get(i);
				TreeItem item = new TreeItem(treeItem, 0);
				item.setText(co.getObjectName().getLocalPart());
				item.setData(co);
				calculateChildren(item, co.getHasComplexObjects());
				calculateChildren(item, co.getHasNativeObjects());
			}
		}
	}
}
