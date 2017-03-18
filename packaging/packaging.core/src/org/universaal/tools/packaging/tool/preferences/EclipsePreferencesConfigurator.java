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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.jface.preference.IPreferenceStore;
import org.universaal.tools.packaging.tool.Activator;
import org.universaal.tools.packaging.tool.util.DefaultLogger;
import org.universaal.tools.packaging.tool.util.ProcessExecutor;

/**
 * A singleton for sharing the configuration among all the classes of the plugin
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class EclipsePreferencesConfigurator {

    public Boolean mEmbSet = false;

    final private IPreferenceStore store;

    public static final EclipsePreferencesConfigurator local = new EclipsePreferencesConfigurator();

    private EclipsePreferencesConfigurator() {
	store = Activator.getDefault().getPreferenceStore();
    }
    private String getString(String key, String def) {
	if (store.contains(key)) {
	    return store.getString(key);
	}
	return System.getProperty(key, def);
    }

    private int getEnum(String key, int def, Map<String, Integer> enumeration){
	String enumerationKey = getString(key, "").toLowerCase();
	Integer enumerationValue = enumeration.get(enumerationKey);
	if ( enumerationValue == null ) {
	    return def;
	} else {
	    return enumerationValue.intValue();
	}
    }
    
    private boolean getBoolean(String key, boolean def) {
	if (store.contains(key)) {
	    return store.getBoolean(key);
	}
	return Boolean.parseBoolean(System.getProperty(key,
		Boolean.toString(def)));
    }


    public String getRecoveryFileName() {
	return getString(ConfigProperties.RECOVERY_FILE_NAME_KEY,
		ConfigProperties.RECOVERY_FILE_NAME_DEFAULT);
    }

    public String getRecoveryPartsName() {
	return getString(ConfigProperties.RECOVERY_PARTS_NAME_KEY,
		ConfigProperties.RECOVERY_PARTS_NAME_DEFAULT);
    }

    public String getTempFolder() {
	String paths[] = new String[] {
		getString(ConfigProperties.TMP_DIR_KEY,
			ConfigProperties.TMP_DIR_DEFAULT),
		System.getenv("tmp") + File.separatorChar + UUID.randomUUID(),
		System.getenv("temp") + File.separatorChar + UUID.randomUUID(),
		System.getenv("TMP") + File.separatorChar + UUID.randomUUID(),
		System.getenv("TEMP") + File.separatorChar + UUID.randomUUID(),
		"." + File.separatorChar + UUID.randomUUID(), };
	for (int i = 0; i < paths.length; i++) {
	    final String path = paths[i];
	    File folder = getFolder(path);
	    if (folder != null && folder.canWrite()) {
		return path;
	    }
	}
	return null;
    }

    private File getFolder(String path) {
	if (path == null)
	    return null;
	File dir = new File(path);
	if (dir == null || (dir.exists() && !dir.isDirectory()))
	    return null;
	if (!dir.exists()) {
	    if (dir.mkdirs() == false)
		return null;
	}
	return dir;
    }

    public File getLogFolder() {
	File folder = getFolder(getString(
		ConfigProperties.LOG_DIR_KEY, ConfigProperties.LOG_DIR_DEFAULT));
	if (folder == null || folder.canWrite() == false) {
	    return null;
	}
	return folder;
    }

    public boolean isConsoleLog() {
	return getBoolean(
		ConfigProperties.ENABLE_CONSOLE_LOG_KEY,
		Boolean.parseBoolean(ConfigProperties.ENABLE_CONSOLE_LOG_DEFAULT));
    }

    public String getKarafPluginGroupId() {
	return getString(ConfigProperties.KARAF_PLUGIN_GROUP_KEY,
		ConfigProperties.KARAF_PLUGIN_GROUP_DEFAULT);
    }

    public String getKarafPluginArtifactId() {
	return getString(ConfigProperties.KARAF_PLUGIN_NAME_KEY,
		ConfigProperties.KARAF_PLUGIN_NAME_DEFAULT);
    }

    public String getKarafPluginVersion() {
	return getString(ConfigProperties.KARAF_PLUGIN_VERSION_KEY,
		ConfigProperties.KARAF_PLUGIN_VERSION_DEFAULT);
    }

    public String getKarafPluginFeatureGoal() {
	return getString(
		ConfigProperties.KARAF_PLUGIN_GOAL_FEATURE_KEY,
		ConfigProperties.KARAF_PLUGIN_GOAL_FEATURE_DEFAULT);
    }

    public String getMavenCommand() {
	return getString(ConfigProperties.MAVEN_COMMAND_KEY,
		ConfigProperties.MAVEN_COMMAND_DEFAULT);
    }

    public boolean isOfflineMode() {
	return getBoolean(ConfigProperties.OFFLINE_MODE_KEY,
		Boolean.parseBoolean(ConfigProperties.OFFLINE_MODE_DEFAULT));
    }

    public boolean isPersistanceEnabled() {
	return getBoolean(ConfigProperties.RECOVERY_MODE_KEY,
		Boolean.parseBoolean(ConfigProperties.RECOVERY_MODE_KEY_DEFAULT));
	}
    
    public String getKarafPluginKarGoal() {
	return getString(ConfigProperties.KARAF_PLUGIN_GOAL_KAR_KEY,
		ConfigProperties.KARAF_PLUGIN_GOAL_KAR_DEFAULT);
    }

    public boolean runMavenEmbedded() {
	boolean mEmb = getBoolean(ConfigProperties.MAVEN_EMBEDDED_KEY,
		Boolean.parseBoolean(ConfigProperties.MAVEN_EMBEDDED_DEFAULT));
	if (!mEmb && !mEmbSet) {
	    try {
		mEmbSet = true;
		if(ProcessExecutor.runMavenCommand("-v", "/") == -1){
			DefaultLogger.getInstance().log("[Application Packager] - WARNING! Maven command empty - Maven embedded used instead.", 2);
			mEmb = !mEmb;	
		}
	    } catch (Exception e) {
		DefaultLogger.getInstance().log("[Application Packager] - WARNING! Maven command not found - Maven embedded used instead.", 2);
		mEmb = !mEmb;
	    }
	}
	return mEmb;
    }    
    
    public int getMavenLogLevel(String message) {

	Map<String, Integer> enumeration = new HashMap<String, Integer>();
	enumeration.put("DEBUG", MavenExecutionRequest.LOGGING_LEVEL_DEBUG);
	enumeration.put("INFO", MavenExecutionRequest.LOGGING_LEVEL_INFO);
	enumeration.put("WARN", MavenExecutionRequest.LOGGING_LEVEL_WARN);
	enumeration.put("ERROR", MavenExecutionRequest.LOGGING_LEVEL_ERROR);
	enumeration.put("FATAL", MavenExecutionRequest.LOGGING_LEVEL_FATAL);
	enumeration.put("DISABLED", MavenExecutionRequest.LOGGING_LEVEL_DISABLED);
	
	for ( String key : enumeration.keySet() ) {
	    if(message.contains(key))
	    	return enumeration.get(key);
	}
	return 4;
    }

    public int getLogLevel(){
    	String levelName = getString(ConfigProperties.LOG_LEVEL_KEY, ConfigProperties.LOG_LEVEL_DEFAULT);
		if ("DEBUG".equalsIgnoreCase(levelName)) {
		    return MavenExecutionRequest.LOGGING_LEVEL_DEBUG;
		} else if ("WARN".equalsIgnoreCase(levelName)) {
		    return MavenExecutionRequest.LOGGING_LEVEL_WARN;
		} else if ("ERROR".equalsIgnoreCase(levelName)) {
		    return MavenExecutionRequest.LOGGING_LEVEL_ERROR;
		} else if ("FATAL".equalsIgnoreCase(levelName)) {
		    return MavenExecutionRequest.LOGGING_LEVEL_FATAL;
		} else if ("DISABLED".equalsIgnoreCase(levelName)) {
		    return MavenExecutionRequest.LOGGING_LEVEL_DISABLED;
		} else if ("INFO".equalsIgnoreCase(levelName)) {
		    return MavenExecutionRequest.LOGGING_LEVEL_INFO;
		}
		System.err.println("Unable to get log level from enviroment using DEBUG level");
		return MavenExecutionRequest.LOGGING_LEVEL_DEBUG;
    }
}
