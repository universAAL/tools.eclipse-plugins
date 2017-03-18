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
package org.ops4j.pax.runner.platform.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import static org.easymock.EasyMock.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.ops4j.io.FileUtils;
import org.ops4j.pax.runner.platform.BundleReference;
import org.ops4j.pax.runner.platform.BundleReferenceBean;
import org.ops4j.pax.runner.platform.Configuration;
import org.ops4j.pax.runner.platform.FilePathStrategy;
import org.ops4j.pax.runner.platform.JavaRunner;
import org.ops4j.pax.runner.platform.PlatformBuilder;
import org.ops4j.pax.runner.platform.PlatformContext;
import org.ops4j.pax.runner.platform.PlatformException;

public class PlatformImplTest
{

    private PlatformDefinition m_definition;
    private Configuration m_config;
    private String m_workDir;
    private PlatformBuilder m_builder;
    private PlatformContext m_context;
    private BundleContext m_bundleContext;
    private Bundle m_bundle;

    @Before
    public void setUp()
        throws IOException, PlatformException
    {
        m_builder = createMock( PlatformBuilder.class );
        m_definition = createMock( PlatformDefinition.class );
        m_config = createMock( Configuration.class );
        m_context = createMock( PlatformContext.class );
        m_bundleContext = createMock( BundleContext.class );
        m_bundle = createMock( Bundle.class );
        File workDir = File.createTempFile( "runner", "" );
        m_workDir = workDir.getAbsolutePath();
        workDir.delete();
        workDir = new File( m_workDir );
        workDir.mkdirs();
        workDir.deleteOnExit();
    }

    @After
    public void tearDown()
    {
        FileUtils.delete( new File( m_workDir ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void constructorWithNullPlatformBuilder()
        throws Exception
    {
        new PlatformImpl( null );
    }

    @Test
    public void startWithBundles()
        throws Exception
    {
        List<BundleReference> bundles = new ArrayList<BundleReference>();
        bundles.add( new BundleReferenceBean( FileUtils.getFileFromClasspath( "platform/bundle1.jar" ).toURL() ) );
        bundles.add( new BundleReferenceBean( FileUtils.getFileFromClasspath( "platform/bundle2.jar" ).toURL() ) );
        start( bundles );
    }

    // test that platform starts even without bundles to be installed
    @Test
    public void startWithoutBundles()
        throws Exception
    {
        start( null );
    }

    // expected to throw an exception the bundle is a plain file not a jar
    @Test( expected = PlatformException.class )
    public void startWithNotAJarBundle()
        throws Exception
    {
        List<BundleReference> bundles = new ArrayList<BundleReference>();
        bundles.add( new BundleReferenceBean( FileUtils.getFileFromClasspath( "platform/invalid.jar" ).toURL() ) );
        start( bundles );
    }

    // expected to throw an exception since bundle is jar that does not have a manifest entry for symbolic name
    @Test( expected = PlatformException.class )
    public void startWithAJarWithNoManifestAttr()
        throws Exception
    {
        List<BundleReference> bundles = new ArrayList<BundleReference>();
        bundles
            .add( new BundleReferenceBean( FileUtils.getFileFromClasspath( "platform/noManifestAttr.jar" ).toURL() ) );
        start( bundles );
    }

    // test that platform starts even if the system jar is not a bundle by itself
    @Test
    public void startWithoutBundlesAndASystemJarThatIsNotBunlde()
        throws Exception
    {
        start( null, FileUtils.getFileFromClasspath( "platform/noManifestAttr.jar" ).toURL() );
    }

    public void start( final List<BundleReference> bundles )
        throws Exception
    {
        start( bundles, FileUtils.getFileFromClasspath( "platform/system.jar" ).toURL() );
    }

    public void start( final List<BundleReference> bundles, URL systemBundleURL )
        throws Exception
    {
        final JavaRunner javaRunner = createMock( JavaRunner.class );
        javaRunner.exec( (String[]) notNull(), (String[]) notNull(), (String) notNull(), (String[]) notNull(),
                         (String) notNull(), (File) notNull(),(String[]) notNull()
        );
        final FilePathStrategy filePathStrategy = createMock( FilePathStrategy.class );

        expect( m_builder.getMainClassName() ).andReturn( "Main" );
        m_context.setProperties( null );
        m_context.setConfiguration( m_config );
        expect( m_config.getWorkingDirectory() ).andReturn( m_workDir );
        m_context.setWorkingDirectory( new File( m_workDir ) );
        expect( m_config.useAbsoluteFilePaths() ).andReturn( false );
        m_context.setFilePathStrategy( (FilePathStrategy) notNull() );
        expect( m_config.isOverwrite() ).andReturn( true );
        expect( m_config.isOverwriteUserBundles() ).andReturn( false );
        expect( m_config.isOverwriteSystemBundles() ).andReturn( false );
        expect( m_config.isDownloadFeedback() ).andReturn( false );
        expect( m_config.isAutoWrap() ).andReturn( false );
        expect( m_config.keepOriginalUrls() ).andReturn( false ).anyTimes();
        expect( m_config.getJavaHome() ).andReturn( "javaHome" );
        expect( m_definition.getSystemPackage() ).andReturn( systemBundleURL );
        expect( m_definition.getSystemPackageName() ).andReturn( "system package" );
        List<BundleReference> platformBundles = new ArrayList<BundleReference>();
        platformBundles.add(
            new BundleReferenceBean( FileUtils.getFileFromClasspath( "platform/platform.jar" ).toURL() )
        );
        // from downloadPlatformBundles()
        expect( m_context.getConfiguration() ).andReturn( m_config );
        expect( m_builder.getRequiredProfile( m_context ) ).andReturn( null );
        expect( m_definition.getPlatformBundles( "" ) ).andReturn( platformBundles );
        // from start()
        m_context.setBundles( (List<BundleReference>) notNull() );
        m_builder.prepare( m_context );
        expect( m_config.getProfiles() ).andReturn( null );
        expect( m_config.getExecutionEnvironment() ).andReturn( "NONE" );
        expect( m_config.getSystemPackages() ).andReturn( null );
        expect( m_config.getVMOptions() ).andReturn( new String[]{ "-Xmx512m", "-Xms128m" } );
        expect( m_config.validateBundles() ).andReturn( true ).anyTimes();
        expect( m_config.skipInvalidBundles() ).andReturn( false ).anyTimes();
        expect( m_context.getFilePathStrategy() ).andReturn( filePathStrategy );
        expect( filePathStrategy.normalizeAsPath( (File) notNull() ) ).andReturn( null );
        expect( ( m_definition.getPackages() ) ).andReturn( null );
        m_context.setSystemPackages( "" );
        m_context.setExecutionEnvironment( "" );
        expect( m_builder.getVMOptions( m_context ) ).andReturn( new String[]{ "-Dproperty=value" } );
        expect( m_config.getEnvOptions( ) ).andReturn( new String[]{ } ).times( 2 );
        expect( m_config.getClasspath() ).andReturn( "" );
        expect( m_builder.getArguments( m_context ) ).andReturn( new String[]{ "arg1" } );

        replay( m_builder, m_definition, m_config, m_context, m_bundleContext, m_bundle, javaRunner, filePathStrategy );
        new TestPlatform().start( null, bundles, null, null, javaRunner );
        verify( m_builder, m_definition, m_config, m_context, m_bundleContext, m_bundle, javaRunner, filePathStrategy );
    }

    @Test( expected = PlatformException.class )
    public void validateBundleWithNoManifestAndCheckAttributes()
        throws Exception
    {
        replay( m_builder, m_bundleContext, m_config );
        PlatformImpl platform = new PlatformImpl( m_builder );
        File file = FileUtils.getFileFromClasspath( "platform/withoutManifest.jar" );
        URL url = file.toURL();
        platform.validateBundle( url, file );
    }

    @Test
    public void determineCachingNameWithNoManifestAttributes()
        throws Exception
    {
        replay( m_builder, m_bundleContext, m_config );
        PlatformImpl platform = new PlatformImpl( m_builder );
        File file = FileUtils.getFileFromClasspath( "platform/noManifestAttr.jar" );
        assertEquals(
            "defaultBSN_0.0.0.jar",
            platform.determineCachingName( file, "defaultBSN" )
        );
        verify( m_builder, m_bundleContext, m_config );
    }

    @Test( expected = PlatformException.class )
    public void validateBundleWithInvalidFile()
        throws Exception
    {
        replay( m_builder, m_bundleContext, m_config );
        PlatformImpl platform = new PlatformImpl( m_builder );
        File file = FileUtils.getFileFromClasspath( "platform/invalid.jar" );
        URL url = file.toURL();
        platform.validateBundle( url, file );
    }

    @Test
    public void determineCacheNameWithValidManifestWithoutVersion()
        throws Exception
    {
        replay( m_builder, m_bundleContext, m_config );
        PlatformImpl platform = new PlatformImpl( m_builder );
        File file = FileUtils.getFileFromClasspath( "platform/bundle1.jar" );
        assertEquals(
            "bundle2_0.0.0.jar",
            platform.determineCachingName( file, "defaultBSN" )
        );
        verify( m_builder, m_bundleContext, m_config );
    }

    @Test
    public void determineCacheNameWithValidManifestWithVersion()
        throws Exception
    {
        replay( m_builder, m_bundleContext, m_config );
        PlatformImpl platform = new PlatformImpl( m_builder );
        File file = FileUtils.getFileFromClasspath( "platform/bundleWithVersion.jar" );
        assertEquals(
            "bundle2_1.2.3.jar",
            platform.determineCachingName( file, "defaultBSN" )
        );
        verify( m_builder, m_bundleContext, m_config );
    }

    @Test
    public void determineCacheNameWithBDNWithSemicolon()
        throws Exception
    {
        replay( m_builder, m_bundleContext, m_config );
        PlatformImpl platform = new PlatformImpl( m_builder );
        File file = FileUtils.getFileFromClasspath( "platform/bundleWithSemicolon.jar" );
        assertEquals(
            "bundleWithSemicolon_0.0.0.jar",
            platform.determineCachingName( file, "defaultBSN" )
        );
        verify( m_builder, m_bundleContext, m_config );
    }

    private class TestPlatform extends PlatformImpl
    {

        TestPlatform()
            throws PlatformException
        {
            super( m_builder );
        }

        @Override
        PlatformDefinition createPlatformDefinition( final Configuration configuration )
            throws PlatformException
        {
            return m_definition;
        }

        @Override
        Configuration createConfiguration( final Dictionary config )
        {
            return m_config;
        }

        @Override
        PlatformContext createPlatformContext()
        {
            return m_context;
        }

    }

}
