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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Carsten Stockloew
 *
 */
public class RepoMgmt {
	// assumption: the submodule folder has (ends with) the same name as the
	// main project,
	// e.g. "middleware" is "platform/middleware"
	// e.g. "samples" is "platform/xtras/samples"

	public static class Repo {

		/** Human-readable name of the repo, to be shown in the plugin ui */
		public String name;

		/** URL in git */
		public String url;

		/** The relative folder in which the pom file file is */
		public String pom;

		private String branch = null;
		private String folder = null;

		Repo(String name, String url, String pom) {
			this.name = name;
			this.url = url;
			this.pom = pom;
		}

		/**
		 * Returns true, if this repo is part of the 'platform' aggregator
		 * project, either the platform repo itself (uAAL.pom) or one of its
		 * submodules. Currently, only the distro-repos will return false.
		 */
		public boolean isPlatformRepo() {
			if (!useAgg)
				return false;
			return pom != null;
		}

		/**
		 * Returns true, if this repo is a submodule of the platform aggregator
		 * project.
		 */
		public boolean isSubmodule() {
			if (isPlatformRepo())
				if (!superpom.equals(pom))
					return true;
			return false;
		}

		public Repo setFolder(String folder) {
			this.folder = folder;
			return this;
		}

		/**
		 * Get the folder name of the repo from the url, e.g. for
		 * "https://github.com/universAAL/middleware.git", it returns
		 * "middleware". Exception: if a folder was set with
		 * {@link #setFolder(String)}, then that folder is returned.
		 */
		public String getFolder() {
			if (folder != null)
				return this.folder;
			String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
			String folder = fileName.substring(0, fileName.lastIndexOf('.'));
			return folder;
		}

		public Repo setBranch(String branch) {
			this.branch = branch;
			return this;
		}

		public String getBranch(String branch) {
			if (master.equals(branch))
				return this.branch;
			return branch;
		}
	}

	private static final String samples = "Samples";
	private static final String karaf = "Apache Karaf Distribution";
	private static final String pax = "Pax Runner Distribution (rundir)";
	public static final String superpom = "uAAL.pom";

	// name of group -> list of repos
	public static Map<String, List<Repo>> groups = new LinkedHashMap<String, List<Repo>>();

	// name of repo -> Repo
	public static Map<String, Repo> repos = new HashMap<String, Repo>();

	// the working set names: artifact ID -> working set name
	private static Map<String, String> workingSets = new HashMap<String, String>();

	// whether to use the platform aggregator repository structure with
	// submodules
	public static boolean useAgg = false;

	public static String master = "master";

	public static String[] branches = new String[] { "3.4.0", master };

	public static Repo platformRepo;

	private static void add(String group, List<Repo> lst) {
		groups.put(group, lst);
		for (Repo r : lst) {
			repos.put(r.name, r);
		}
	}

	public static boolean isRecom(String name) {
		if (samples.equals(name) || karaf.equals(name) || pax.equals(name))
			return true;
		return false;
	}

	static {
		List<Repo> repos;

		repos = new ArrayList<Repo>();
		// we assume, that the super pom is the first one in the list (used for
		// downloading of the platform repo)
		platformRepo = new Repo("uAAL Super POM", "https://github.com/universAAL/platform.git", superpom);
		repos.add(platformRepo);
		repos.add(new Repo("Middleware", "https://github.com/universAAL/middleware.git", "pom"));
		repos.add(new Repo("Ontology", "https://github.com/universAAL/ontology.git", "ont.pom"));
		repos.add(new Repo("Security and Privacy-Awareness", "https://github.com/universAAL/security.git",
				"security.pom"));
		repos.add(new Repo("Remote Interoperability", "https://github.com/universAAL/remote.git", "ri.pom"));
		repos.add(new Repo("Context", "https://github.com/universAAL/context.git", "ctxt.pom"));
		repos.add(new Repo("User Interaction", "https://github.com/universAAL/ui.git", "ui.pom"));
		repos.add(new Repo("Service", "https://github.com/universAAL/service.git", "srvc.pom"));
		repos.add(new Repo("Local Device Discovery and Integration (lddi)", "https://github.com/universAAL/lddi.git",
				"lddi.pom"));
		add("Platform", repos);

		repos = new ArrayList<Repo>();
		repos.add(new Repo(samples, "https://github.com/universAAL/samples.git", "samples.pom"));
		repos.add(new Repo("Utility Libraries", "https://github.com/universAAL/utilities.git", "utilities.pom"));
		repos.add(new Repo("Maven", "https://github.com/universAAL/maven.git", "maven.pom"));
		repos.add(new Repo("Integration Tests", "https://github.com/universAAL/itests.git", ""));
		repos.add(new Repo("Runtime Tools", "https://github.com/universAAL/tools.runtime.git", "tools.pom"));
		add("Extras", repos);

		repos = new ArrayList<Repo>();
		repos.add(new Repo(karaf, "https://github.com/universAAL/distro.karaf.git", null));
		repos.add(new Repo(pax, "https://github.com/universAAL/distro.pax.git", null).setBranch("rundir")
				.setFolder("rundir"));
		add("Distributions", repos);

		workingSets.put("mw.pom", "universAAL Middleware");
		workingSets.put("mw.pom.core", "universAAL Middleware Core");
		workingSets.put("mw.pom.osgi", "universAAL Middleware OSGi");
		workingSets.put("mw.pom.config", "universAAL Middleware Config");
		workingSets.put("ont.pom", "universAAL Ontology");
		workingSets.put("security.pom", "universAAL Security");
		workingSets.put("ri.pom", "universAAL Remote Interoperability");
		workingSets.put("ctxt.pom", "universAAL Context");
		workingSets.put("ui.pom", "universAAL User Interaction");
		workingSets.put("srvc.pom", "universAAL Service");
		workingSets.put("samples.pom", "universAAL Samples");
		workingSets.put("utilities.pom", "universAAL Utility Libraries");
		workingSets.put("maven.pom", "universAAL Maven");
		workingSets.put("itests.pom", "universAAL Integration Tests");
		workingSets.put("lddi.pom", "universAAL LDDI");
		workingSets.put("lddi.pom.bluetooth", "universAAL LDDI Bluetooth");
		workingSets.put("lddi.pom.common", "universAAL LDDI Common Components");
		workingSets.put("lddi.pom.config", "universAAL LDDI Config");
		workingSets.put("lddi.pom.fs20", "universAAL LDDI FS20");
		workingSets.put("lddi.pom.knx", "universAAL LDDI KNX");
		workingSets.put("lddi.pom.zigbee", "universAAL LDDI ZigBee");
		workingSets.put("lddi.pom.zwave", "universAAL LDDI ZWave");
		workingSets.put("tools.pom", "universAAL Runtime Tools");
	}

	public static String getWorkingSet(String artifactID) {
		return workingSets.get(artifactID);
	}
}
