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
package org.universaal.tools.transformationcommand.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.universaal.tools.transformationcommand.activator.Activator;

public class Uml2JavaPreferencePage
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {

	StringFieldEditor javaPathName;
	StringFieldEditor testPathName;
	StringFieldEditor rootPathName;
	BooleanFieldEditor absoluteBoolean;

	public Uml2JavaPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Please enter a path for the files to be saved to. " +
				"If \" Use absolute path\" is not checked, the path refers to a folder " +
				"relative to the project folder.");
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		absoluteBoolean = new BooleanFieldEditor(PreferenceConstants.P_UML2JAVA_ABSOLUTE_BOOLEAN, "Use absolute path.", getFieldEditorParent());
		rootPathName = new StringFieldEditor(PreferenceConstants.P_UML2JAVA_ROOTPATH, "Please enter the desired root output path (blank for project root)", getFieldEditorParent());
		javaPathName = new StringFieldEditor(PreferenceConstants.P_UML2JAVA_JAVAPATH, "Please enter the relative subdirectory within the root for Java files.", getFieldEditorParent());
		testPathName = new StringFieldEditor(PreferenceConstants.P_UML2JAVA_TESTPATH, "Please enter the relative subdirectory within the root for Java test files.", getFieldEditorParent());

		addField(absoluteBoolean);
		addField(rootPathName);
		addField(javaPathName);
		addField(testPathName);


	}

}
