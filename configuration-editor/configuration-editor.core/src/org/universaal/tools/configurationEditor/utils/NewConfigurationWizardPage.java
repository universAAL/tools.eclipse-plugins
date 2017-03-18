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
package org.universaal.tools.configurationEditor.utils;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.universaal.tools.configurationEditor.Activator;

public class NewConfigurationWizardPage extends WizardNewFileCreationPage {

    public NewConfigurationWizardPage(IStructuredSelection selection) {
        super("NewConfigFileWizardPage", selection);
        setTitle("New Configuration");
        setDescription("Creates a new configuration file");
        setFileName("newConfiguration");
        setFileExtension("xml");
    }

    @Override
    protected InputStream getInitialContents() {
        try {
            return Activator.getDefault().getBundle().getEntry("resources/defaultConfiguration.xml").openStream();
        } catch (IOException e) {
            return null; // ignore and create empty comments
        }
    }
    
}