/*

        Copyright 2007-2014 CNR-ISTI, http://isti.cnr.it
        Institute of Information Science and Technologies
        of the Italian National Research Council

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
package org.universaal.tools.packaging.tool;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

import org.universaal.tools.packaging.tool.preferences.EclipsePreferencesConfigurator;
import org.universaal.tools.packaging.tool.util.DefaultLogger;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.universaal.tools.packaging.tool"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	public static String tempDir;  

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context){

		try {
			super.start(context);
			plugin = this;
	
			tempDir = EclipsePreferencesConfigurator.local.getTempFolder();
					
			File outputDir = EclipsePreferencesConfigurator.local.getLogFolder();
			if ( outputDir != null ) {
				DefaultLogger.getInstance().log("*** [Application Packager] - The log file is available at "+outputDir+" ***", 1);
				System.setOut(new PrintStream(new File(outputDir+"/log.txt")));
				System.setErr(new PrintStream(new File(outputDir+"/errlog.txt")));
			} else if ( EclipsePreferencesConfigurator.local.isConsoleLog() == false ) {
				DefaultLogger.getInstance().log("*** [Application Packager] - The log file is available at "+tempDir+" ***", 1);
				System.setOut(new PrintStream(new File(tempDir+"/log.txt")));
				System.setErr(new PrintStream(new File(tempDir+"/errlog.txt")));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}