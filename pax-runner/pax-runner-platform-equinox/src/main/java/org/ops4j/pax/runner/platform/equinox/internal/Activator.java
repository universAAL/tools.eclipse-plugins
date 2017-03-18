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
package org.ops4j.pax.runner.platform.equinox.internal;

import org.osgi.framework.BundleContext;
import org.ops4j.pax.runner.platform.PlatformBuilder;
import org.ops4j.pax.runner.platform.builder.AbstractPlatformBuilderActivator;

/**
 * Bundle activator for equinox platform.
 *
 * @author Alin Dreghiciu
 * @see org.ops4j.pax.runner.platform.builder.AbstractPlatformBuilderActivator
 * @since August 20, 2007
 */
public final class Activator
    extends AbstractPlatformBuilderActivator
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected PlatformBuilder[] createPlatformBuilders( final BundleContext bundleContext )
    {
        return new PlatformBuilder[]{
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.2.1" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.3.0" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.3.1" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.3.2" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.4.0" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.4.1" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.4.2" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.5.0" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.5.1" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.6.0" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.6.1" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.6.2" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.7.0" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.7.1" ),
            new EquinoxPlatformBuilderF321T372( bundleContext, "3.7.2" ),
            new EquinoxPlatformBuilderF380( bundleContext, "3.8.0" ),
            new EquinoxPlatformBuilderF380( bundleContext, "3.8.1" ),
            new EquinoxPlatformBuilderF380( bundleContext, "3.8.2" ),
            new EquinoxPlatformBuilderF380( bundleContext, "3.9.1" ),
            new EquinoxPlatformBuilderSnapshot( bundleContext )
        };
    }

}

