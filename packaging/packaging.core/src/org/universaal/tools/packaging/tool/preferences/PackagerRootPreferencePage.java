/*

        Copyright 2007-2014 CNR-ISTI, http://isti.cnr.it
        Institute of Information Science and Technologies
        of the Italian National Research Council

	Copyright 2007-2014 SINTEF, http://www.sintef.no
	
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
package org.universaal.tools.packaging.tool.preferences;

import java.util.Arrays;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.universaal.tools.packaging.tool.Activator;

/**
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:erlend.stav@sintef.no">Erlend Stav</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class PackagerRootPreferencePage extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

    @Override
    protected void checkState() {
	// TODO Auto-generated method stub
	super.checkState();
    }


    @Override
    public boolean performOk() {
	// TODO Auto-generated method stub
	return super.performOk();
    }


    @Override
    public void propertyChange(PropertyChangeEvent event) {
	
	FieldEditor source = (FieldEditor) event.getSource();	
	System.out.println(source.getLabelText());
	System.out.println(event.getProperty()+":"+event.getOldValue()+" -> "+event.getNewValue());
	
	super.propertyChange(event);
    }

    BooleanFieldEditor logToConsole;
    
    ComboFieldEditor logLevel;
    String[][] allowedLogLevels = new String[][] { 
    		{"debug", 			"DEBUG"}, 
    		{"info", 			"INFO"},
    		{"warnings", 		"WARN"},
    		{"errors",			"ERROR"},
    		{"fatal errors",	"FATAL"}};
    
    StringFieldEditor mavenGoalKarafFeature;
    StringFieldEditor mavenGoalKar;
    StringFieldEditor mavenKarafPluginGroup;
    StringFieldEditor mavenKarafPluginVersion;
    BooleanFieldEditor offlineMode;
    BooleanFieldEditor persistence;
    StringFieldEditor mavenKarafPluginName;
    FileFieldEditor mavenCommand;
    BooleanFieldEditor mavenEmbedded;
    private GroupFieldEditor mavenGoals;
    private GroupFieldEditor karafPlugin;

    public PackagerRootPreferencePage() {
	super(FLAT);
	setPreferenceStore(Activator.getDefault().getPreferenceStore());
	setDescription("Here you can customize the behavior of uAAL UAAP Packager");
    }

    
    public void init(IWorkbench workbench) {
	// Intentionally left blank
    }

    @Override
    protected void createFieldEditors() {
	
	mavenGoals = new GroupFieldEditor("Name of Maven Goal for Maven Karaf Plugin", getFieldEditorParent());
	
	logToConsole = new BooleanFieldEditor(
		ConfigProperties.ENABLE_CONSOLE_LOG_KEY,
		"Enable log on the console", getFieldEditorParent());

	logLevel = new ComboFieldEditor(ConfigProperties.LOG_LEVEL_KEY, 
			"Log level", allowedLogLevels, getFieldEditorParent());
	
	offlineMode = new BooleanFieldEditor(ConfigProperties.OFFLINE_MODE_KEY,
			"Offline Mode", getFieldEditorParent());

	persistence = new BooleanFieldEditor(ConfigProperties.RECOVERY_MODE_KEY,
			"Data Persistence", getFieldEditorParent());

	mavenGoalKarafFeature = new StringFieldEditor(
		ConfigProperties.KARAF_PLUGIN_GOAL_FEATURE_KEY,
		"Goal for generating the XML feature file",
		mavenGoals.getFieldEditorParent());

	mavenGoalKar = new StringFieldEditor(
		ConfigProperties.KARAF_PLUGIN_GOAL_KAR_KEY,
		"Goal for generating the KAR file",
		mavenGoals.getFieldEditorParent());

	karafPlugin  = new GroupFieldEditor("Karaf Maven Plugin to invoke", getFieldEditorParent());

	mavenKarafPluginGroup = new StringFieldEditor(
		ConfigProperties.KARAF_PLUGIN_GROUP_KEY,
		"GroupId",
		karafPlugin.getFieldEditorParent());

	mavenKarafPluginVersion = new StringFieldEditor(
		ConfigProperties.KARAF_PLUGIN_VERSION_KEY,
		"Version",
		karafPlugin.getFieldEditorParent());

	mavenKarafPluginName = new StringFieldEditor(
		ConfigProperties.KARAF_PLUGIN_NAME_KEY,
		"ArtifacId",
		karafPlugin.getFieldEditorParent());
	
	mavenCommand = new FileFieldEditor(
		ConfigProperties.MAVEN_COMMAND_KEY,
		"The command to execute for starting Maven",
		getFieldEditorParent());
	
	mavenCommand.setEmptyStringAllowed(true);	

	mavenEmbedded = new BooleanFieldEditor(
		ConfigProperties.MAVEN_EMBEDDED_KEY,
		"Use maven embedded in Eclipse", getFieldEditorParent());
	
	mavenGoals.setFieldEditors(Arrays.asList(new FieldEditor[]{mavenGoalKar,mavenGoalKarafFeature}));
	karafPlugin.setFieldEditors(Arrays.asList(new FieldEditor[]{mavenKarafPluginGroup,mavenKarafPluginVersion,mavenKarafPluginName}));
	
	addField(logToConsole);
	addField(logLevel);
	addField(offlineMode);
	addField(persistence);
	addField(mavenGoals);
	addField(karafPlugin);
	addField(mavenEmbedded);
	addField(mavenCommand);
	/*
	addField(mavenKarafPluginGroup);
	addField(mavenKarafPluginName);
	addField(mavenKarafPluginVersion);
	*/
	
	
    }

    
}