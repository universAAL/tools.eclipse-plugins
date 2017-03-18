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
import java.util.UUID;

import org.apache.maven.execution.MavenExecutionRequest;
import org.universaal.tools.packaging.tool.util.DefaultLogger;
import org.universaal.tools.packaging.tool.util.ProcessExecutor;

/**
 * A singleton for sharing the configuration among all the classes of the plugin
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class SystemPropertiesConfigurator {

    public Boolean mEmbSet = false;

    public static final SystemPropertiesConfigurator local = new SystemPropertiesConfigurator();

    private SystemPropertiesConfigurator() {

    }

    public String getRecoveryFileName() {
	return System.getProperty(ConfigProperties.RECOVERY_FILE_NAME_KEY,
		ConfigProperties.RECOVERY_FILE_NAME_DEFAULT);
    }

    public String getRecoveryPartsName() {
	return System.getProperty(ConfigProperties.RECOVERY_PARTS_NAME_KEY,
		ConfigProperties.RECOVERY_PARTS_NAME_DEFAULT);
    }

    public String getTempFolder() {
	String paths[] = new String[] {
		System.getProperty(ConfigProperties.TMP_DIR_KEY,
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
	File folder = getFolder(System.getProperty(
		ConfigProperties.LOG_DIR_KEY, ConfigProperties.LOG_DIR_DEFAULT));
	if (folder == null || folder.canWrite() == false) {
	    return null;
	}
	return folder;
    }

    public boolean isConsoleLog() {
	return Boolean.valueOf(System.getProperty(
		ConfigProperties.ENABLE_CONSOLE_LOG_KEY,
		ConfigProperties.ENABLE_CONSOLE_LOG_DEFAULT));
    }

    public String getKarafPluginGroupId() {
	return System.getProperty(ConfigProperties.KARAF_PLUGIN_GROUP_KEY,
		ConfigProperties.KARAF_PLUGIN_GROUP_DEFAULT);
    }

    public String getKarafPluginArtifactId() {
	return System.getProperty(ConfigProperties.KARAF_PLUGIN_NAME_KEY,
		ConfigProperties.KARAF_PLUGIN_NAME_DEFAULT);
    }

    public String getKarafPluginVersion() {
	return System.getProperty(ConfigProperties.KARAF_PLUGIN_VERSION_KEY,
		ConfigProperties.KARAF_PLUGIN_VERSION_DEFAULT);
    }

    public String getKarafPluginFeatureGoal() {
	return System.getProperty(
		ConfigProperties.KARAF_PLUGIN_GOAL_FEATURE_KEY,
		ConfigProperties.KARAF_PLUGIN_GOAL_FEATURE_DEFAULT);
    }

    public String getMavenCommand() {
	return System.getProperty(ConfigProperties.MAVEN_COMMAND_KEY,
		ConfigProperties.MAVEN_COMMAND_DEFAULT);
    }

    public Boolean isOfflineMode() {
	return Boolean.valueOf(System.getProperty(
		ConfigProperties.OFFLINE_MODE_KEY,
		ConfigProperties.OFFLINE_MODE_DEFAULT));
    }

    public boolean isPersistanceEnabled() {
	return Boolean.valueOf(System.getProperty(
		ConfigProperties.RECOVERY_MODE_KEY,
		ConfigProperties.RECOVERY_MODE_KEY_DEFAULT));
    }
    
    public String getKarafPluginKarGoal() {
	return System.getProperty(ConfigProperties.KARAF_PLUGIN_GOAL_KAR_KEY,
		ConfigProperties.KARAF_PLUGIN_GOAL_KAR_DEFAULT);
    }

    public Boolean runMavenEmbedded() {
	Boolean mEmb = Boolean.valueOf(System.getProperty(
		ConfigProperties.MAVEN_EMBEDDED_KEY,
		ConfigProperties.MAVEN_EMBEDDED_DEFAULT));
	if (!mEmb && !mEmbSet) {
	    try {
		mEmbSet = true;
		if(ProcessExecutor.runMavenCommand("-v", "/") == -1){
			DefaultLogger.getInstance().log("[Application Packager] - WARNING! Maven command empty - Maven embedded used instead.", 2);
			mEmb = !mEmb;	
		}
	    } catch (Exception e) {
		DefaultLogger.getInstance().log("[Application Packager] - WARNING! Maven command not found - Maven embedded used instead.", 1);
		mEmb = !mEmb;
	    }
	}
	return mEmb;
    }

    private int getLogLevel(final String levelName) {
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
	System.err
		.println("Unable to get log level from enviroment using DEBUG level");
	return MavenExecutionRequest.LOGGING_LEVEL_DEBUG;
    }

    public int getMavenLogLevel() {
	return getLogLevel(System.getProperty("org.uAAL.packager.loglevel",
		"DEBUG"));
    }

}
