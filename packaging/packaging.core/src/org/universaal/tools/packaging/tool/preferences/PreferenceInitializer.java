/*

        Copyright 2007-2014 CNR-ISTI, http://isti.cnr.it
        Institute of Information Science and Technologies
        of the Italian National Research Council

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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.universaal.tools.packaging.tool.Activator;

/**
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    private void setDefaultAndValue(IPreferenceStore store, String key,
	    String def, String val) {
	if ( store.contains(key) ) {
	    /*
	     * The key was already set so Eclipse will take care of loading old values
	     */
	    store.setDefault(key, def);
	} else {
	    /*
	     * The first time initialization
	     */
	    store.setDefault(key, def);
	    store.setValue(key, val);
	}	
    }

    private void setDefaultAndValue(IPreferenceStore store, String key,
	    boolean def, boolean val) {
	if ( store.contains(key) ) {
	    /*
	     * The key was already set so Eclipse will take care of loading old values
	     */
	    store.setDefault(key, def);
	} else {
	    /*
	     * The first time initialization
	     */
	    store.setDefault(key, def);
	    store.setValue(key, val);
	}	
    }

    @Override
    public void initializeDefaultPreferences() {
	final IPreferenceStore store = Activator.getDefault()
		.getPreferenceStore();

	final SystemPropertiesConfigurator config = SystemPropertiesConfigurator.local;

	setDefaultAndValue(
			store,
			ConfigProperties.ENABLE_CONSOLE_LOG_KEY,
			Boolean.parseBoolean(ConfigProperties.ENABLE_CONSOLE_LOG_DEFAULT),
			config.isConsoleLog());

	setDefaultAndValue(store,
		ConfigProperties.KARAF_PLUGIN_GOAL_FEATURE_KEY,
		ConfigProperties.KARAF_PLUGIN_GOAL_FEATURE_DEFAULT,
		config.getKarafPluginFeatureGoal());

	setDefaultAndValue(store, ConfigProperties.KARAF_PLUGIN_GOAL_KAR_KEY,
		ConfigProperties.KARAF_PLUGIN_GOAL_KAR_DEFAULT,
		config.getKarafPluginKarGoal());

	setDefaultAndValue(store, ConfigProperties.KARAF_PLUGIN_GROUP_KEY,
		ConfigProperties.KARAF_PLUGIN_GROUP_DEFAULT,
		config.getKarafPluginGroupId());

	setDefaultAndValue(store, ConfigProperties.KARAF_PLUGIN_NAME_KEY,
		ConfigProperties.KARAF_PLUGIN_NAME_DEFAULT,
		config.getKarafPluginArtifactId());

	setDefaultAndValue(store, ConfigProperties.KARAF_PLUGIN_VERSION_KEY,
		ConfigProperties.KARAF_PLUGIN_VERSION_DEFAULT,
		config.getKarafPluginVersion());

	setDefaultAndValue(store, ConfigProperties.OFFLINE_MODE_KEY,
			Boolean.parseBoolean(ConfigProperties.OFFLINE_MODE_DEFAULT),
			config.isOfflineMode());

	setDefaultAndValue(store, ConfigProperties.RECOVERY_MODE_KEY,
			Boolean.parseBoolean(ConfigProperties.RECOVERY_MODE_KEY_DEFAULT),
			config.isPersistanceEnabled());

	setDefaultAndValue(store, ConfigProperties.MAVEN_COMMAND_KEY,
		ConfigProperties.MAVEN_COMMAND_DEFAULT,
		config.getMavenCommand());

	setDefaultAndValue(store, ConfigProperties.MAVEN_EMBEDDED_KEY,
		Boolean.parseBoolean(ConfigProperties.MAVEN_EMBEDDED_DEFAULT),
		config.runMavenEmbedded());
    }

}
