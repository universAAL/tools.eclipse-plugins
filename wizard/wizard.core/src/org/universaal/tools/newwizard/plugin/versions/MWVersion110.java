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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Implements IMWVersion for the 1.1.0 release of the MW. It extends 100 version
 * in order to reuse similar methods, but overrides everything in which it
 * differs from it.
 * 
 * @author alfiva
 */
public class MWVersion110 extends MWVersion100{
    
    @Override
    public int getMWVersionNumber() {
	return IMWVersion.VER_110;
    }
    
    @Override
    public String[] getChecksLabels() {
	String[] values={"Page2.60","Page2.61","Page2.62", 
		"Page2.63","Page2.64","Page2.65", 
		"Page2.76","Page2.77", 
		" "," "};
	return values;
    }
    
    @Override
    public boolean[] getChecksVisible() {
	boolean[] values={true,true,true, 
		true,true,true, 
		true,true, 
		false,false};
	return values;
    }
    
    @Override
    public boolean[] getChecksActiveByApp(int app) {
	boolean[] values;
	values = new boolean[]{
		(app==APP_FULL||app==APP_NOGUI||app==APP_ACT), 
		(app==APP_FULL||app==APP_NOGUI), 
		false, 
		(app==APP_FULL||app==APP_NOGUI||app==APP_REASON), 
		(app==APP_FULL||app==APP_NOGUI||app==APP_ACT||app==APP_GAUGE||app==APP_REASON), 
		false,
		(app==APP_HANDLER), 
		(app==APP_FULL), 
		false, 
		false };
	return values;
    }
    @Override
    public String[] getClassesLabels() {
	String[] values = { "PageI.40", "PageI.41", "PageI.42", "PageI.43",
		"PageI.44", "PageI.55", "PageI.56" };
	return values;
    }

    //________CONSTANTS________
    
    protected static final String BASIC_DEPS = 
	    "		<dependency>\n"
	    + "			<groupId>org.apache.felix</groupId>\n"
	    + "			<artifactId>org.osgi.core</artifactId>\n"
	    + "			<version>1.0.1</version>\n"
	    + "		</dependency>\n"
	    + "		<dependency>\n"
	    + "			<groupId>org.universAAL.middleware</groupId>\n"
	    + "			<artifactId>mw.data.representation</artifactId>\n"
	    + "			<version>1.1.0</version>\n"
	    + "		</dependency>\n"
	    + "		<dependency>\n"
	    + "			<groupId>org.universAAL.middleware</groupId>\n"
	    + "			<artifactId>mw.bus.model</artifactId>\n"
	    + "			<version>1.1.0</version>\n"
	    + "		</dependency>\n"
	    + "		<dependency>\n"
	    + "			<groupId>org.universAAL.middleware</groupId>\n"
	    + "			<artifactId>mw.container.xfaces</artifactId>\n"
	    + "			<version>1.1.0</version>\n"
	    + "		</dependency>\n"
	    + "		<dependency>\n"
	    + "			<groupId>org.universAAL.middleware</groupId>\n"
	    + "			<artifactId>mw.container.osgi</artifactId>\n"
	    + "			<version>1.1.0</version>\n"
	    + "		</dependency>\n";	
    protected static final String SERVICE_DEPS =
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.middleware</groupId>\n" 
	    + "			<artifactId>mw.bus.service</artifactId>\n" 
	    + "			<version>1.1.0</version>\n" 
	    + "		</dependency>\n";
    protected static final String CONTEXT_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.middleware</groupId>\n" 
	    + "			<artifactId>mw.bus.context</artifactId>\n" 
	    + "			<version>1.1.0</version>\n" 
	    + "		</dependency>\n";
    protected static final String UI_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.middleware</groupId>\n" 
	    + "			<artifactId>mw.bus.ui</artifactId>\n" 
	    + "			<version>1.1.0</version>\n" 
	    + "		</dependency>\n";
    protected static final String PHWORLD_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.ontology</groupId>\n" 
	    + "			<artifactId>ont.phWorld</artifactId>\n" 
	    + "			<version>1.1.0</version>\n" 
	    + "		</dependency>\n";
    protected static final String PROFILE_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.ontology</groupId>\n" 
	    + "			<artifactId>ont.profile</artifactId>\n" 
	    + "			<version>1.1.0</version>\n" 
	    + "		</dependency>\n";
    
    //________FILE MANIPULATION METHODS________
    
    /* (non-Javadoc)
     * @see org.universaal.tools.newwizard.plugin.versions.MWVersion100#customizePom(java.lang.String, java.io.InputStream, boolean[], boolean, boolean)
     */
    @Override
    public InputStream customizePom(String pack, InputStream input,
	    boolean[] checks, boolean phworld, boolean profile) throws IOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	StringBuilder output = new StringBuilder();
	String line;
	while ((line = reader.readLine()) != null) {
	    if (line.contains("</project>")) {
		output.append("	<packaging>bundle</packaging>\n");
		output.append("	<dependencies>\n");
		output.append(BASIC_DEPS);
		if (checks[0] || checks[1]) {
		    output.append(SERVICE_DEPS);
		}
		if (checks[3] || checks[4]) {
		    output.append(CONTEXT_DEPS);
		}
		if (checks[6] || checks[7]) {
		    output.append(UI_DEPS);
		}
		if (phworld) {
		    output.append(PHWORLD_DEPS);
		}
		if (profile) {
		    output.append(PROFILE_DEPS);
		}
		output.append("	</dependencies>\n");
		output.append(BUILD.replace(TAG_PACKAGE, pack));
		output.append(REPOS);
		output.append("</project>\n");
	    } else {
		output.append(line + "\n");
	    }
	}
	return new ByteArrayInputStream(output.toString().getBytes());
    }
    
    /* (non-Javadoc)
     * @see org.universaal.tools.newwizard.plugin.versions.MWVersion100#updatePom(java.io.InputStream, int)
     */
    @Override
    protected InputStream updatePom(InputStream input, int clstype) throws IOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(
		input));
	StringBuilder output = new StringBuilder();
	String line;
	boolean context = false;
	boolean service = false;
	boolean ui = false;
	while ((line = reader.readLine()) != null) {
	    if (line.contains("mw.bus.context")) { //$NON-NLS-1$
		context = true;
	    }
	    if (line.contains("mw.bus.service")) { //$NON-NLS-1$
		service = true;
	    }
	    if (line.contains("mw.bus.ui")) { //$NON-NLS-1$
		ui = true;
	    }
	    if (line.contains("</dependencies>")) { //$NON-NLS-1$
		StringBuilder outputnew = new StringBuilder();
		if (!context && (clstype == 0 || clstype == 1 || clstype == 2)) {
		    outputnew
			    .append(SERVICE_DEPS); //$NON-NLS-1$
		}
		if (!service
			&& (clstype == 3 || clstype == 4 )) {
		    outputnew
			    .append(CONTEXT_DEPS); //$NON-NLS-1$
		}
		if (!ui
			&& (clstype > 4)) {
		    outputnew
			    .append(UI_DEPS); //$NON-NLS-1$
		}
		outputnew.append("</dependencies>"); //$NON-NLS-1$
		line = line.replace("</dependencies>", outputnew.toString()); //$NON-NLS-1$
	    }
	    output.append(line + "\n"); //$NON-NLS-1$
	}
	return new ByteArrayInputStream(output.toString().getBytes());
    }
    
    //________HELPER METHODS________
    
    /* (non-Javadoc)
     * @see org.universaal.tools.newwizard.plugin.versions.MWVersion030#getChecksFiles()
     */
    @Override
    public String[] getChecksFiles() {
	String[] values = {"SCallee", "SCaller", " ",
		"CSubscriber", "CPublisher", " ",
		"UIfHandler", "UIfCaller",  };
	return values;
    }
    
    /* (non-Javadoc)
     * @see org.universaal.tools.newwizard.plugin.versions.MWVersion030#getClassesFiles()
     */
    @Override
    public String[] getClassesFiles() {
	String[] values = { "SCallee", "SCalleeProvidedService", "SCaller", "CSubscriber",
		"CPublisher", "UIfCaller", "UIfHandler" };
	return values;
    }
    
    /* (non-Javadoc)
     * @see org.universaal.tools.newwizard.plugin.versions.MWVersion030#getTemplateFilesByApp(int)
     */
    @Override
    public String[] getTemplateFilesByApp(int app) {
	switch (app) {
	case APP_FULL:
	    return new String[] { "Activator", "SCallee",
		    "SCalleeProvidedService", "SCaller", "CSubscriber",
		    "CPublisher", "UIfCaller" };
	case APP_NOGUI:
	    return new String[] { "Activator", "SCallee",
		    "SCalleeProvidedService", "SCaller", "CSubscriber",
		    "CPublisher" };
	case APP_GAUGE:
	    return new String[] { "Activator", "CPublisher" };
	case APP_ACT:
	    return new String[] { "Activator", "SCallee",
		    "SCalleeProvidedService", "CPublisher" };
	case APP_REASON:
	    return new String[] { "Activator", "CSubscriber", "CPublisher" };
	case APP_HANDLER:
	    return new String[] {};
	default:
	    return new String[] {};
	}
    }
    
    /* (non-Javadoc)
     * @see org.universaal.tools.newwizard.plugin.versions.MWVersion100#getMainFolder()
     */
    @Override
    public String getMainFolder() {
	return "files/1.1.0/";
    }
}
