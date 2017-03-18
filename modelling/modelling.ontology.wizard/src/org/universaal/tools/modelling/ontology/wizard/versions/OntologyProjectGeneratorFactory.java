package org.universaal.tools.modelling.ontology.wizard.versions;


public class OntologyProjectGeneratorFactory {
    /**
     * Returns an implementation of IMWversion for the given version.
     * 
     * @param versionIndex
     *            The version ID of the MW, as represented by IMWVersion
     *            constants.
     * @return The implementation of the right IMWVersion.
     */
    public static IOntologyProjectGenerator getMWVersion(int versionIndex){
	switch (versionIndex) {
	case IOntologyProjectGenerator.VER_110:
	    return new OntologyProjectGeneratorMW110();
	case IOntologyProjectGenerator.VER_120:
	    return new OntologyProjectGeneratorMW120();
	case IOntologyProjectGenerator.VER_130:
	    return new OntologyProjectGeneratorMW130();
	case IOntologyProjectGenerator.VER_200:
	    return new OntologyProjectGeneratorMW200();
	case IOntologyProjectGenerator.VER_300:
	    return new OntologyProjectGeneratorMW3x0(IOntologyProjectGenerator.VER_300);
	case IOntologyProjectGenerator.VER_310:
	    return new OntologyProjectGeneratorMW3x0(IOntologyProjectGenerator.VER_310);
	case IOntologyProjectGenerator.VER_320:
	    return new OntologyProjectGeneratorMW3x0(IOntologyProjectGenerator.VER_320);
	case IOntologyProjectGenerator.VER_330:
	    return new OntologyProjectGeneratorMW3x0(IOntologyProjectGenerator.VER_330);
	case IOntologyProjectGenerator.VER_340:
	    return new OntologyProjectGeneratorMW3x0(IOntologyProjectGenerator.VER_340);
	default:
	    return new OntologyProjectGeneratorMW3x0(IOntologyProjectGenerator.VER_340);
	}
    }
    
    /**
     * Gets the name of the MW version to display.
     * 
     * @param version
     *            MW Version number, as in IMWversion.
     * @return The String with the name.
     */
    public static String getVersonName(int version){
	switch (version) {
	case IOntologyProjectGenerator.VER_110:
	    return "1.1.0";
	case IOntologyProjectGenerator.VER_120:
	    return "1.2.0";
	case IOntologyProjectGenerator.VER_130:
	    return "1.3.0";
	case IOntologyProjectGenerator.VER_200:
	    return "2.0.0";
	case IOntologyProjectGenerator.VER_300:
	    return "3.0.0";
	case IOntologyProjectGenerator.VER_310:
	    return "3.1.0";
	case IOntologyProjectGenerator.VER_320:
	    return "3.2.0";
	case IOntologyProjectGenerator.VER_330:
	    return "3.3.0";
	case IOntologyProjectGenerator.VER_340:
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
    public static String[] getAllVersonNames() {
	return new String[] { 
		getVersonName(IOntologyProjectGenerator.VER_110),
		getVersonName(IOntologyProjectGenerator.VER_120), 
		getVersonName(IOntologyProjectGenerator.VER_130),
		getVersonName(IOntologyProjectGenerator.VER_200),
		getVersonName(IOntologyProjectGenerator.VER_300),
		getVersonName(IOntologyProjectGenerator.VER_310),
		getVersonName(IOntologyProjectGenerator.VER_320),
		getVersonName(IOntologyProjectGenerator.VER_330),
		getVersonName(IOntologyProjectGenerator.VER_340),
		};
    }
}
