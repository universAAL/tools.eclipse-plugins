/*
 * Copyright 2009 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.runner.platform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.runner.commons.Info;
import org.ops4j.pax.runner.handler.internal.URLUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * In process Java Runner. No external process will be started.
 *
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 * @since 0.20.0, May 10, 2009
 */
public class InProcessJavaRunner
    implements JavaRunner
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( InProcessJavaRunner.class );

    /**
     * Framework active.
     */
    private boolean m_frameworkActive;

    /**
     * Constructor.
     */
    public InProcessJavaRunner()
    {
        m_frameworkActive = false;
    }

    public void exec( String[] vmOptions, String[] classpath, String mainClass, String[] programOptions, String javaHome, File workingDir, String[] environmentVariables )
        throws PlatformException
    {
        if (environmentVariables == null || environmentVariables.length == 0) {
            exec( vmOptions,classpath,mainClass,programOptions,javaHome,workingDir );
        }else {
            throw new PlatformException( "Rethink what you are doing: trying to change process environment (setting env variables) and use Pax Runner with inProcess?" );
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void exec( final String[] vmOptions,
                                   final String[] classpath,
                                   final String mainClass,
                                   final String[] programOptions,
                                   final String javaHome,
                                   final File workingDirectory )
        throws PlatformException
    {
        if( m_frameworkActive )
        {
            throw new PlatformException( "Platform already started" );
        }
        m_frameworkActive = true;

        final URLClassLoader classLoader = createClassLoader( classpath );
        final Properties systemProps = extractSystemProperties( vmOptions );

        final ClassLoader tcclBackup = Thread.currentThread().getContextClassLoader();
        final Properties systemPropsBackup = System.getProperties();
        final URLStreamHandlerFactory handlerFactoryBackup = URLUtils.resetURLStreamHandlerFactory();
        try
        {
            Thread.currentThread().setContextClassLoader( classLoader );
            System.setProperties( systemProps );

            final Class<?> clazz = classLoader.loadClass( mainClass );
            final Method mainMethod = clazz.getMethod( "main", String[].class );

            LOG.info( "Runner has successfully finished his job!" );
            Info.println(); // print an empty line

            mainMethod.invoke( null, new Object[]{ programOptions } );
        }
        catch( ClassNotFoundException e )
        {
            throw new PlatformException( "Cannot find target framework main class", e );
        }
        catch( NoSuchMethodException e )
        {
            throw new PlatformException( "Cannot find target framework main method", e );
        }
        catch( IllegalAccessException e )
        {
            throw new PlatformException( "Cannot run the target framework", e );
        }
        catch( InvocationTargetException e )
        {
            throw new PlatformException( "Cannot run the target framework", e );
        }
        finally
        {
            URLUtils.setURLStreamHandlerFactory( handlerFactoryBackup );
            System.setProperties( systemPropsBackup );
            Thread.currentThread().setContextClassLoader( tcclBackup );
        }
    }

    /**
     * Creates the clasloader to usefor loading the framework main class.
     *
     * @param classpath classpath entries
     *
     * @return classloader
     *
     * @throws PlatformException if classpath entries cannot be converted to urls
     */
    private static URLClassLoader createClassLoader( final String[] classpath )
        throws PlatformException
    {
        final List<URL> classpathUrls = new ArrayList<URL>();
        if( classpath != null )
        {
            for( String path : classpath )
            {
                try
                {
                    classpathUrls.add( new File( path ).toURL() );
                }
                catch( MalformedURLException e )
                {
                    throw new PlatformException( "Cannot setup target framework classpath", e );
                }
            }
        }
        return new URLClassLoader(
            classpathUrls.toArray( new URL[classpathUrls.size()] ),
            InProcessJavaRunner.class.getClassLoader().getParent()
        );
    }

    /**
     * Extract from provided virtual machine options the system properties = vm option sthat start with -D.
     *
     * @param vmOptions virtual machine options
     *
     * @return current system properties + configured system properties
     */
    private static Properties extractSystemProperties( final String[] vmOptions )
    {
        final Properties systemProps = new Properties();
        systemProps.putAll( System.getProperties() );
        for( String vmOption : vmOptions )
        {
            if( vmOption.startsWith( "-D" ) && vmOption.length() > 3 && vmOption.contains( "=" ) )
            {
                String[] segments = vmOption.substring( 2 ).split( "=" );
                systemProps.setProperty( segments[ 0 ], segments[ 1 ] );
            }
            else
            {
                LOG.warn( "VM option [" + vmOption + "] cannot be used" );
            }
        }
        return systemProps;
    }

}