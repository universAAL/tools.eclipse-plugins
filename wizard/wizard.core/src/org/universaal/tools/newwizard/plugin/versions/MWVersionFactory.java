/*
	Copyright 2012-2014 ITACA-TSB, http://www.tsb.upv.es
	Instituto Tecnologico de Aplicaciones de Comunicacion 
	Avanzadas - Grupo Tecnologias para la Salud y el 
	Bienestar (TSB)
	
	See the NOTICE file distributed with this work for additional 
	information regarding copyright ownership
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	  http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package org.universaal.tools.newwizard.plugin.versions;

public class MWVersionFactory {
    
    /**
     * Returns an implementation of IMWversion for the given version.
     * 
     * @param versionIndex
     *            The version ID of the MW, as represented by IMWVersion
     *            constants.
     * @return The implementation of the right IMWVersion.
     */
    public static IMWVersion getMWVersion(int versionIndex){
	switch (versionIndex) {
	case IMWVersion.VER_030:
	    return new MWVersion030();
	case IMWVersion.VER_100:
	    return new MWVersion100();
	case IMWVersion.VER_110:
	    return new MWVersion110();
	case IMWVersion.VER_120:
	    return new MWVersion120();
	case IMWVersion.VER_130:
	    return new MWVersion130();
	case IMWVersion.VER_200:
	    return new MWVersion200();
	case IMWVersion.VER_300:
	    return new MWVersion300();
	case IMWVersion.VER_310:
	    return new MWVersion310();
	case IMWVersion.VER_320:
	    return new MWVersion320();
	case IMWVersion.VER_330:
	    return new MWVersion330();
	case IMWVersion.VER_340:
	    return new MWVersion340();
	default:
	    return new MWVersion340();
	}
    }
    
    /**
     * Gets the name of the MW version to display.
     * 
     * @param version
     *            MW Version number, as in IMWversion.
     * @return The String with the name.
     */
    public static String getVERname(int version){
	switch (version) {
	case IMWVersion.VER_030:
	    return "0.3.0-SNAPSHOT";
	case IMWVersion.VER_100:
	    return "1.0.0";
	case IMWVersion.VER_110:
	    return "1.1.0";
	case IMWVersion.VER_120:
	    return "1.2.0";
	case IMWVersion.VER_130:
	    return "1.3.0";
	case IMWVersion.VER_200:
	    return "2.0.0";
	case IMWVersion.VER_300:
	    return "3.0.0";
	case IMWVersion.VER_310:
	    return "3.1.0";
	case IMWVersion.VER_320:
	    return "3.2.0";
	case IMWVersion.VER_330:
	    return "3.3.0";
	case IMWVersion.VER_340:
	    return "3.4.0";
	default:
	    return "3.4.0";
	}
    }

    /**
     * Get all the MW version names to display.
     * 
     * @return An array of Strings containing the available MW version names in
     *         order.
     */
    public static String[] getAllVERnames() {
	return new String[] { 
		getVERname(IMWVersion.VER_030),
		getVERname(IMWVersion.VER_100), 
		getVERname(IMWVersion.VER_110),
		getVERname(IMWVersion.VER_120), 
		getVERname(IMWVersion.VER_130),
		getVERname(IMWVersion.VER_200),
		getVERname(IMWVersion.VER_300),
		getVERname(IMWVersion.VER_310),
		getVERname(IMWVersion.VER_320),
		getVERname(IMWVersion.VER_330),
		getVERname(IMWVersion.VER_340),
		};
    }

}
