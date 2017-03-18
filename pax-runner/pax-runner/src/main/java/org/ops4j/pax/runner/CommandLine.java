/*
 * Copyright 2007 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.runner;

import java.util.List;

/**
 * Abstracts accesss to command line arguments.
 *
 * @author Alin Dreghiciu
 * @since August 26, 2007
 */
public interface CommandLine
{

    /**
     * Configuration url option.
     */
    static final String OPTION_CONFIG = "config";
    /**
     * Debug option.
     */
    static final String OPTION_LOG = "log";
    /**
     * Handlers option.
     */
    static final String OPTION_HANDLERS = "handlers";
    /**
     * Scanners option.
     */
    static final String OPTION_SCANNERS = "scanners";
    /**
     * Platform option.
     */
    static final String OPTION_PLATFORM = "platform";
    /**
     * Executor option.
     */
    static final String OPTION_EXECUTOR = "executor";
    /**
     * Services option.
     */
    static final String OPTION_SERVICES = "services";
    /**
     * Platform version option.
     */
    static final String OPTION_PLATFORM_VERSION = "version";
    /**
     * Platform snapshot version option.
     */
    static final String OPTION_PLATFORM_VERSION_SNAPSHOT = "snapshot";
    /**
     * Platform snapshot version.
     */
    static final String PLATFORM_VERSION_SNAPSHOT = "SNAPSHOT";    
    /**
     * Boot classpath prepended option.
     */
    static final String OPTION_BOOT_CP_PREPEND = "bcp/p";
    /**
     * Boot classpath appended option.
     */
    static final String OPTION_BOOT_CP_APPEND = "bcp/a";
    /**
     * Profiles option.
     */
    static final String OPTION_PROFILES = "profiles";
    /**
     * Console option.
     */
    static final String OPTION_CONSOLE = "console";
    /**
     * Shell option.
     */
    static final String OPTION_SHELL = "shell";
    /**
     * Profiles repository.
     */
    static final String OPTION_PROFILES_REPO = "profilesRepositories";
    /**
     * Profiles group id.
     */
    static final String OPTION_PROFILES_GROUPID = "profilesGroupId";
    /**
     * Keep Original URLs option.
     */
    static final String OPTION_KEEP_ORIGINAL_URLS = "keepOriginalUrls";
    /**
     * Disable Logos option.
     */
    static final String OPTION_NOLOGO = "nologo";
    /**
     * Daemon Timeout option.
     */
    static final String OPTION_DAEMON_TIMEOUT = "daemonTimeout";

    /**
     * Returns the value of an option by key. If option is not defined returns null.
     *
     * @param key option key
     *
     * @return The option value.
     */
    String getOption( String key );

    /**
     * Returns the values of an array option (multiple values) by key. If option is not defined returns empty array.
     *
     * @param key option key
     *
     * @return The option values as array.
     */
    String[] getMultipleOption( String key );

    /**
     * Returns the list of all arguments.
     *
     * @return list of arguments; if there are no arrguments returns an empty list
     */
    List<String> getArguments();

}
