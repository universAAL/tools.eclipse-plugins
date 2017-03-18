/*
	Copyright 2011 SINTEF, http://www.sintef.no
	
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
package org.universaal.tools.transformationcommand.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.universaal.tools.transformationcommand.activator.Activator;

public class TransformOntUML2Java extends TransformationHandler {
	//static final String TRANSFORMATION_FILENAME = "transformations/ontUML2JavaV2.m2t";
	static final String TRANSFORMATION_FILENAME = "transformations/OntologyUML2Java";
	static final String THIS_BUNDLE_NAME = Activator.PLUGIN_ID;
	private static final String SOURCE_FILE_SUFFIX = ".uml";

	public TransformOntUML2Java() {
		setFileAndBundleName(TRANSFORMATION_FILENAME + "_1_3_0.m2t", THIS_BUNDLE_NAME);
	}
		
	@Override
	public void doTransform(IFile inputFile, ExecutionEvent event) {
		IWorkbenchWindow window;
		try {
			window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			if (window != null) {
				ElementListSelectionDialog dialog = 
						  new ElementListSelectionDialog(window.getShell(), new LabelProvider());
						dialog.setElements(new String[] { "1_1_0", "1_2_0", "1_3_0" , "2_0_0", "3_0_0", "3_1_0", "3_2_0", "3_3_0", "3_4_0" });
						dialog.setTitle("Please select middleware version to transform to:");
						// User pressed cancel
						if (dialog.open() != Window.OK) {
						    return;
						}
						Object[] result = dialog.getResult();
						if (result.length == 1) {
							if (((String)result[0]).startsWith("3_"))
								result[0] = "3_0_0";
							setFileAndBundleName(TRANSFORMATION_FILENAME + "_" + (String)result[0] + ".m2t", THIS_BUNDLE_NAME);
							super.doTransform(inputFile, event);
						}
				}
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean dualMetamodel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getSourceFileSuffix() {
		// TODO Auto-generated method stub
		return SOURCE_FILE_SUFFIX;
	}
}
