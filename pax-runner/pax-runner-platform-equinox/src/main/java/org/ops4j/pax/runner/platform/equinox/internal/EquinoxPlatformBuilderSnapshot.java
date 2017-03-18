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
package org.ops4j.pax.runner.platform.equinox.internal;

import org.ops4j.net.URLUtils;
import org.ops4j.pax.runner.platform.Configuration;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Platform builder for the snapshot (nightly build) Equinox.
 *
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 * @since 0.18.0, March 05, 2009
 */
public class EquinoxPlatformBuilderSnapshot
    extends EquinoxPlatformBuilderF380
{

    /**
     * Create a new equinux platform builder.
     *
     * @param bundleContext a bundle context
     */
    public EquinoxPlatformBuilderSnapshot( final BundleContext bundleContext )
    {
        super( bundleContext, "SNAPSHOT" );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getDefinition( final Configuration configuration )
        throws IOException
    {
        return URLUtils.prepareInputStream(
            new URL( configuration.getProperty( "equinox.snapshot.definition.url" ) ),
            true
        );
    }

}
