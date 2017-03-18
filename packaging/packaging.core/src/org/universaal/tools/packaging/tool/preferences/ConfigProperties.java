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

/**
 * This class contains all configuration parameters and their default values,
 * which change the behavior of the plugin
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public interface ConfigProperties {

    public static final String TMP_DIR_KEY = "org.uAAL.packager.log.dir";
    public static final String TMP_DIR_DEFAULT = null;

    public static final String LOG_DIR_KEY = "org.uAAL.packager.log.dir";
    public static final String LOG_DIR_DEFAULT = TMP_DIR_DEFAULT;

    public static final String RECOVERY_PARTS_NAME_KEY = "org.uAAL.packager.recovery.partsname";
    public static final String RECOVERY_PARTS_NAME_DEFAULT = "/.parts";
    public static final String RECOVERY_FILE_NAME_KEY = "org.uAAL.packager.recovery.filename";
    public static final String RECOVERY_FILE_NAME_DEFAULT = "/.recovery";
    
    /**
     * This is a boolean property for enabling/disabling the data persistence.<br>
     * By the default data persistence is {@value #RECOVERY_MODE_KEY_DEFAULT}
     */
    public static final String RECOVERY_MODE_KEY = "org.uAAL.packager.recovery";
    public static final String RECOVERY_MODE_KEY_DEFAULT = "true";
    
    /**
     * This is a boolean property for enabling/disabling the console output.<br>
     * By the default console debug is {@value #ENABLE_CONSOLE_LOG_DEFAULT}
     */
    public static final String ENABLE_CONSOLE_LOG_KEY = "org.uAAL.packager.log.console";
    public static final String ENABLE_CONSOLE_LOG_DEFAULT = "true";

    /**
     * This is String property representing the group name of the Karaf's Maven
     * plugin to use<br>
     * The default value is {@value #KARAF_PLUGIN_GROUP_DEFAULT}
     */
    public static final String KARAF_PLUGIN_GROUP_KEY = "karaf.tool.groupId";
    public static final String KARAF_PLUGIN_GROUP_DEFAULT = "org.apache.karaf.tooling";

    /**
     * This is String property representing the name of the Karaf's Maven plugin
     * to use<br>
     * The default value is {@value #KARAF_PLUGIN_NAME_DEFAULT}
     */
    public static final String KARAF_PLUGIN_NAME_KEY = "karaf.tool.artifactId";
    public static final String KARAF_PLUGIN_NAME_DEFAULT = "features-maven-plugin";

    /**
     * This is String property representing the version of the Karaf's Maven
     * plugin to use<br>
     * The default value is {@value #KARAF_PLUGIN_VERSION_DEFAULT}
     */
    public static final String KARAF_PLUGIN_VERSION_KEY = "karaf.tool.version";
    public static final String KARAF_PLUGIN_VERSION_DEFAULT = "2.3.1";

    /**
     * This is String property representing the name of the Maven goal to use
     * for generating the Karaf's feature file.<br>
     * The default value is {@value #KARAF_PLUGIN_GOAL_FEATURE_DEFAULT}
     */
    public static final String KARAF_PLUGIN_GOAL_FEATURE_KEY = "karaf.tool.goal.feature";
    public static final String KARAF_PLUGIN_GOAL_FEATURE_DEFAULT = "generate-features-xml";

    /**
     * This is String property representing the name of the Maven goal to use
     * for generating the Karaf's KAR file.<br>
     * The default value is {@value #KARAF_PLUGIN_GOAL_KAR_DEFAULT}
     */
    public static final String KARAF_PLUGIN_GOAL_KAR_KEY = "karaf.tool.goal.karfile";
    public static final String KARAF_PLUGIN_GOAL_KAR_DEFAULT = "create-kar";

    /**
     * This is a boolean property for avoid Maven to access to Internet<br>
     * By the default console debug is {@value #OFFLINE_MODE_DEFAULT}
     */
    public static final String OFFLINE_MODE_KEY = "org.uAAL.packager.offline";
    public static final String OFFLINE_MODE_DEFAULT = "true";

    /**
     * This is a String property representing the command (i.e. the absolute
     * path) to use for running Maven<br>
     * <b>NOTE:</b>This command will be used only if {@link #MAVEN_EMBEDDED_KEY}
     * is set to <code>false</code><br>
     * By the default console debug is {@value #MAVEN_COMMAND_DEFAULT}
     */
    public static final String MAVEN_COMMAND_KEY = "org.uAAL.packager.maven.cmd";
    public static final String MAVEN_COMMAND_DEFAULT = "";

    /**
     * This is a boolean property for avoid to use the Eclipse embedded Maven<br>
     * By the default console debug is {@value #MAVEN_EMBEDDED_DEFAULT}
     */
    public static final String MAVEN_EMBEDDED_KEY = "org.uAAL.packager.maven.embedded";
    public static final String MAVEN_EMBEDDED_DEFAULT = "true";
    
    /**
     * This is a String property representing the log level used by App Packager (including Maven<br>
     * <b>NOTE:</b>Acceptable values are info, debug, warn, error, fatal, and disabled<br>
     * By the default console debug is {@value #LOG_LEVEL_DEFAULT}
     */
    public static final String LOG_LEVEL_KEY = "org.uAAL.packager.loglevel";
    public static final String LOG_LEVEL_DEFAULT = "debug";

}
