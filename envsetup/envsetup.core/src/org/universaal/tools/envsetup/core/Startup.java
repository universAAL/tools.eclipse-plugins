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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * 
 * @author Carsten Stockloew
 *
 */
public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		// just a dummy to get the activator started
		// System.out.println(" -- earlyStartup");

		createRundir();
		createConsole();
	}

	private void createConsole() {
		// redirect output to default console
		MessageConsole console = new MessageConsole("My Console", null);
		console.activate();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		MessageConsoleStream stream = console.newMessageStream();
		System.setOut(new PrintStream(stream));
		System.setErr(new PrintStream(stream));
	}

	private void createRundir() {
		// System.out.println("createRundir");
		String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		File baseDir = new File(new File(workspacePath, "rundir"), "confadmin");
		writeFile(baseDir, "mw.bus.model.osgi", "sodapop.key");
		writeFile(baseDir, "mw.managers.aalspace.osgi", "Home.space");
		writeFile(baseDir, "mw.managers.aalspace.osgi", "aalspace.xsd");
		writeFile(baseDir, "services", "mw.connectors.communication.jgroups.core.properties");
		writeFile(baseDir, "services", "mw.connectors.discovery.slp.core.properties");
		writeFile(baseDir, "services", "mw.managers.aalspace.core.properties");
		writeFile(baseDir, "services", "mw.managers.deploy.core.properties");
		writeFile(baseDir, "services", "mw.modules.aalspace.core.properties");
		writeFile(baseDir, "services", "org.ops4j.pax.logging.properties");
		writeFile(baseDir, "services", "org.universAAL.mw.data.representation.properties");

		// URL url = Activator.getDefault().getBundle().getEntry("files/"); //
		// Startup.class.getResource("files/");
		// if (url == null) {
		// // error - missing folder
		// System.out.println("ERROR: url == null");
		// } else {
		// System.out.println(" -- " + url.toString());
		// System.out.println(" -- " + url.getProtocol());
		//
		// File dir;
		// dir = new File(url.toString());
		//
		// try {
		// // dir = new File(FileLocator.resolve(url).toURI());
		// dir = new File(FileLocator.toFileURL(url).toURI());
		// System.out.println(" -- " + dir.toString());
		// } catch (URISyntaxException | IOException e) {
		// e.printStackTrace();
		// }
		//
		// for (File nextFile : dir.listFiles()) {
		// System.out.println(" -- file: " + nextFile.toString());
		// }
		// }
	}

	private void writeFile(File baseDir, String subDir, String filename) {
		File dir = new File(baseDir, subDir);
		dir.mkdirs();
		File file = new File(dir, filename);
		if (!file.exists()) {
			try {
				InputStream in = getClass().getResourceAsStream("/files/" + subDir + "/" + filename);
				OutputStream out = new FileOutputStream(file);
				byte buf[] = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0)
					out.write(buf, 0, len);
				out.close();
				in.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
