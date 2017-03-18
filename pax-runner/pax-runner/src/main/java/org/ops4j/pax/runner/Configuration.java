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

/**
 * Abstracts accesss to runner configuration.
 *
 * @author Alin Dreghiciu
 * @since August 26, 2007
 */
public interface Configuration
{

    /**
     * Returns a property from configuration by key.
     *
     * @param key key of the property
     *
     * @return value of property
     */
    String getProperty( String key );

    /**
     * Returns all properties those name match the regexp.
     *
     * @param regex regular expresion to match
     *
     * @return all properties that match.
     */
    String[] getPropertyNames( String regex );

}
