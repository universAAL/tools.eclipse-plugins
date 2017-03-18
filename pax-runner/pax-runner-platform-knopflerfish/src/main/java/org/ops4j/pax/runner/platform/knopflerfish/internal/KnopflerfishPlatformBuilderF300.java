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
package org.ops4j.pax.runner.platform.knopflerfish.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.runner.platform.BundleReference;
import org.ops4j.pax.runner.platform.Configuration;
import org.ops4j.pax.runner.platform.PlatformBuilder;
import org.ops4j.pax.runner.platform.PlatformContext;
import org.ops4j.pax.runner.platform.PlatformException;
import org.ops4j.util.collections.PropertiesWriter;

/**
 * Platform builder for knopflerfish platform after version 3.0.0.
 *
 * @author Alin Dreghiciu
 * @since 1.2.0, July 20, 2009
 */
public class KnopflerfishPlatformBuilderF300
    implements PlatformBuilder
{

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog( KnopflerfishPlatformBuilderF300.class );
    /**
     * Provider name to be used in registration.
     */
    private static final String PROVIDER_NAME = "knopflerfish";
    /**
     * Name of the main class from Knopflerfish.
     */
    private static final String MAIN_CLASS_NAME = "org.knopflerfish.framework.Main";
    /**
     * The directory name where the configuration will be stored.
     */
    private static final String CONFIG_DIRECTORY = "knopflerfish";
    /**
     * Configuration file name.
     */
    private static final String CONFIG_INI = "config.ini";
    /**
     * Caching directory.
     */
    private static final String CACHE_DIRECTORY = "fwdir";
    /**
     * Profile name to be used when console should be started.
     */
    private static final String CONSOLE_PROFILE = "tui";
    /**
     * Separator for properties (bundles)
     */
    private static final String SEPARATOR = " ";
    /**
     * Current bundle context.
     */
    private final BundleContext m_bundleContext;
    /**
     * Supported version.
     */
    private final String m_version;

    /**
     * Create a new equinux platform builder.
     *
     * @param bundleContext a bundle context
     * @param version       supported version
     */
    public KnopflerfishPlatformBuilderF300( final BundleContext bundleContext, final String version )
    {
        NullArgumentException.validateNotNull( bundleContext, "Bundle context" );
        NullArgumentException.validateNotNull( version, "Version" );
        m_bundleContext = bundleContext;
        m_version = version;
    }

    /**
     * Creates a config.ini file under the working directory/knopflerfish directory.
     *
     * @see org.ops4j.pax.runner.platform.PlatformBuilder
     *      #prepare(org.ops4j.pax.runner.platform.PlatformContext)
     */
    public void prepare( final PlatformContext context )
        throws PlatformException
    {
        NullArgumentException.validateNotNull( context, "Platform context" );
        final List<BundleReference> bundles = context.getBundles();
        OutputStream os = null;
        try
        {
            final File workingDirectory = context.getWorkingDirectory();
            // make sure the configuration directory exists
            final File configDirectory = new File( workingDirectory, CONFIG_DIRECTORY );
            configDirectory.mkdirs();

            // create the configuration file
            final File configFile = new File( configDirectory, CONFIG_INI );
            configFile.createNewFile();
            LOGGER.debug( "Create knopflerfish configuration ini file [" + configFile + "]" );
            final Configuration configuration = context.getConfiguration();

            os = new FileOutputStream( configFile );
            final PropertiesWriter writer = new PropertiesWriter( os, SEPARATOR );

            writeHeader( writer );

            writer.append( "#############################" );
            writer.append( " Knopflerfish settings" );
            writer.append( "#############################" );

            // varios settings
            {
                writer
                    .append( "-Dorg.osgi.provisioning.spid", "knopflerfish" )
                    .append( "-Dorg.knopflerfish.verbosity", "0" )
                    .append( "-Doscar.repository.url", "http://www.knopflerfish.org/repo/repository.xml" )
                    .append( "-Dorg.knopflerfish.framework.usingwrapperscript", "false" )
                    .append( "-Dorg.knopflerfish.framework.exitonshutdown", "true" )
                    .append( "-Dorg.knopflerfish.framework.debug.packages", "false" )
                    .append( "-Dorg.knopflerfish.framework.debug.errors", "true" )
                    .append( "-Dorg.knopflerfish.framework.debug.classloader", "false" )
                    .append( "-Dorg.knopflerfish.framework.debug.startlevel", "false" )
                    .append( "-Dorg.knopflerfish.framework.debug.ldap", "false" )
                    .append( "-Dorg.knopflerfish.startlevel.use", "true" );
            }
            // storage directory
            {
                final File cacheDirectory = new File( configDirectory, CACHE_DIRECTORY );
                writer.append(
                    "-Dorg.osgi.framework.storage",
                    context.getFilePathStrategy().normalizeAsPath( cacheDirectory ).replace( File.separatorChar, '/' )
                );
            }
            // framework start level
            {
                final Integer startLevel = configuration.getStartLevel();
                if( startLevel != null )
                {
                    writer.append( "-Dorg.osgi.framework.startlevel.beginning", startLevel.toString() );
                }
            }
            // use persisted state
            {
                final Boolean usePersistedState = configuration.usePersistedState();
                if( usePersistedState != null && !usePersistedState )
                {
                    writer.append( "-Dorg.osgi.framework.storage.clean", "onFirstInit" );
                }
            }
            // execution environments
            {
                writer.append( "-D" + Constants.FRAMEWORK_EXECUTIONENVIRONMENT, context.getExecutionEnvironment() );
            }
            // boot delegation packages
            {
                final String bootDelegation = context.getConfiguration().getBootDelegation();
                if( bootDelegation != null )
                {
                    writer.append( "-D" + Constants.FRAMEWORK_BOOTDELEGATION, bootDelegation );
                }
            }
            // system packages
            {
                writer.append( "-Dorg.knopflerfish.framework.system.packages.base", context.getSystemPackages() );
            }
            // framework start level
            {
                final Integer startLevel = configuration.getStartLevel();
                if( startLevel != null )
                {
                    writer.append();
                    writer.append( " Framework start level" );
                    writer.appendRaw( "-startlevel " + startLevel.toString() );
                }
            }
            // provisioned bundles
            {
                if( bundles != null && bundles.size() > 0 )
                {
                    writer.append();
                    writer.append( "#############################" );
                    writer.append( " Client bundles to install" );
                    writer.append( "#############################" );
                    appendBundles( writer, bundles, context, configuration.getBundleStartLevel() );
                }
            }
            // user system properties
            {
                writer.append();
                writer.append( "#############################" );
                writer.append( " System properties" );
                writer.append( "#############################" );
                appendProperties( writer, context.getProperties() );
            }
            // bundle start level
            {
                final Integer bundleStartLevel = configuration.getBundleStartLevel();
                if( bundleStartLevel != null )
                {
                    writer.append();
                    writer.append( " Initial bundles start level" );
                    writer.appendRaw( "-initlevel " + bundleStartLevel.toString() );
                }
            }

            writer.write();
        }
        catch( IOException e )
        {
            throw new PlatformException( "Could not create knopflerfish configuration file", e );
        }
        finally
        {
            if( os != null )
            {
                try
                {
                    os.close();
                }
                catch( IOException e )
                {
                    throw new PlatformException( "Could not create knopflerfish configuration file", e );
                }
            }
        }

    }

    /**
     * Writes properties to configuration file.
     *
     * @param writer     a property writer
     * @param properties properties to be written; can be null
     */
    private void appendProperties( final PropertiesWriter writer, final Properties properties )
    {
        if( properties != null )
        {
            final Enumeration enumeration = properties.propertyNames();
            while( enumeration.hasMoreElements() )
            {
                final String key = (String) enumeration.nextElement();
                writer.append( "-D" + key, properties.getProperty( key ) );
            }
        }
    }

    /**
     * Writes bundles to configuration file.
     *
     * @param writer            a property writer
     * @param bundles           bundles to write
     * @param context           platform context
     * @param defaultStartlevel default start level for bundles. used if no start level is set on bundles
     *
     * @throws java.net.MalformedURLException re-thrown from getting the file url
     * @throws org.ops4j.pax.runner.platform.PlatformException
     *                                        if one of the bundles does not have a file
     */
    private void appendBundles( final PropertiesWriter writer,
                                final List<BundleReference> bundles,
                                final PlatformContext context,
                                final Integer defaultStartlevel )
        throws MalformedURLException, PlatformException
    {
        final Map<Integer, List<String>> bundlesMap = new TreeMap<Integer, List<String>>();
        for( BundleReference reference : bundles )
        {
            URL url = reference.getURL();
            if( url == null )
            {
                throw new PlatformException( "URL of the bundle to install cannot be null" );
            }

            String propertyName;

            final Boolean shouldStart = reference.shouldStart();
            if( shouldStart != null && shouldStart )
            {
                propertyName = "-istart ";
            }
            else
            {
                propertyName = "-install ";
            }

            Integer startLevel = reference.getStartLevel();
            if( startLevel == null )
            {
                startLevel = defaultStartlevel;
            }
            List<String> bundlesList = bundlesMap.get( startLevel );
            if( bundlesList == null )
            {
                bundlesList = new ArrayList<String>();
                bundlesMap.put( startLevel, bundlesList );
            }
            bundlesList.add( propertyName + context.getFilePathStrategy().normalizeAsUrl( url ) );
        }
        for( Map.Entry<Integer, List<String>> entry : bundlesMap.entrySet() )
        {
            writer.appendRaw( "-initlevel " + entry.getKey() );
            for( String reference : entry.getValue() )
            {
                writer.appendRaw( reference );
            }
        }
    }

    /**
     * Writes OPS4j header.
     *
     * @param writer a property writer
     */
    private void writeHeader( final PropertiesWriter writer )
    {
        writer.append( "###############################################" );
        writer.append( "              ______  ________  __  __        #" );
        writer.append( "             / __  / /  __   / / / / /        #" );
        writer.append( "            /  ___/ /  __   / _\\ \\ _/         #" );
        writer.append( "           /  /    /  / /  / / _\\ \\           #" );
        writer.append( "          /__/    /__/ /__/ /_/ /_/           #" );
        writer.append( "                                              #" );
        writer.append( " Pax Runner from OPS4J - http://www.ops4j.org #" );
        writer.append( "###############################################" );
        writer.append();
    }

    /**
     * @see org.ops4j.pax.runner.platform.PlatformBuilder#getMainClassName()
     */
    public String getMainClassName()
    {
        return MAIN_CLASS_NAME;
    }

    /**
     * @see org.ops4j.pax.runner.platform.PlatformBuilder
     *      #getArguments(org.ops4j.pax.runner.platform.PlatformContext)
     */
    public String[] getArguments( final PlatformContext context )
    {
        NullArgumentException.validateNotNull( context, "Platform context" );
        return new String[]{
            "-xargs",
            context.getFilePathStrategy().normalizeAsUrl(
                new File( new File( context.getWorkingDirectory(), CONFIG_DIRECTORY ), CONFIG_INI )
            )
        };
    }

    /**
     * @see org.ops4j.pax.runner.platform.PlatformBuilder#getVMOptions(org.ops4j.pax.runner.platform.PlatformContext)
     */
    public String[] getVMOptions( final PlatformContext context )
    {
        NullArgumentException.validateNotNull( context, "Platform context" );

        final Collection<String> vmOptions = new ArrayList<String>();
        final File workingDirectory = context.getWorkingDirectory();
        vmOptions.add(
            "-Dorg.osgi.framework.dir="
            + context.getFilePathStrategy().normalizeAsPath(
                new File( new File( workingDirectory, CONFIG_DIRECTORY ), CACHE_DIRECTORY )
            )
        );
        return vmOptions.toArray( new String[vmOptions.size()] );
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getDefinition( final Configuration configuration )
        throws IOException
    {
        final String definitionFile = "META-INF/platform-knopflerfish/definition-" + m_version + ".xml";
        final URL url = m_bundleContext.getBundle().getResource( definitionFile );
        if( url == null )
        {
            throw new FileNotFoundException( definitionFile + " could not be found" );
        }
        return url.openStream();
    }

    /**
     * If the console option is set then it will return the tui profile otherwise will return null.
     *
     * @see org.ops4j.pax.runner.platform.PlatformBuilder
     *      #getRequiredProfile(org.ops4j.pax.runner.platform.PlatformContext)
     */
    public String getRequiredProfile( final PlatformContext context )
    {
        final Boolean console = context.getConfiguration().startConsole();
        if( console == null || !console )
        {
            return null;
        }
        else
        {
            return CONSOLE_PROFILE;
        }
    }

    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return "Knopflerfish " + m_version;
    }

    /**
     * @see org.ops4j.pax.runner.platform.PlatformBuilder#getProviderName()
     */
    public String getProviderName()
    {
        return PROVIDER_NAME;
    }

    /**
     * @see org.ops4j.pax.runner.platform.PlatformBuilder#getProviderVersion()
     */
    public String getProviderVersion()
    {
        return m_version;
    }

}