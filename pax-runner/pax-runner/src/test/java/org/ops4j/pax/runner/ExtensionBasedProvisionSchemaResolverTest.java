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

import java.io.File;
import java.net.MalformedURLException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.io.FileUtils;

public class ExtensionBasedProvisionSchemaResolverTest
{

    private ProvisionSchemaResolver m_underTest;

    @Before
    public void setUp()
    {
        m_underTest = new ExtensionBasedProvisionSchemaResolver();
    }

    // if starts with scan- it must return the same string
    @Test
    public void resolveStartingWithScan()
    {
        assertEquals( "Resolved", "scan-x:any", m_underTest.resolve( "scan-x:any" ) );
    }

    @Test
    public void resolvePOMWithFileProtocol()
    {
        assertEquals( "Resolved", "scan-pom:file:pom.xml", m_underTest.resolve( "file:pom.xml" ) );
    }

    @Test
    public void resolvePOMWithMvnProtocol()
    {
        assertEquals( "Resolved", "scan-pom:mvn:group/artifact/version/pom",
                      m_underTest.resolve( "mvn:group/artifact/version/pom" )
        );
    }

    @Test
    public void resolvePOMWithAnyProtocol()
    {
        assertEquals( "Resolved", "scan-pom:http:pom.xml", m_underTest.resolve( "http:pom.xml" ) );
    }

    @Test
    public void resolvePOMWithoutProtocol()
        throws Exception
    {
        File file = FileUtils.getFileFromClasspath( "ebpsresolver/pom.xml" );
        assertEquals( "Resolved", "scan-pom:" + file.toURL().toExternalForm(),
                      m_underTest.resolve( file.getAbsolutePath() )
        );
    }

    @Test
    public void resolveJarWithFileProtocol()
    {
        assertEquals( "Resolved", "scan-bundle:file:x.jar", m_underTest.resolve( "file:x.jar" ) );
    }

    @Test
    public void resolveJarWithAnyProtocol()
    {
        assertEquals( "Resolved", "scan-bundle:http:x.jar", m_underTest.resolve( "http:x.jar" ) );
    }

    @Test
    public void resolveJarWithoutProtocol()
        throws Exception
    {
        File file = FileUtils.getFileFromClasspath( "ebpsresolver/x.jar" );
        assertEquals( "Resolved", "scan-bundle:" + file.toURL().toExternalForm(),
                      m_underTest.resolve( file.getAbsolutePath() )
        );
    }

    @Test
    public void resolveBundleWithFileProtocol()
    {
        assertEquals( "Resolved", "scan-bundle:file:x.bundle", m_underTest.resolve( "file:x.bundle" ) );
    }

    @Test
    public void resolveBundleWithAnyProtocol()
    {
        assertEquals( "Resolved", "scan-bundle:http:x.bundle", m_underTest.resolve( "http:x.bundle" ) );
    }

    @Test
    public void resolveBundleWithOptions()
    {
        assertEquals( "Resolved", "scan-bundle:file:x.jar@5@nostart", m_underTest.resolve( "file:x.jar@5@nostart" ) );
    }

    @Test
    public void resolveBundleWithoutProtocol()
        throws Exception
    {
        File file = FileUtils.getFileFromClasspath( "ebpsresolver/x.bundle" );
        assertEquals( "Resolved", "scan-bundle:" + file.toURL().toExternalForm(),
                      m_underTest.resolve( file.getAbsolutePath() )
        );
    }

    @Test
    public void resolveDirWithFileProtocol()
    {
        assertEquals( "Resolved", "scan-dir:file:x.zip", m_underTest.resolve( "file:x.zip" ) );
    }

    @Test
    public void resolveDirWithAnyProtocol()
    {
        assertEquals( "Resolved", "scan-dir:http:x.zip", m_underTest.resolve( "http:x.zip" ) );
    }

    @Test
    public void resolveDirWithFullFilterSeparator()
    {
        assertEquals( "Resolved", "scan-dir:x.zip!/", m_underTest.resolve( "x.zip!/" ) );
    }

    @Test
    public void resolveDirWithoutProtocol()
        throws Exception
    {
        File file = FileUtils.getFileFromClasspath( "ebpsresolver/x.zip" );
        assertEquals( "Resolved", "scan-dir:" + file.toURL().toExternalForm(),
                      m_underTest.resolve( file.getAbsolutePath() )
        );
    }

    @Test
    public void resolveAnyExtensionWithFileProtocol()
    {
        assertEquals( "Resolved", "scan-file:file:x.any", m_underTest.resolve( "file:x.any" ) );
    }

    @Test
    public void resolveAnyExtensionWithAnyProtocol()
    {
        assertEquals( "Resolved", "scan-file:http:x.any", m_underTest.resolve( "http:x.any" ) );
    }

    @Test
    public void resolveAnyExtensionWithoutProtocol()
        throws Exception
    {
        File file = FileUtils.getFileFromClasspath( "ebpsresolver/x.any" );
        assertEquals( "Resolved", "scan-file:" + file.toURL().toExternalForm(),
                      m_underTest.resolve( file.getAbsolutePath() )
        );
    }

    @Test
    public void resolveNoExtensionWithFileProtocol()
    {
        assertEquals( "Resolved", "scan-dir:file:x", m_underTest.resolve( "file:x" ) );
    }

    @Test
    public void resolveNoExtensionWithAnyProtocol()
    {
        assertEquals( "Resolved", "scan-dir:http:x", m_underTest.resolve( "http:x" ) );
    }

    @Test
    public void resolveNoExtensionWithoutProtocol()
        throws Exception
    {
        File file = FileUtils.getFileFromClasspath( "ebpsresolver/x" );
        assertEquals( "Resolved", "scan-dir:" + file.toURL().toExternalForm(),
                      m_underTest.resolve( file.getAbsolutePath() )
        );
    }

    @Test
    public void resolveSlashWithFileProtocol()
    {
        assertEquals( "Resolved", "scan-dir:file:x/", m_underTest.resolve( "file:x/" ) );
    }

    @Test
    public void resolveSlashWithAnyProtocol()
    {
        assertEquals( "Resolved", "scan-dir:http:x/", m_underTest.resolve( "http:x/" ) );
    }

    @Test
    public void resolveSlashWithoutProtocol()
        throws Exception
    {
        File file = FileUtils.getFileFromClasspath( "ebpsresolver/x/" );
        assertEquals( "Resolved", "scan-dir:" + file.toURL().toExternalForm(),
                      m_underTest.resolve( file.getAbsolutePath() )
        );
    }

    @Test
    public void resolveBackslashWithFileProtocol()
    {
        assertEquals( "Resolved", "scan-dir:file:x\\", m_underTest.resolve( "file:x\\" ) );
    }

    @Test
    public void resolveBackslashWithAnyProtocol()
    {
        assertEquals( "Resolved", "scan-dir:http:x\\", m_underTest.resolve( "http:x\\" ) );
    }

    @Test
    public void resolveBackSlashWithoutProtocol()
        throws Exception
    {
        File file = FileUtils.getFileFromClasspath( "ebpsresolver/x" + File.separator );
        assertEquals( "Resolved", "scan-dir:" + file.toURL().toExternalForm(),
                      m_underTest.resolve( file.getAbsolutePath() )
        );
    }

    @Test
    public void resolveNull()
    {
        assertNull( "Resolved expected to be null", m_underTest.resolve( null ) );
    }

    @Test
    public void resolveEmpty()
    {
        assertNull( "Resolved expected to be null", m_underTest.resolve( " " ) );
    }

    @Test
    public void resolveMvnWithoutVersion()
        throws MalformedURLException
    {
        assertEquals( "Resolved", "scan-bundle:mvn:org.ops4j/mine", m_underTest.resolve( "mvn:org.ops4j/mine" ) );
    }

    @Test
    public void resolveMvnWithVersion()
        throws MalformedURLException
    {
        assertEquals( "Resolved", "scan-bundle:mvn:org.ops4j/mine/0.2.0",
                      m_underTest.resolve( "mvn:org.ops4j/mine/0.2.0" )
        );
    }

    @Test
    public void resolveWrap()
        throws MalformedURLException
    {
        assertEquals( "Resolved", "scan-bundle:wrap:mvn:org.ops4j/mine",
                      m_underTest.resolve( "wrap:mvn:org.ops4j/mine" )
        );
    }

    @Test
    public void resolveWrapWithInstructions()
        throws MalformedURLException
    {
        assertEquals( "Resolved", "scan-bundle:wrap:mvn:org.ops4j/mine!instruction=value",
                      m_underTest.resolve( "wrap:mvn:org.ops4j/mine!instruction=value" )
        );
    }

}
