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
package org.ops4j.pax.runner.platform.felix.internal;

import org.osgi.framework.BundleContext;
import org.ops4j.pax.runner.platform.PlatformBuilder;
import org.ops4j.pax.runner.platform.builder.AbstractPlatformBuilderActivator;

/**
 * Bundle activator for felix platform.
 *
 * @author Alin Dreghiciu
 * @see org.ops4j.pax.runner.platform.builder.AbstractPlatformBuilderActivator
 * @since September 01, 2007
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
            new FelixPlatformBuilderF100T122( bundleContext, "1.0.0" ),
            new FelixPlatformBuilderF100T122( bundleContext, "1.0.1" ),
            new FelixPlatformBuilderF100T122( bundleContext, "1.0.3" ),
            new FelixPlatformBuilderF100T122( bundleContext, "1.0.4" ),
            new FelixPlatformBuilderF100T122( bundleContext, "1.2.0" ),
            new FelixPlatformBuilderF100T122( bundleContext, "1.2.1" ),
            new FelixPlatformBuilderF100T122( bundleContext, "1.2.2" ),
            new FelixPlatformBuilderF140T141( bundleContext, "1.4.0" ),
            new FelixPlatformBuilderF140T141( bundleContext, "1.4.1" ),
            new FelixPlatformBuilderF160( bundleContext, "1.6.0" ),
            new FelixPlatformBuilderF160( bundleContext, "1.6.1" ),
            new FelixPlatformBuilderF160( bundleContext, "1.8.0" ),
            new FelixPlatformBuilderF160( bundleContext, "1.8.1" ),
            new FelixPlatformBuilderF160( bundleContext, "2.0.0" ),
            new FelixPlatformBuilderF160( bundleContext, "2.0.1" ),
            new FelixPlatformBuilderF160( bundleContext, "2.0.2" ),
            new FelixPlatformBuilderF160( bundleContext, "2.0.3" ),
            new FelixPlatformBuilderF160( bundleContext, "2.0.4" ),
            new FelixPlatformBuilderF160( bundleContext, "2.0.5" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.0" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.1" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.2" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.3" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.4" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.5" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.6" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.7" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.8" ),
            new FelixPlatformBuilderF160( bundleContext, "3.0.9" ),
            new FelixPlatformBuilderF160( bundleContext, "3.2.0" ),
            new FelixPlatformBuilderF160( bundleContext, "3.2.1" ),
            new FelixPlatformBuilderF160( bundleContext, "3.2.2" ),
            new FelixPlatformBuilderF160( bundleContext, "4.0.0" ),
            new FelixPlatformBuilderF160( bundleContext, "4.0.1" ),
            new FelixPlatformBuilderF160( bundleContext, "4.0.2" ),
            new FelixPlatformBuilderF160( bundleContext, "4.0.3" ),
            new FelixPlatformBuilderF160( bundleContext, "4.2.0" ),
            new FelixPlatformBuilderF160( bundleContext, "4.2.1" ),
            new FelixPlatformBuilderF160( bundleContext, "4.4.0" ),
            new FelixPlatformBuilderF160( bundleContext, "4.4.1" ),
            new FelixPlatformBuilderF160( bundleContext, "4.6.0" ),
            new FelixPlatformBuilderF160( bundleContext, "4.6.1" ),
            new FelixPlatformBuilderF160( bundleContext, "5.0.0" ),
            new FelixPlatformBuilderF160( bundleContext, "5.0.1" ),
            new FelixPlatformBuilderF160( bundleContext, "5.2.0" ),
            new FelixPlatformBuilderF160( bundleContext, "5.4.0" ),
            new FelixPlatformBuilderSnapshot( bundleContext )
        };
    }

}

