/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut für Graphische Datenverarbeitung
	
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
package org.universaal.uaalpax.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.universaal.uaalpax.shared.Attribute;
import org.universaal.uaalpax.ui.dialogs.NewRunconfigDialog;

public class NewRunConfigHandler extends AbstractHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String mode = event.getParameter("org.universaal.uaalpax.commandparameters.runDebugMode");
		boolean debug = (mode != null && mode.toLowerCase().equals("debug"));
		createNewLaunchConfiguration(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), debug);		
		return null;
	}
	
	private void createNewLaunchConfiguration(Shell shell, boolean debug) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType("org.eclipse.pde.ui.EquinoxLauncher");
		
		NewRunconfigDialog d = new NewRunconfigDialog(null);
		if (d.open() != Window.OK)
			return;
		
		try {
			System.out.println(" -- NewRunConfigHandler.createNewLaunchConfiguration");
			ILaunchConfigurationWorkingCopy configuration = type.newInstance(d.getContainer(), d.getName());
			configuration.setAttribute("append.args", true);
			configuration.setAttribute("automaticAdd", true);
			configuration.setAttribute("automaticValidate", false);
			configuration.setAttribute("bootstrap", "");
			configuration.setAttribute("checked", "");
			configuration.setAttribute("default_start_level", 60);
			configuration.setAttribute("clearConfig", false);
			configuration.setAttribute("configLocation", "${workspace_loc}/rundir/demo.config");
			configuration.setAttribute("default", true);
			configuration.setAttribute("default_auto_start", true);
			configuration.setAttribute("includeOptional", true);
			configuration.setAttribute("org.eclipse.debug.core.source_locator_id",
					"org.eclipse.pde.ui.launcher.PDESourceLookupDirector");
			
			configuration
					.setAttribute("org.eclipse.jdt.launching.JRE_CONTAINER",
							"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/J2SE-1.5");
			configuration
					.setAttribute(
							"org.eclipse.jdt.launching.PROGRAM_ARGUMENTS",
							"-console --obrRepositories=http://depot.universaal.org/nexus/content/repositories/snapshots/repository.xml,http://depot.universaal.org/nexus/content/repositories/releases/repository.xml,http://bundles.osgi.org/obr/browse?_xml=1&amp;amp;cmd=repository --org.ops4j.pax.url.mvn.repositories=+http://depot.universaal.org/nexus/content/groups/public,http://depot.universaal.org/nexus/content/repositories/snapshots@snapshots@noreleases --log=DEBUG");
			configuration
					.setAttribute(
							"org.eclipse.jdt.launching.VM_ARGUMENTS",
							"-Dosgi.noShutdown=true -Dfelix.log.level=4 -Dorg.universAAL.middleware.peer.is_coordinator=true -Dorg.universAAL.middleware.peer.member_of=urn:org.universAAL.aal_space:test_env -Dbundles.configuration.location=${workspace_loc}/rundir/confadmin -Djava.net.preferIPv4Stack=true");
			configuration.setAttribute("org.eclipse.jdt.launching.WORKING_DIRECTORY",
					"${workspace_loc}/rundir/demo.config");
			configuration.setAttribute("org.ops4j.pax.cursor.hotDeployment", false);
			configuration.setAttribute("org.ops4j.pax.cursor.logLevel", "DEBUG");
			configuration.setAttribute("org.ops4j.pax.cursor.overwrite", false);
			configuration.setAttribute("org.ops4j.pax.cursor.overwriteSystemBundles", false);
			configuration.setAttribute("org.ops4j.pax.cursor.overwriteUserBundles", false);
			configuration.setAttribute("default", true);
			configuration.setAttribute("default", true);
			
			if (!configuration.hasAttribute("org.ops4j.pax.cursor.profiles")) {
				ArrayList<String> classpath = new ArrayList<String>();
				classpath.add("felix.obr");
				configuration.setAttribute("org.ops4j.pax.cursor.profiles", classpath);
			}
			
			configuration.setAttribute("osgi_framework_id", "--platform=felix --version=4.4.1");
			configuration.setAttribute("pde.version", "3.3");
			configuration.setAttribute("show_selected_only", true);
			configuration.setAttribute("tracing", false);
			configuration.setAttribute("useCustomFeatures", false);
			configuration.setAttribute("useDefaultConfigArea", false);
			
			configuration.removeAttribute("target_bundles");
			
			Map<String, String> toSave = new HashMap<String, String>();
			List<String> arguments = new LinkedList<String>();
			arguments.add("--overwrite=true");
			arguments.add("--overwriteUserBundles=true");
			arguments.add("--overwriteSystemBundles=true");
			arguments.add("--log=DEBUG");
			arguments.add("--profiles=felix.obr");
			configuration.setAttribute(Attribute.PROVISION_ITEMS, toSave);
			configuration.setAttribute(Attribute.RUN_ARGUMENTS, arguments);
			
			configuration.doSave();
			
			// IStructuredSelection selection = new StructuredSelection(configuration);
			
			DebugUITools.openLaunchConfigurationDialog(shell, configuration, DebugUIPlugin.getDefault()
					.getLaunchConfigurationManager().getLaunchGroup(type, debug? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE).getIdentifier(),
					null);
			
			// TODO
			// DebugUITools.openLaunchConfigurationDialog(null, configuration,
			// DebugUIPlugin.getDefault().getLaunchConfigurationManager().getLaunchGroup("org.eclipse.pde.ui.EquinoxLauncher"),
			// null);
		} catch (CoreException e) {
			 ErrorDialog.openError(shell, "Error creating launch config",
			 		"An unenspected error has occured. Launch config could not be created.", new Status(Status.ERROR, "org.ops4j.pax.runner.uaal.ui", "See below", e));
		}
	}
}
