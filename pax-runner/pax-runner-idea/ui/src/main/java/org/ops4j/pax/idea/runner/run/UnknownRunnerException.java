/*
 * Copyright 2006 Niclas Hedhman.
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
package org.ops4j.pax.idea.runner.run;

import org.ops4j.pax.idea.runner.PaxRunnerException;

public class UnknownRunnerException extends PaxRunnerException
{

    private String m_platform;

    public UnknownRunnerException( String platform )
    {
        super( "Runner is unknown: " + platform );
        m_platform = platform;
    }

    public String getPlatform()
    {
        return m_platform;
    }
}
