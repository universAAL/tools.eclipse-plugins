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

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This interface must be implemented for each new release that can be used as
 * MW compliance in the wizards. Although it could be possible to use an
 * abstract class, an interface was chosen to allow for more flexibility upon
 * unexpected requirements by future MW versions. If two releases are very
 * similar, the newest one can inherit from the previous, and override the
 * appropriate methods.
 * 
 * @author alfiva
 */
public interface IMWVersion {
    /**
     * Identifies MW version 0.3.0-SNAPSHOT.
     */
    public static final int VER_030=0;
    /**
     * Identifies MW version 1.0.0.
     */
    public static final int VER_100=1;
    /**
     * Identifies MW version 1.1.0.
     */
    public static final int VER_110=2;
    /**
     * Identifies MW version 1.2.0.
     */
    public static final int VER_120=3;
    /**
     * Identifies MW version 1.3.0.
     */
    public static final int VER_130=4;
    /**
     * Identifies MW version 2.0.0.
     */
    public static final int VER_200=5;
    /**
     * Identifies MW version 3.0.0.
     */
    public static final int VER_300=6;
    /**
     * Identifies MW version 3.1.0.
     */
    public static final int VER_310=7;
    /**
     * Identifies MW version 3.2.0.
     */
    public static final int VER_320=8;
    /**
     * Identifies MW version 3.3.0.
     */
    public static final int VER_330=9;
    /**
     * Identifies MW version 3.4.0.
     */
    public static final int VER_340=10;
    
    /**
     * Identifies a sample AAL App that makes full use of uAAL wrappers.
     */
    public static final int APP_FULL=0;
    /**
     * Identifies a sample App that uses uAAL wrappers but without user
     * interface.
     */
    public static final int APP_NOGUI=1;
    /**
     * Identifies a Context Gauge, an App that only gives context info.
     */
    public static final int APP_GAUGE=2;
    /**
     * Identifies a Context Actuator, which provides services to operate on some
     * device and gives info about it.
     */
    public static final int APP_ACT=3;
    /**
     * Identifies a context Reasoner that gets context info and infers new
     * context info.
     */
    public static final int APP_REASON=4;
    /**
     * Identifies a User Interface Handler.
     */
    public static final int APP_HANDLER=5;
    
    /**
     * Get the MW version identifier.
     * 
     * @return The constant that identifies the MW version represented by this
     *         implementor.
     */
    public int getMWVersionNumber();
    
    /**
     * Get the readable labels for the available classes checkboxes that can be
     * selected in project wizard page 1. The returning array must be
     * "synchronized" in order, size and meaning with the returned arrays of the
     * other getCheck* methods.
     * 
     * @return The array of readable Strings.
     */
    public String[] getChecksLabels();

    /**
     * Get which of the classes checkboxes are visible in project wizard page 1.
     * The returning array must be "synchronized" in order, size and meaning
     * with the returned arrays of the other getCheck* methods.
     * 
     * @return An array of booleans: true if the checkbox should be shown, false
     *         otherwise.
     */
    public boolean[] getChecksVisible();

    /**
     * Get which of the classes checkboxes are marked in project wizard page 1
     * for a given selected template app. The returning array must be
     * "synchronized" in order, size and meaning with the returned arrays of the
     * other getCheck* methods.
     * 
     * @param app
     *            Identifier of the sample application.
     * @return An array of booleans: true if the checkbox should be marked,
     *         false otherwise.
     */
    public boolean[] getChecksActiveByApp(int app);

    /**
     * Get the list of readable labels to be shown in the available classes in
     * the item wizard.
     * 
     * @return The array of readable Strings.
     */
    public String[] getClassesLabels();
    
    /**
     * Create all the needed files for a template application with sample code.
     * 
     * @param src
     *            Source folder where to place the package with the files.
     * @param app
     *            Identifier of the sample application.
     * @param pack
     *            Full name of the package where the files will be placed.
     * @param monitor
     *            Eclipse monitor.
     * @throws CoreException
     *             If there is a problem with Eclipse.
     * @throws IOException
     *             If there is a problem handling the files.
     */
    public void createTemplateFiles(IFolder src, int app, String pack, IProgressMonitor monitor) throws CoreException, IOException;

    /**
     * Create the files selected in the wizard.
     * 
     * @param src
     *            Source folder where to place the package with the files.
     * @param checks
     *            Array of booleans representing which checkboxes are marked.
     *            Must be "synchronized" in order, size and meaning with the
     *            returned arrays of the other getCheck* methods.
     * @param pack
     *            Full name of the package where the files will be placed.
     * @param monitor
     *            Eclipse monitor.
     * @throws CoreException
     *             If there is a problem with Eclipse.
     * @throws IOException
     *             If there is a problem handling the files.
     */
    public void createEmptyFiles(IFolder src, boolean[] checks, String pack, IProgressMonitor monitor) throws CoreException, IOException;

    /**
     * Modify the POM file according to the generated files by project wizard.
     * 
     * @param pom
     *            The handle to the POM file.
     * @param checks
     *            Array of booleans representing which checkboxes are marked.
     *            Must be "synchronized" in order, size and meaning with the
     *            returned arrays of the other getCheck* methods.
     * @param pack
     *            Full name of the package where the files will be placed.
     * @param monitor
     *            Eclipse monitor.
     * @throws CoreException
     *             If there is a problem with Eclipse.
     * @throws IOException
     *             If there is a problem handling the files.
     */
    public void modifyPOMFile(IFile pom, boolean[] checks, String pack, IProgressMonitor monitor) throws CoreException, IOException;

    /**
     * Create a new file according to what was selected in the item wizard.
     * 
     * @param src
     *            Conainer (package or folder) where to place the file.
     * @param pack
     *            Full name of the package where the file will be placed.
     * @param clstype
     *            Index representing the class type selected, according to the
     *            array returned by getClassesLabels.
     * @param name
     *            Name of the file to be created.
     * @param monitor
     *            Eclipse monitor.
     * @return True if there was no problem. False if the file couldnt be
     *         created (usually because it already exists).
     * @throws CoreException
     *             If there is a problem with Eclipse.
     * @throws IOException
     *             If there is a problem handling the files.
     */
    public boolean createNewFile(IContainer src, String pack, int clstype,String name, IProgressMonitor monitor) throws CoreException,IOException;

    /**
     * Modify the POM file according to the generated file by item wizard.
     * 
     * @param pom
     *            The handle to the POM file.
     * @param clstype
     *            Index representing the class type selected, according to the
     *            array returned by getClassesLabels.
     * @param monitor
     *            Eclipse monitor.
     * @throws CoreException
     *             If there is a problem with Eclipse.
     * @throws IOException
     *             If there is a problem handling the files.
     */
    public void modifyPOMFile(IFile pom, int clstype, IProgressMonitor monitor) throws CoreException, IOException;
}
