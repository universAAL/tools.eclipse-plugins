/*	
	Copyright 2007-2016 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institute for Computer Graphics Research
	
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
package org.universaal.tools.envsetup.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.universaal.tools.envsetup.handlers.EnvSetupHandler;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Carsten Stockloew
 *
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.universaal.tools.envsetup.core"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static ILog log;

	/**
	 * The constructor
	 */
	public Activator() {
		log = getLog();
	}

	public static void log(String msg) {
		log(msg, null);
	}

	public static void log(String msg, Exception e) {
		if (log != null)
			log.log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, e));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		IPath path = getStateLocation();
		//System.out.println(" -- getStateLocation: " + path.toFile());
		File dir = path.toFile();
		File file = new File(dir, "EnvSetup.ini");
		if (file.exists()) {
			// don't do anything
		} else {
			// write dummy file
			Writer writer;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
				writer.write("universAAL Studio Environment Setup\n");
				writer.write("Delete this file to get the dialog open at startup again\n");
				writer.write("This only works one time, then the file will be created again");
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// open dialog
			EnvSetupHandler.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
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
}
