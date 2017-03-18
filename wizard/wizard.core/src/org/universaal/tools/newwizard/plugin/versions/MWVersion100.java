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
 * Implements IMWVersion for the 1.0.0 release of the MW. It extends 030 version
 * in order to reuse similar methods, but overrides everything in which it
 * differs from it.
 * 
 * @author alfiva
 */
public class MWVersion100 extends MWVersion030{
    
    @Override
    public int getMWVersionNumber() {
	return IMWVersion.VER_100;
    }

    //________CONSTANTS________
    /**
     * Basic dependencies to all artifacts in POM.
     */
    protected static final String BASIC_DEPS = 
	    "		<dependency>\n"
	    + "			<groupId>org.apache.felix</groupId>\n"
	    + "			<artifactId>org.osgi.core</artifactId>\n"
	    + "			<version>1.0.1</version>\n"
	    + "		</dependency>\n"
	    + "		<dependency>\n"
	    + "			<groupId>org.universAAL.middleware</groupId>\n"
	    + "			<artifactId>mw.data.representation</artifactId>\n"
	    + "			<version>1.0.0</version>\n"
	    + "		</dependency>\n"
	    + "		<dependency>\n"
	    + "			<groupId>org.universAAL.middleware</groupId>\n"
	    + "			<artifactId>mw.bus.model</artifactId>\n"
	    + "			<version>1.0.0</version>\n"
	    + "		</dependency>\n"
	    + "		<dependency>\n"
	    + "			<groupId>org.universAAL.middleware</groupId>\n"
	    + "			<artifactId>mw.container.xfaces</artifactId>\n"
	    + "			<version>1.0.0</version>\n"
	    + "		</dependency>\n"
	    + "		<dependency>\n"
	    + "			<groupId>org.universAAL.middleware</groupId>\n"
	    + "			<artifactId>mw.container.osgi</artifactId>\n"
	    + "			<version>1.0.0</version>\n"
	    + "		</dependency>\n";	
    /**
     * Service bus dependencies.
     */
    protected static final String SERVICE_DEPS =
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.middleware</groupId>\n" 
	    + "			<artifactId>mw.bus.service</artifactId>\n" 
	    + "			<version>1.0.0</version>\n" 
	    + "		</dependency>\n";
    /**
     * Context bus dependencies.
     */
    protected static final String CONTEXT_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.middleware</groupId>\n" 
	    + "			<artifactId>mw.bus.context</artifactId>\n" 
	    + "			<version>1.0.0</version>\n" 
	    + "		</dependency>\n";
    /**
     * I/O bus dependencies.
     */
    protected static final String UI_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.middleware</groupId>\n" 
	    + "			<artifactId>mw.bus.io</artifactId>\n" 
	    + "			<version>1.0.0</version>\n" 
	    + "		</dependency>\n";
    /**
     * phWorld ontology dependencies.
     */
    protected static final String PHWORLD_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.ontology</groupId>\n" 
	    + "			<artifactId>ont.phWorld</artifactId>\n" 
	    + "			<version>1.0.0</version>\n" 
	    + "		</dependency>\n";
    /**
     * profiling ontology dependencies.
     */
    protected static final String PROFILE_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.ontology</groupId>\n" 
	    + "			<artifactId>ont.profile</artifactId>\n" 
	    + "			<version>1.0.0</version>\n" 
	    + "		</dependency>\n";
    
    //________FILE MANIPULATION METHODS________
    
    /* (non-Javadoc)
     * @see org.universaal.tools.newwizard.plugin.versions.MWVersion030#customizePom(java.lang.String, java.io.InputStream, boolean[], boolean, boolean)
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
		if (checks[6] || checks[7] || checks[8] || checks[9]) {
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
     * @see org.universaal.tools.newwizard.plugin.versions.MWVersion030#updatePom(java.io.InputStream, int)
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
	    if (line.contains("mw.bus.io")) { //$NON-NLS-1$
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
     * @see org.universaal.tools.newwizard.plugin.versions.MWVersion030#getMainFolder()
     */
    @Override
    public String getMainFolder() {
	return "files/1.0.0/";
    }
    
}
