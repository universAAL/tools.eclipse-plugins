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

package org.universaal.uaalpax.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.examples.util.ConsoleDependencyGraphDumper;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.universaal.uaalpax.maven.MavenDependencyResolver;
import org.universaal.uaalpax.shared.Attribute;
import org.universaal.uaalpax.versionprovider.UAALVersionProvider;

public class BundleModel {
	/** Threshold for assuming that a particular version is used */
	private static final float VERSION_WEIGHT_THRESHOLD = 0.3f;
	
	public static final String UNKNOWN_VERSION = "Unknown";
	
	private BundleSet currentBundles;
	
	private List<BundlePresenter> allPresenters = new ArrayList<BundlePresenter>();
	
	private UAALVersionProvider versionProvider;
	private String currentVersion;
	
	private ModelDialogProvider dialogProvider;
	
	private List<BundleChangeListener> changeListeners = new ArrayList<BundleChangeListener>();
	
	private ArtifactGraph artifactGraph;
	
	private ExecutorService graphExecutor;
	private Future<?> graphRebuildFuture;
	
	private boolean firestPresentersUpdate;
	
	public BundleModel(UAALVersionProvider versionProvider, ModelDialogProvider dialogProvider) {
		this.currentBundles = new BundleSet();
		this.versionProvider = versionProvider;
		this.dialogProvider = dialogProvider;
		
		this.artifactGraph = new ArtifactGraph();
		this.graphExecutor = Executors.newSingleThreadExecutor();
		this.graphRebuildFuture = null;
	}
	
	public void addChangeListener(BundleChangeListener listener) {
		if (listener == null)
			throw new NullPointerException("listener is null");
		
		changeListeners.add(listener);
	}
	
	private void notifyChanged() {
		for (BundleChangeListener l : changeListeners)
			l.notifyChanged();
	}
	
	public void addPresenter(BundlePresenter presenter) {
		allPresenters.add(presenter);
	}
	
	public void updateModel(ILaunchConfiguration configuration) {
		cancelRebuildGraph();
		
		firestPresentersUpdate = true;
		
		currentBundles.updateBundles(configuration);
		
		currentVersion = UNKNOWN_VERSION;
		
		rebuildGraphInBackground();
		
		for (String version : versionProvider.getAvailableVersions()) {
			if (containsAllBundlesOfVersion(currentBundles, version)) {
				currentVersion = version;
				break;
			}
		}
		
		updatePresenters();
	}
	
	private void rebuildGraphInforeground(BundleSet composites) {
		artifactGraph.rebuildFromSetInBackground(currentBundles);
		
		if (composites != null) {
			currentBundles.removeAll(composites.allBundles());
			for (BundleEntry comp : composites) {
				Set<BundleEntry> bes = getCompositeEntries(comp.getLaunchUrl());
				for (BundleEntry be : bes)
					insertBundleAndDepsNoWait(be);
			}
		}
	}
	
	private void rebuildGraphInBackground() {
		cancelRebuildGraph();
		
		graphRebuildFuture = graphExecutor.submit(new Runnable() {
			public void run() {
				System.out.println("rebuilding graph...");
				artifactGraph.rebuildFromSetInBackground(currentBundles);
				System.out.println("rebuilding graph finished");
			}
		});
	}
	
	private void cancelRebuildGraph() {
		if (graphRebuildFuture != null) {
			graphRebuildFuture.cancel(true);
			graphRebuildFuture = null;
		}
	}
	
	private void waitGraph() {
		if (graphRebuildFuture != null) {
			if (!graphRebuildFuture.isDone()) {
				ProgressMonitorDialog d = new ProgressMonitorDialog(dialogProvider.getShell());
				try {
					d.run(true, false, new IRunnableWithProgress() {
						
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							monitor.beginTask("Dependency graph", IProgressMonitor.UNKNOWN);
							monitor.subTask("Building dependency graph of launch config bundles...");
							
							try {
								graphRebuildFuture.get();
							} catch (InterruptedException e) {
							} catch (ExecutionException e) {
							} catch (CancellationException e) {
							}
							
							monitor.done();
						}
					});
				} catch (InvocationTargetException e1) {
				} catch (InterruptedException e1) {
				}
			}
		}
		
		graphRebuildFuture = null;
	}
	
	public void removeAll(Collection<BundleEntry> entries) {
		waitGraph();
		removeUnneededDependencies(entries);
		updatePresenters();
	}
	
	public void remove(BundleEntry be) {
		waitGraph();
		removeNoUpdate(be);
		updatePresenters();
	}
	
	public void removeNoUpdate(BundleEntry be) {
		Set<BundleEntry> bes = new HashSet<BundleEntry>();
		bes.add(be);
		waitGraph();
		removeUnneededDependencies(bes);
	}
	
	public void add(BundleEntry e) {
		waitGraph();
		insertBundleAndDeps(e);
		updatePresenters();
	}
	
	public void addAll(Collection<BundleEntry> entries) {
		waitGraph();
		for (BundleEntry be : entries)
			insertBundleAndDeps(be);
		
		notifyChanged();
		updatePresenters();
	}
	
	public BundleSet getBundles() {
		return currentBundles;
	}
	
	public void checkForComposites() {
		BundleSet composites = new BundleSet();
		for (BundleEntry be : currentBundles)
			if (be.isComposite())
				composites.add(be);
		
		if (composites.isEmpty()
				|| dialogProvider
						.openDialog(
								"Resolve composites",
								"The current run configuration contains composites, but composites aren't directly supported now.\n"
										+ "Do you want to resolve them to individual bundles instead?\n\n"
										+ "Note that if you don't resolve them, this tool could add bundle dependencies to the run config although they are already contained in a composite",
								"Yes", "No") == 1)
			composites = null;
		
		if (composites != null)
			rebuildGraphInforeground(composites);
	}
	
	private void insertBundleAndDepsNoWait(BundleEntry be) {
		if (be.isComposite()) {
			int res = dialogProvider.openDialog("Insert all bundles from composite",
					"Management of direct composite entries is not supported now\n"
							+ "Do you want to add all bundles of given composite instead?", "Yes", "Cancel");
			
			if (res == 0) {
				Set<BundleEntry> bes = getCompositeEntries(be.getLaunchUrl());
				for (BundleEntry be1 : bes)
					insertBundleAndDepsNoWait(be1);
			}
		} else if (!be.isMavenBundle()) {
			int res = dialogProvider.openDialog("Not a maven artifact",
					"Warning: the entered launch URL does not represent a maven artifact", "OK", "Cancel");
			if (res == 0)
				currentBundles.add(be);
		} else {
			while (true) {
				boolean insterted = false;
				
				try {
					Artifact a = be.toArtifact();
					try {
						DependencyNode deps = MavenDependencyResolver.getResolver().resolveDependencies(a);
						ConsoleDependencyGraphDumper dumper = new ConsoleDependencyGraphDumper();
						deps.accept(dumper);
						
						Set<ArtifactURL> depList = listDependencies(deps, null);
						String approxVersion = checkVersion(depList);
						
						if (approxVersion != null) {
							// no version is set, ask if one should be set
							if (getCurrentVersion() == UNKNOWN_VERSION) {
								int sel = dialogProvider.openDialog("Set uAAL version",
										"You have many bundles which are used by universAAL version " + approxVersion
												+ ", but the version of this run config is not set. Do you want to set it to version "
												+ approxVersion + "?", new String[] { "Yes", "Ignore" });
								
								if (sel == 0) {
									// will always go right since no version is set yet
									changeToVersion(approxVersion);
								}
							}
							// version is set and differs from approximated one
							else if (!getCurrentVersion().equals(approxVersion)) {
								int sel = dialogProvider.openDialog("Version conflict", "The bundle \"" + be.getLaunchUrl()
										+ "\" which you want to add depends on " + "uAAL version " + approxVersion
										+ ", but the current run config version is " + getCurrentVersion()
										+ ". Do you really want to add this bundle with its all depencencies?", new String[] { "No", "Yes",
										"Yes, but without depencencies" });
								
								if (sel == 0) // No
									return;
								else if (sel == 2) { // without depencencies
									currentBundles.add(be);
									return;
								} // otherwise add with all depencencies
							}
						}
						
						insertDependencies(deps /* , 1 */); // deps contains already 'be' as root, so no need to insert it second time
						
						artifactGraph.insertDependencyNode(deps); // update graph
						insterted = true;
					} catch (DependencyCollectionException e1) {
						// errors will be handled later in code
					} catch (TimeoutException e) {
						// errors will be handled later in code
						System.out.println("Resolution timeout");
					}
				} catch (UnknownBundleFormatException e2) {
					// should never happen since already checked for right formats
				}
				
				if (!insterted) {
					int ret = dialogProvider
							.openDialog("Error during depencency resolution",
									"There was an error resolving depencencies for bundle " + be.getLaunchUrl() + ". ", "Ignore", "Retry",
									"Cancel");
					
					if (ret == 0) // ignore
						currentBundles.add(be);
					else if (ret == 1) // retry
						continue;
					// else break and do nothing
				}
				
				break;
			}
		}
	}
	
	private void insertBundleAndDeps(BundleEntry be) {
		waitGraph();
		insertBundleAndDepsNoWait(be);
	}
	
	private Set<ArtifactURL> listDependencies(DependencyNode node, Set<ArtifactURL> deps) {
		if (deps == null)
			deps = new HashSet<ArtifactURL>();
		
		Dependency d = node.getDependency();
		if (d != null)
			deps.add(BundleEntry.artifactUrlFromArtifact(d.getArtifact()));
		
		for (DependencyNode child : node.getChildren())
			listDependencies(child, deps);
		
		return deps;
	}
	
	public Set<String> getAvailableVersion() {
		return versionProvider.getAvailableVersions();
	}
	
	public String getCurrentVersion() {
		return currentVersion;
	}
	
	public BundleSet getMiddlewareBundles() {
		return versionProvider.getBundlesOfVersion(currentVersion);
	}
	
	private Artifact checkCoreToOsgi(Artifact a) {
		if (a.getArtifactId().endsWith(".core") && a.getGroupId().toLowerCase().startsWith("org.universaal.middleware")) {
			// System.out.println("renaming artifact from " + a);
			Artifact osgi = new DefaultArtifact(a.getGroupId(), a.getArtifactId().substring(0, a.getArtifactId().length() - 5)
					.concat(".osgi"), a.getExtension(), a.getBaseVersion());
			osgi = MavenDependencyResolver.getResolver().resolveArtifact(osgi);
			// System.out.println("to " + osgi);
			if (osgi != null)
				return osgi;
		}
		return a;
	}
	
	/**
	 * @param node
	 * @param minStartLevel
	 *            min start level of child bundles, begin at 1 on very upper call
	 * @return start level of the node, i.e. dependent bundles have to start at a higher level
	 */
	private int insertDependencies(DependencyNode node /* , int minStartLevel */) {
		int minStartLevel = 1;
		
		Dependency d = node.getDependency();
		if (d != null) {
			Artifact a = d.getArtifact();
			a = checkCoreToOsgi(a);
			ArtifactURL url = BundleEntry.artifactUrlFromArtifact(a);
			if (url.url.contains("mw.bus.ui"))
				System.out.println("inserting" + url);
			
			// check if bundle is already included
			BundleEntry be = currentBundles.find(url);
			if (be != null) {
				minStartLevel = Math.max(minStartLevel, be.getLevel());
			} else {
				if (versionProvider.isIgnoreArtifactOfVersion(currentVersion, url))
					return minStartLevel;
				
				// traverse postorder
				for (DependencyNode child : node.getChildren())
					minStartLevel = Math.max(minStartLevel, insertDependencies(child /* , minStartLevel */));
				minStartLevel++;
				currentBundles.add(new BundleEntry(a, minStartLevel));
			}
		}
		
		return minStartLevel;
	}
	
	private void removeUnneededDependencies(Collection<BundleEntry> entries) {
		entries = new HashSet<BundleEntry>(entries);
		HashSet<BundleEntry> rawEntries = new HashSet<BundleEntry>();
		for (Iterator<BundleEntry> iter = entries.iterator(); iter.hasNext();) {
			BundleEntry be = iter.next();
			if (!be.isMavenBundle()) {
				iter.remove();
				rawEntries.add(be);
			}
		}
		
		// check for bundles which depend on those from entries
		Set<ArtifactURL> additionallyRemoved = artifactGraph.checkCanRemove(entries);
		boolean remove = true;
		
		// some bundles depend on bundles to remove, ask what to do
		if (additionallyRemoved != null && !additionallyRemoved.isEmpty()) {
			remove = false;
			BundleSet toRemove = new BundleSet();
			
			StringBuilder sb = new StringBuilder("Following bundles depend on the bundles to remove:\n\n");
			
			for (BundleEntry be : currentBundles) {
				try {
					if (additionallyRemoved.contains(be.getArtifactUrl())) {
						toRemove.add(be);
						sb.append(be.getLaunchUrl()).append("\n");
					}
				} catch (UnknownBundleFormatException e) {
				}
			}
			
			sb.append("\nHow to proceed?");
			
			int ret = dialogProvider.openDialog("Error while removing the bundles", sb.toString(), "Remove them all",
					"Ignore dependencies and remove", "Cancel");
			
			if (ret == 0) { // remove all
				entries = new HashSet<BundleEntry>(entries);
				for (BundleEntry be : toRemove)
					entries.add(be);
				remove = true;
			} else if (ret == 1) { // ignore
				remove = true;
			}
		}
		
		if (remove) {
			Set<ArtifactURL> removed = artifactGraph.removeEntries(entries, versionProvider.getBundlesOfVersion(currentVersion));
			for (Iterator<BundleEntry> iter = currentBundles.iterator(); iter.hasNext();) {
				BundleEntry be = iter.next();
				try {
					if (rawEntries.contains(be) || removed.contains(be.getArtifactUrl()) || entries.contains(be))
						iter.remove();
				} catch (UnknownBundleFormatException e) {
				}
			}
		}
	}
	
	public void updatePresenters() {
		if (firestPresentersUpdate) {
			firestPresentersUpdate = false;
			checkForComposites();
		}
		
		BundleSet projects = currentBundles;
		for (BundlePresenter presenter : allPresenters)
			projects = presenter.updateProjectList(projects);
		notifyChanged();
	}
	
	private boolean containsAllBundlesOfVersion(BundleSet launchProjects, String version) {
		BundleSet versionBundles = versionProvider.getBundlesOfVersion(version);
		if (versionBundles == null)
			return false;
		
		for (ArtifactURL url : versionBundles.allArtifactURLs())
			if (!launchProjects.containsArtifactURL(url))
				return false;
		
		return true;
	}
	
	public void changeToVersion(String newVersion) {
		// waitGraph();
		cancelRebuildGraph();
		
		BundleSet oldBS = versionProvider.getBundlesOfVersion(currentVersion);
		if (oldBS != null)
			for (BundleEntry be : oldBS)
				currentBundles.remove(be);
		
		for (Iterator<BundleEntry> iter = currentBundles.iterator(); iter.hasNext();) {
			BundleEntry be = iter.next();
			try {
				if (versionProvider.isIgnoreArtifactOfVersion(newVersion, be.getArtifactUrl()))
					iter.remove();
			} catch (UnknownBundleFormatException e) {
			}
		}
		
		// assume that the levels fit
		BundleSet newBS = versionProvider.getBundlesOfVersion(newVersion);
		if (newBS != null)
			for (BundleEntry be : newBS)
				currentBundles.add(be);
		
		currentVersion = newVersion;
		
		rebuildGraphInBackground();
		notifyChanged();
		updatePresenters();
	}
	
	public boolean checkCompatibleWithVersion(String version, ArtifactURL url) {
		BundleSet bundles = versionProvider.getBundlesOfVersion(version);
		if (bundles == null || bundles.containsArtifactURL(url)) // url is in bundles for current version
			return true;
		
		for (String v : versionProvider.getAvailableVersions()) {
			if (version.equals(v))
				continue;
			
			BundleSet otherBundles = versionProvider.getBundlesOfVersion(v);
			if (otherBundles != null && otherBundles.containsArtifactURL(url))
				return false; // bundles of an other version contains this url
								// but not bundles of current version
		}
		
		return true;
	}
	
	public Set<ArtifactURL> getIncompatibleProjects(String newVersion) {
		// find out which projects have to be checked for compatibility
		Set<ArtifactURL> toCheck = new HashSet<ArtifactURL>();
		BundleSet versionBundles = versionProvider.getBundlesOfVersion(currentVersion);
		// fill toCheck with all bundles except of those in current version set
		if (versionBundles != null) {
			for (ArtifactURL url : currentBundles.allArtifactURLs())
				if (!versionBundles.containsArtifactURL(url))
					toCheck.add(url);
		} else
			for (ArtifactURL url : currentBundles.allArtifactURLs())
				toCheck.add(url);
		
		// check all toCheck project for compatibility with new version
		for (Iterator<ArtifactURL> iter = toCheck.iterator(); iter.hasNext();)
			if (checkCompatibleWithVersion(newVersion, iter.next()))
				iter.remove();
		
		return toCheck;
	}
	
	public boolean checkCompatibleWithCurrentVersion(ArtifactURL url) {
		return checkCompatibleWithVersion(currentVersion, url);
	}
	
	private Set<BundleEntry> getCompositeEntries(LaunchURL url) {
		if (BundleEntry.isCompositeURL(url)) {
			Artifact a;
			try {
				a = BundleEntry.artifactFromURL(url);
			} catch (UnknownBundleFormatException e) {
				return new HashSet<BundleEntry>(); // should never happen due to previous checks
			}
			
			a = MavenDependencyResolver.getResolver().resolveArtifact(a);
			if (a == null)
				return new HashSet<BundleEntry>(); // should never happen due to previous checks
				
			return readArtifactsFromComposite(a.getFile());
		} else {
			Set<BundleEntry> bundles = new HashSet<BundleEntry>();
			bundles.add(new BundleEntry(url, "")); // use default settings here since a proper level will be set on bundle insertion
			return bundles;
		}
	}
	
	private Set<BundleEntry> readArtifactsFromComposite(File file) {
		Set<BundleEntry> arts = new HashSet<BundleEntry>();
		if (!file.exists() || !file.canRead())
			return arts; // TODO error message
			
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String url;
			
			while ((url = br.readLine()) != null) {
				if (!url.isEmpty())
					arts.addAll(getCompositeEntries(new LaunchURL(url)));
			}
			br.close();
		} catch (FileNotFoundException e) {
			return arts;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return arts;
		}
		
		return arts;
	}
	
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
		// finally, save arguments list
		List<String> arguments = new LinkedList<String>();
		
		try {
			List<String> prev = configuration.getAttribute(Attribute.RUN_ARGUMENTS, (List<String>) null);
			if (prev != null) {
				for (String o : prev) {
					if ((o instanceof String && ((String) o).startsWith("-")))
						arguments.add(o);
				}
			} else {
				arguments.add("--overwrite=true");
				arguments.add("--overwriteUserBundles=true");
				arguments.add("--overwriteSystemBundles=true");
				arguments.add("--log=DEBUG");
				//arguments.add("--profiles=felix.obr");
			}
		} catch (CoreException e1) {
		}
		
		Map<String, String> toSave = new HashMap<String, String>();
		for (BundleEntry be : currentBundles) {
			StringBuffer options = new StringBuffer().append(be.isSelected()).append("@").append(be.isStart()).append("@")
					.append(be.getLevel()).append("@").append(be.isUpdate());
			toSave.put(be.getLaunchUrl().url, options.toString());
			
			if (be.isSelected()) {
				final StringBuffer provisionFrom = new StringBuffer(be.getLaunchUrl().url);
				if (be.getLevel() >= 0) {
					provisionFrom.append("@").append(be.getLevel());
				}
				if (!be.isStart()) {
					provisionFrom.append("@nostart");
				}
				if (be.isUpdate()) {
					provisionFrom.append("@update");
				}
				arguments.add(provisionFrom.toString());
			}
		}
		
		configuration.setAttribute(Attribute.PROVISION_ITEMS, toSave);
		configuration.setAttribute(Attribute.RUN_ARGUMENTS, arguments);
		
		try {
			if (!configuration.hasAttribute("osgi_framework_id"))
				configuration.setAttribute("osgi_framework_id", "--platform=felix --version=4.4.1");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			trySetAttribute(configuration, "append.args", true);
			trySetAttribute(configuration, "automaticAdd", true);
			trySetAttribute(configuration, "automaticValidate", false);
			trySetAttribute(configuration, "bootstrap", "");
			trySetAttribute(configuration, "checked", "");
			trySetAttribute(configuration, "default_start_level", 40);
			trySetAttribute(configuration, "clearConfig", false);
			trySetAttribute(configuration, "configLocation", "${workspace_loc}/rundir/demo.config");
			trySetAttribute(configuration, "default", true);
			trySetAttribute(configuration, "default_auto_start", true);
			trySetAttribute(configuration, "includeOptional", true);
			trySetAttribute(configuration, "org.eclipse.debug.core.source_locator_id",
					"org.eclipse.pde.ui.launcher.PDESourceLookupDirector");
			
			trySetAttribute(configuration, "org.eclipse.jdt.launching.JRE_CONTAINER",
					"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/J2SE-1.5");
			trySetAttribute(
					configuration,
					"org.eclipse.jdt.launching.PROGRAM_ARGUMENTS",
					"-console --obrRepositories=http://depot.universaal.org/nexus/content/repositories/snapshots/repository.xml,http://depot.universaal.org/nexus/content/repositories/releases/repository.xml,http://bundles.osgi.org/obr/browse?_xml=1&amp;amp;cmd=repository --org.ops4j.pax.url.mvn.repositories=+http://depot.universaal.org/nexus/content/groups/public,http://depot.universaal.org/nexus/content/repositories/snapshots@snapshots@noreleases --log=DEBUG");
			trySetAttribute(
					configuration,
					"org.eclipse.jdt.launching.VM_ARGUMENTS",
					"-Dosgi.noShutdown=true -Dfelix.log.level=4 -Dorg.universAAL.middleware.peer.is_coordinator=true -Dorg.universAAL.middleware.peer.member_of=urn:org.universAAL.aal_space:test_env -Dbundles.configuration.location=${workspace_loc}/rundir/confadmin");
			trySetAttribute(configuration, "org.eclipse.jdt.launching.WORKING_DIRECTORY", "${workspace_loc}/rundir/demo.config");
			trySetAttribute(configuration, "org.ops4j.pax.cursor.hotDeployment", false);
			trySetAttribute(configuration, "org.ops4j.pax.cursor.logLevel", "DEBUG");
			trySetAttribute(configuration, "org.ops4j.pax.cursor.overwrite", false);
			trySetAttribute(configuration, "org.ops4j.pax.cursor.overwriteSystemBundles", false);
			trySetAttribute(configuration, "org.ops4j.pax.cursor.overwriteUserBundles", false);
			trySetAttribute(configuration, "default", true);
			trySetAttribute(configuration, "default", true);
			
			// if (!configuration.hasAttribute("org.ops4j.pax.cursor.profiles"))
			// {
			// ArrayList<String> classpath = new ArrayList<String>();
			// classpath.add("felix.obr");
			// configuration.setAttribute("org.ops4j.pax.cursor.profiles",
			// classpath);
			// }
			
			trySetAttribute(configuration, "pde.version", "3.3");
			trySetAttribute(configuration, "show_selected_only", false);
			trySetAttribute(configuration, "tracing", false);
			trySetAttribute(configuration, "useCustomFeatures", false);
			trySetAttribute(configuration, "useDefaultConfigArea", false);
			
			configuration.removeAttribute("target_bundles");
			configuration.removeAttribute("workspace_bundles");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private static void trySetAttribute(ILaunchConfigurationWorkingCopy configuration, String attribute, int value) throws CoreException {
		if (!configuration.hasAttribute(attribute))
			configuration.setAttribute(attribute, value);
	}
	
	private static void trySetAttribute(ILaunchConfigurationWorkingCopy configuration, String attribute, String value) throws CoreException {
		if (!configuration.hasAttribute(attribute))
			configuration.setAttribute(attribute, value);
	}
	
	private static void trySetAttribute(ILaunchConfigurationWorkingCopy configuration, String attribute, boolean value)
			throws CoreException {
		if (!configuration.hasAttribute(attribute))
			configuration.setAttribute(attribute, value);
	}
	
	private String checkVersion(Set<ArtifactURL> deps) {
		String maxVersion = null;
		
		float maxWeight = 0;
		for (String version : versionProvider.getAvailableVersions()) {
			float weight = versionProvider.getVersionScore(version, deps);
			if (weight > maxWeight) {
				maxWeight = weight;
				maxVersion = version;
			}
		}
		
		if (maxWeight < VERSION_WEIGHT_THRESHOLD)
			maxVersion = null;
		
		return maxVersion;
	}
}
