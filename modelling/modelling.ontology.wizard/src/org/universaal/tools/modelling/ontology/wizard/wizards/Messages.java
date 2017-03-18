package org.universaal.tools.modelling.ontology.wizard.wizards;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "org.universaal.tools.modelling.ontology.wizard.wizards.messages";//+Locale.getDefault().getLanguage().toLowerCase()+"_"+Locale.getDefault().getLanguage().toUpperCase();//$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
	    .getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String getString(String key) {
	try {
	    return RESOURCE_BUNDLE.getString(key);
	} catch (MissingResourceException e) {
	    return '!' + key + '!';
	}
    }
}
