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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * Implements IMWVersion for the 0.3.0-SNAPSHOT version of the MW. It includes
 * additional methods for file-handling, since have particularities depending on
 * the version.
 * 
 * @author alfiva
 */
public class MWVersion030 implements IMWVersion{

    public int getMWVersionNumber() {
	return IMWVersion.VER_030;
    }
    
    public String[] getChecksLabels() {
	String[] values={"Page2.60","Page2.61","Page2.62", 
		"Page2.63","Page2.64","Page2.65", 
		"Page2.66","Page2.67", 
		"Page2.68","Page2.69"};
	return values;
    }

    public boolean[] getChecksVisible() {
	boolean[] values={true,true,true, 
		true,true,true, 
		true,true, 
		true,true};
	return values;
    }

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
		(app==APP_FULL), 
		(app==APP_HANDLER) };
	return values;
    }

    public String[] getClassesLabels() {
	String[] values = { "PageI.40", "PageI.41", "PageI.42", "PageI.43",
		"PageI.44", "PageI.45", "PageI.46", "PageI.47", "PageI.48" };
	return values;
    }

    public void createTemplateFiles(IFolder src, int app, String pack, IProgressMonitor monitor) throws CoreException, IOException {
	String[] filenames = getTemplateFilesByApp(app);
	String folder = getTemplateFolderByApp(app);
	for (int i = 0; i < filenames.length; i++) {
	    IFile f = src.getFile(filenames[i]+".java");
	    f.create(customizePackAndName(pack, folder + filenames[i]), true, monitor);
	}
    }

    public void createEmptyFiles(IFolder src, boolean[] checks, String pack,
	    IProgressMonitor monitor) throws CoreException, IOException {
	// First always create Activator
	IFile f1 = src.getFile("Activator.java");
	f1.create(customizeActivator(pack, getMainFolder() + "Activator", checks), true, monitor);

	// Then iterate checks and create appropriate files
	String[] filenames = getChecksFiles();
	for (int i = 0; i < checks.length; i++) {
	    if (checks[i]) {
		// If not the Default or set to Default
		if (!(     ((i == 1) && (checks[2])) 
			|| ((i == 4) && (checks[5]))
			|| (i == 2) 
			|| (i == 5))) {
		    IFile f = src.getFile(filenames[i]+".java");
		    f.create(customizePackAndName(pack, getMainFolder() + filenames[i]), true, monitor);
		}
		// If its SCallee, also create Provided Serv
		if (i == 0) {
		    IFile faux = src.getFile("SCalleeProvidedService.java");
		    faux.create(customizePackAndName(pack, getMainFolder() + "SCalleeProvidedService"), true, monitor);
		}
	    }
	}
    }

    public void modifyPOMFile(IFile pom, boolean[] checks, String pack,
	    IProgressMonitor monitor) throws CoreException, IOException {
	pom.setContents(customizePom(pack,pom.getContents(), checks, true,true),
		true, true, monitor);//TODO: customizePom(,,true,true) set them properly to phworld and profile
    }
    
    public void modifyPOMFile(IFile pom, int clstype, IProgressMonitor monitor) throws CoreException, IOException {
	pom.setContents(updatePom(pom.getContents(), clstype),
		true, true, monitor);
    }
    
    public boolean createNewFile(IContainer src, String pack, int type,
	    String name, IProgressMonitor monitor) throws CoreException,
	    IOException {
	//First check if they exist
	IFile file, fileaux = null;
	file = src.getFile(new Path(name + ".java"));
	if (file.exists())
	    return false;
	if (type==0) {
	    fileaux = src.getFile(new Path(name + "ProvidedService.java"));
	    if (fileaux.exists())
		return false;
	}
	//Then create
	file.create(customizePackAndName(pack, getMainFolder() + getClassesFiles()[type], name),
		true, monitor);
	if (fileaux != null) {
	    fileaux.create(
		    customizePackAndName(pack, getMainFolder()
			    + getClassesFiles()[1], name + "ProvidedService"),
		    true, monitor);
	}
	return true;
    }
    
    //________CONSTANTS________
    
    /**
     * Identifies "package" section in file.
     */
    public static String TAG_PACKAGE="/*TAG:PACKAGE*/";
    /**
     * Identifies "import" section in file.
     */
    public static String TAG_IMPORT="/*TAG:IMPORT*/";
    /**
     * Identifies "initialization" section in file.
     */
    public static String TAG_INIT="/*TAG:INIT*/";
    /**
     * Identifies "start" method in Activator file.
     */
    public static String TAG_START="/*TAG:START*/";
    /**
     * Identifies "stop" method in Activator file.
     */
    public static String TAG_STOP="/*TAG:STOP*/";
    /**
     * Identifies class name section in file.
     */
    public static String TAG_CLASSNAME="/*TAG:CLASSNAME*/";

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
	    + "			<version>0.3.0-SNAPSHOT</version>\n"
	    + "		</dependency>\n";	
    /**
     * Service bus dependencies.
     */
    protected static final String SERVICE_DEPS =
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.middleware</groupId>\n" 
	    + "			<artifactId>mw.bus.service</artifactId>\n" 
	    + "			<version>0.3.0-SNAPSHOT</version>\n" 
	    + "		</dependency>\n";
    /**
     * Context bus dependencies.
     */
    protected static final String CONTEXT_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.middleware</groupId>\n" 
	    + "			<artifactId>mw.bus.context</artifactId>\n" 
	    + "			<version>0.3.0-SNAPSHOT</version>\n" 
	    + "		</dependency>\n";
    /**
     * I/O bus dependencies.
     */
    protected static final String UI_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.middleware</groupId>\n" 
	    + "			<artifactId>mw.bus.io</artifactId>\n" 
	    + "			<version>0.3.0-SNAPSHOT</version>\n" 
	    + "		</dependency>\n";
    /**
     * phWorld ontology dependencies.
     */
    protected static final String PHWORLD_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.ontology</groupId>\n" 
	    + "			<artifactId>ont.phWorld</artifactId>\n" 
	    + "			<version>0.2.2-SNAPSHOT</version>\n" 
	    + "		</dependency>\n";
    /**
     * profiling ontology dependencies.
     */
    protected static final String PROFILE_DEPS = 
	    "		<dependency>\n" 
	    + "			<groupId>org.universAAL.ontology</groupId>\n" 
	    + "			<artifactId>ont.profile</artifactId>\n" 
	    + "			<version>0.3.0-SNAPSHOT</version>\n" 
	    + "		</dependency>\n";
    /**
     * Common project build section in POM.
     */
    protected static final String BUILD = 
	    "	<build>\n" 
	    + "		<plugins>\n" 
	    + "			<plugin>\n" 
	    + "				<groupId>org.apache.felix</groupId>\n" 
	    + "				<artifactId>maven-bundle-plugin</artifactId>\n" 
	    + "				<extensions>true</extensions>\n" 
	    + "				<configuration>\n" 
	    + "					<instructions>\n" 
	    + "						<Bundle-Name>${project.name}</Bundle-Name>\n" 
	    + "						<Bundle-Activator>" + TAG_PACKAGE + ".Activator</Bundle-Activator>\n" 
	    + "						<Bundle-Description>${project.description}</Bundle-Description>\n" 
	    + "						<Bundle-SymbolicName>${project.artifactId}</Bundle-SymbolicName>\n" 
	    + "					</instructions>\n" 
	    + "				</configuration>\n" 
	    + "			</plugin>\n" 
	    + "		</plugins>\n"
	    + "	</build>\n";
    /**
     * Common repositories section in POM
     */
    protected static final String REPOS = 
	    "	<repositories>\n"
	    + "		<repository>\n"
	    + "			<id>central</id>\n"
	    + "			<name>Central Maven Repository</name>\n"
	    + "			<url>http://repo1.maven.org/maven2</url>\n"
	    + "			<snapshots>\n"
	    + "				<enabled>false</enabled>\n"
	    + "			</snapshots>\n"
	    + "		</repository>\n"
	    + "		<repository>\n"
	    + "			<id>apache-snapshots</id>\n"
	    + "			<name>Apache Snapshots</name>\n"
	    + "			<url>http://repository.apache.org/snapshots</url>\n"
	    + "			<releases>\n"
	    + "				<enabled>false</enabled>\n"
	    + "			</releases>\n"
	    + "			<snapshots>\n"
	    + "				<updatePolicy>daily</updatePolicy>\n"
	    + "			</snapshots>\n"
	    + "		</repository>\n"
	    + "		<repository>\n"
	    + "			<id>uaal</id>\n"
	    + "			<name>universAAL Repositories</name>\n"
	    + "			<url>http://depot.universaal.org/maven-repo/releases/</url>\n"
	    + "			<snapshots>\n"
	    + "				<enabled>false</enabled>\n"
	    + "			</snapshots>\n"
	    + "		</repository>\n"
	    + "		<repository>\n"
	    + "			<id>uaal-snapshots</id>\n"
	    + "			<name>universAAL Snapshot Repositories</name>\n"
	    + "			<url>http://depot.universaal.org/maven-repo/snapshots/</url>\n"
	    + "			<releases>\n"
	    + "				<enabled>false</enabled>\n"
	    + "			</releases>\n" 
	    + "		</repository>\n"
	    + "	</repositories>\n";

    //________FILE MANIPULATION METHODS________
    
    /**
     * Scan a file stream and replace the package and name tags with the
     * appropriate values. Name of the class is automatically taken from file
     * name.
     * 
     * @param pack
     *            Full name of the package.
     * @param filepath
     *            Path to the original blank file.
     * @return The modified stream.
     * @throws IOException
     *             If any problem with the file.
     */
    public InputStream customizePackAndName(String pack, String filepath) throws IOException {
	return customizePackAndName(pack, filepath, filepath.substring(filepath.lastIndexOf("/") + 1));
    }
    
    /**
     * Scan a file stream and replace the package and name tags with the
     * appropriate values.
     * 
     * @param pack
     *            Full name of the package.
     * @param filepath
     *            Path to the original blank file.
     * @param name
     *            Name of the final file.
     * @return The modified stream.
     * @throws IOException
     *             If any problem with the file.
     */
    public InputStream customizePackAndName(String pack, String filepath, String name) throws IOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(
		MWVersion030.class.getClassLoader().getResourceAsStream(
			filepath+".java")));
	StringBuilder output = new StringBuilder();
	String line;
	while ((line = reader.readLine()) != null) {
	    if (line.contains(TAG_PACKAGE)) {
		line = "package " + pack + ";\n";
	    }else if (line.contains(TAG_CLASSNAME)) {
		line = line
			.replace(
				TAG_CLASSNAME, name);
	    }
	    output.append(line + "\n");
	}
	return new ByteArrayInputStream(output.toString().getBytes());
    }

    /**
     * Scan an Activator and replace all tags with appropriate content.
     * 
     * @param pack
     *            Full name of the package.
     * @param filepath
     *            Path to the original blank file.
     * @param checks
     *            Array of booleans representing which checkboxes are marked.
     *            Must be "synchronized" in order, size and meaning with the
     *            returned arrays of the other getCheck* methods.
     * @return The modified stream.
     * @throws IOException
     *             If any problem with the file.
     */
    public InputStream customizeActivator(String pack, String filepath,
	     boolean[] checks) throws IOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(
		MWVersion030.class.getClassLoader().getResourceAsStream(
			filepath + ".java")));
	StringBuilder output = new StringBuilder();
	String line;
	String[] filenames = getChecksFiles();
	while ((line = reader.readLine()) != null) {
	    if (line.contains(TAG_PACKAGE)) {
		output.append("package " + pack + ";\n");
	    } else if (line.contains(TAG_IMPORT)) {
		if (checks[2]) {
		    output.append("import org.universAAL.middleware.service.ServiceCaller;\n");
		    output.append("import org.universAAL.middleware.service.DefaultServiceCaller;\n");
		}
		if (checks[5]) {
		    output.append("import org.universAAL.middleware.context.ContextPublisher;\n");
		    output.append("import org.universAAL.middleware.context.DefaultContextPublisher;\n");
		}
	    } else if (line.contains(TAG_INIT)) {
		for(int i=0;i<checks.length;i++){
		    if(checks[i] && i!=2 && i!=5){//Skip defaults
			if(i==1 && checks[2]){
			    output.append("	public static ServiceCaller "+filenames[i].toLowerCase()+"=null;\n");
			}else if(i==4 && checks[5]){
			    output.append("	public static ContextPublisher "+filenames[i].toLowerCase()+"=null;\n");
			}else{
			    output.append("	public static "+filenames[i]+" "+filenames[i].toLowerCase()+"=null;\n");
			}
		    }
		}
	    } else if (line.contains(TAG_START)) {
		for(int i=0;i<checks.length;i++){
		    if(checks[i] && i!=2 && i!=5){//Skip defaults
			if(i==1 && checks[2]){
			    output.append("		"+filenames[i].toLowerCase()+"=new DefaultServiceCaller(context);\n");
			}else if(i==4 && checks[5]){
			    output.append("		//TODO: You have to pass your own ContextProvider instead of null!\n");
			    output.append("		"+filenames[i].toLowerCase()+"=new DefaultContextPublisher(context,null);\n");
			}else{
			    output.append("		"+filenames[i].toLowerCase()+"=new "+filenames[i]+"(context);\n");
			}
		    }
		}
		
	    } else if (line.contains(TAG_STOP)) {
		for(int i=0;i<checks.length;i++){
		    if(checks[i] && i!=2 && i!=5){//Skip defaults
			output.append("		"+filenames[i].toLowerCase()+".close();\n");
		    }
		}
	    } else {
		output.append(line + "\n");
	    }
	}
	return new ByteArrayInputStream(output.toString().getBytes());
    }
    
    /**
     * Scan the POM file and modify the contents according to parameters.
     * 
     * @param pack
     *            Full name of the package.
     * @param input
     *            Stream holding the POM file.
     * @param checks
     *            Array of booleans representing which checkboxes are marked.
     *            Must be "synchronized" in order, size and meaning with the
     *            returned arrays of the other getCheck* methods.
     * @param phworld
     *            True if POM must include dependency to phWorld ontology.
     * @param profile
     *            True if POM must include dependency to profiling ontology.
     * @return The modified stream.
     * @throws IOException
     *             If any problem with the file.
     */
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
    
    /**
     * Scan the POM file and modify the contents according to parameters.
     * 
     * @param input
     *            Stream holding the POM file.
     * @param clstype
     *            Index representing the class type selected, according to the
     *            array returned by getClassesLabels.
     * @return The modified stream.
     * @throws IOException
     *             If any problem with the file.
     */
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
    
    /**
     * Get the file names for the available classes checkboxes that can be
     * selected in project wizard page 1. The returning array must be
     * "synchronized" in order, size and meaning with the returned arrays of the
     * other getCheck* methods.
     * 
     * @return The array of file names.
     */
    public String[] getChecksFiles() {
	String[] values = {"SCallee", "SCaller", " ",
		"CSubscriber", "CPublisher", " ",
		"OPublisher", "OSubscriber", 
		"IPublisher", "ISubscriber" };
	return values;
    }
    
    /**
     * Get the list of file names for the available classes in the item wizard.
     * The returning array must be "synchronized" in order, size and meaning
     * with the returned array of the other getClassLabels method.
     * 
     * @return The array of file names.
     */
    public String[] getClassesFiles() {
	String[] values = { "SCallee", "SCalleeProvidedService", "SCaller", "CSubscriber",
		"CPublisher", "OSubscriber", "OPublisher", "ISubscriber", "IPublisher" };
	return values;
    }
    
    /**
     * Get the list of file names for a given template application.
     * 
     * @param app
     *            Identifier of the sample application.
     * @return Array with the list of file names. Must not contain ".java"
     *         suffix. Does not need to be "synchronized" with any method. The
     *         names are as they can be found in the right folder.
     */
    public String[] getTemplateFilesByApp(int app) {
	switch (app) {
	case APP_FULL:
	    return new String[] { "Activator", "SCallee",
		    "SCalleeProvidedService", "SCaller", "CSubscriber",
		    "CPublisher", "OPublisher", "ISubscriber" };
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

    /**
     * Get the folder where the template files can be found for each template
     * application.
     * 
     * @param app
     *            Identifier of the sample application.
     * @return The path of the template folder.
     */
    public String getTemplateFolderByApp(int app) {
	switch (app) {
	case APP_FULL:
	    return getMainFolder()+"templates/full/";
	case APP_NOGUI:
	    return getMainFolder()+"templates/nogui/";
	case APP_GAUGE:
	    return getMainFolder()+"templates/gauge/";
	case APP_ACT:
	    return getMainFolder()+"templates/actuator/";
	case APP_REASON:
	    return getMainFolder()+"templates/reasoner/";
	case APP_HANDLER:
	    return getMainFolder()+"templates/handler/";
	default:
	    return getMainFolder();
	}
    }
    
    /**
     * Get the root folder for this MW version files.
     * 
     * @return The path of the root folder.
     */
    public String getMainFolder() {
	return "files/0.3.0-SNAPSHOT/";
    }
}