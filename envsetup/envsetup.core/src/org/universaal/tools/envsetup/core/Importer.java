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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.internal.ui.workingsets.WorkingSetComparator;
import org.eclipse.jdt.internal.ui.workingsets.WorkingSetModel;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.IMavenProjectImportResult;
//import org.eclipse.m2e.core.project.IMavenProjectImportResult;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.universaal.tools.envsetup.core.RepoMgmt.Repo;

/**
 * 
 * @author Carsten Stockloew
 *
 */
@SuppressWarnings("restriction")
public class Importer {

	// the projects that should be closed afterwards (i.e. karaf.feature)
	private List<IProject> projectsToClose;

	// all imported projects
	private List<IProject> allProjects;

	public void perform(Repo r, String branch, String dirBase, final IProgressMonitor monitor) {
		projectsToClose = new ArrayList<IProject>();
		allProjects = new ArrayList<IProject>();

		File dir = perform_Download(r, branch, dirBase, monitor);
		if (dir == null)
			return;
		perform_Import(r, dir, monitor);
	}

	private File perform_Download(Repo r, String branch, String dirBase, final IProgressMonitor monitor) {
		// download
		// ---------
		if (r.isPlatformRepo() && r != RepoMgmt.platformRepo) {
			// make sure that the platform aggregator project already exists
			File dir = new File(dirBase, RepoMgmt.platformRepo.getFolder());
			if (!dir.exists())
				perform_Download(RepoMgmt.platformRepo, branch, dirBase, monitor);

			// now download the submodule
			String subdir = Downloader.downloadSubmodule(r, r.getBranch(branch), dir, monitor);
			return new File(dir, subdir);
		} else {
			String folder = r.getFolder();
			File dir = new File(dirBase, folder);
			if (dir.exists()) {
				if (!renameRundir(dirBase, folder)) {
					err("Folder '" + dir + "' already exists - skip downloading repository " + r.name);
					return dir;
				}
			}

			// create folder and download from git
			if (!dir.mkdirs()) {
				if (!dir.exists()) {
					err("Folder '" + dir + "' could not be created - skip repository " + r.name);
					return null;
				}
			}

			// download via git
			out("Downloading universAAL source from " + r.url + " to " + dir.toString());
			try {
				Downloader.downloadRepo(r.url, r.getBranch(branch), dir, monitor);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return dir;
		}
	}

	private void perform_Import(Repo r, File dir, final IProgressMonitor monitor) {
		if (r.pom != null) {
			// import projects in eclipse
			// ---------------------------
			// sets: working set -> List of artifactID (= project name)
			Map<String, List<String>> sets = new HashMap<String, List<String>>();
			// projects to import
			List<MavenProjectInfo> projects = new ArrayList<MavenProjectInfo>();
			readPom(new File(dir, r.pom), "", "", sets, projects);

			IProjectConfigurationManager cm = MavenPlugin.getProjectConfigurationManager();
			ProjectImportConfiguration conf = new ProjectImportConfiguration();
			try {
				List<IMavenProjectImportResult> reslst = cm.importProjects(projects, conf, monitor);
				for (IMavenProjectImportResult res : reslst) {
					IProject proj = res.getProject();
					if (proj != null) {
						allProjects.add(proj);
					} else {
						// System.out.println("Importer ERROR: project in
						// IMavenProjectImportResult is null.");
					}
				}

				// // close the project for karaf feature
				// for (List<String> set : sets.values()) {
				// for (String artifactID : set) {
				// if (artifactID.endsWith(".karaf.feature")) {
				// IWorkspaceRoot root =
				// ResourcesPlugin.getWorkspace().getRoot();
				// final IProject proj = root.getProject(artifactID);
				// if (proj != null) {
				// if (proj.isOpen()) {
				// projectsToClose.add(proj);
				// // PlatformUI.getWorkbench().getDisplay().syncExec(new
				// // Runnable() {
				// // public void run() {
				// // try {
				// // proj.close(new NullProgressMonitor());
				// // } catch (CoreException e) {
				// // e.printStackTrace();
				// // }
				// // }
				// // });
				// }
				// }
				// }
				// }
				// }
			} catch (CoreException e) {
				e.printStackTrace();
				return;
			}

			// working sets
			// -------------
			try {
				manageWorkingSets(sets);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Special case for rundir: if the folder is the pax rundir (which might
	 * have been created by another tool), but not a git repo, then rename the
	 * folder.
	 * 
	 * @param dirBase
	 *            the base dir
	 * @param folder
	 *            the folder (e.g. 'rundir'), must exist
	 * @return true, if the folder is the rundir and was renamed
	 */
	private boolean renameRundir(String dirBase, String folder) {
		if (!"rundir".equals(folder))
			return false;
		if (isRepo(new File(dirBase, folder)))
			return false;

		// rename
		File dir;
		int i = 0;
		String name;
		do {
			name = folder + ".old" + i;
			dir = new File(dirBase, name);
			if (!dir.exists())
				break;
			i++;
		} while (true);

		boolean res = false;

		for (i = 1; i < 11; i++) {
			res = new File(dirBase, folder).renameTo(dir);
			if (res)
				break;

			err("Renaming folder 'rundir' (as backup) to '" + name + "' failed. Retry " + i + "/" + 10);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (!res) {
			err("The folder 'rundir' exists but is not a git repo. Renaming it (as backup) to '" + name + "' failed. "
					+ "Please rename the folder manually and try again.");
		}

		return true;
	}

	private boolean isRepo(File dir) {
		// the dir must exist
		if (!dir.exists())
			return false;
		// there must be a file or subdir in that dir named ".git"
		dir = new File(dir, ".git");
		return dir.exists();
	}

	public List<IProject> getProjectsToClose() {
		return projectsToClose;
	}

	public List<IProject> getAllProjects() {
		return allProjects;
	}

	private void manageWorkingSets(final Map<String, List<String>> sets) {
		// get the package explorer (is there a better way?)
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkingSetManager manager = workbenchWindow.getWorkbench().getWorkingSetManager();
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

				IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getViewReferences();
				for (IViewReference view : views) {
					if ("org.eclipse.jdt.ui.PackageExplorer".equals(view.getId())) {
						PackageExplorerPart pe = (PackageExplorerPart) view.getView(false);
						// System.out.println(" -- found PackageExplorerPart: "
						// + pe);

						// This call will switch the view to
						// 'working set' as top level elements.
						// This is needed to create the
						// WorkingSetModel which, otherwise, is null
						// FIXME: when called the 1st time it may
						// also show up a dialog where the user has
						// to select which sets to show
						pe.rootModeChanged(PackageExplorerPart.WORKING_SETS_AS_ROOTS);

						WorkingSetModel wsm = pe.getWorkingSetModel();
						// System.out.println(" -- found WorkingSetModel: " +
						// wsm);

						IWorkingSet allsets[] = wsm.getAllWorkingSets();

						for (String setName : sets.keySet()) {
							if ("".equals(setName))
								continue;
							// get or create the working set
							IWorkingSet set = null;
							for (int i = 0; i < allsets.length; i++) {
								if (setName.equals(allsets[i].getName())) {
									set = allsets[i];
									break;
								}
							}
							// System.out.println(" -- working set search: " +
							// set);

							if (set == null) {
								// System.out.println(" -- creating working set:
								// " +
								// setName);
								set = manager.createWorkingSet(setName, new IAdaptable[0]);
								set.setLabel(setName);
								manager.addWorkingSet(set);

								allsets = wsm.getAllWorkingSets();
								boolean isSortingEnabled = wsm.isSortingEnabled();
								IWorkingSet[] activesets = wsm.getActiveWorkingSets();

								IWorkingSet[] new_allsets = new IWorkingSet[allsets.length + 1];
								IWorkingSet[] new_activesets = new IWorkingSet[activesets.length + 1];
								add(allsets, activesets, set, new_allsets, new_activesets);

								try {
									wsm.setWorkingSets(new_allsets, isSortingEnabled, new_activesets);
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
									System.out.println(" ---------\nAdding working set: " + set.getName() + " sorting: "
											+ isSortingEnabled);
									out("previous all sets:", allsets);
									out("previous active sets", activesets);
									out("new all sets:", new_allsets);
									out("new active sets", new_activesets);

									// something went wrong .. again
									// sort the working sets and try again
									try {
										System.out.println("Trying to add a sorted array..");
										Arrays.sort(new_allsets, new WorkingSetComparator(true));
										Arrays.sort(new_activesets, new WorkingSetComparator(true));
										wsm.setWorkingSets(new_allsets, isSortingEnabled, new_activesets);
									} catch (IllegalArgumentException ex) {
										// giving up..
										throw ex;
									}
								}
							}

							// add all artifacts to the working set
							List<String> artifacts = sets.get(setName);
							for (String artifactID : artifacts) {
								final IProject proj = root.getProject(artifactID);
								manager.addToWorkingSets(proj, new IWorkingSet[] { set });
							}
						}
						break;
					}
				}
			}
		});
	}

	private void out(String label, IWorkingSet[] arr) {
		System.out.println(label);
		for (IWorkingSet s : arr)
			System.out.println(" - " + s.getName());
	}

	/**
	 * Helper method to add a working set to an array of working sets. The new
	 * set is sorted if the given one was sorted.
	 */
	private void add(IWorkingSet[] all, IWorkingSet[] active, IWorkingSet el, IWorkingSet[] newall,
			IWorkingSet[] newactive) {
		// first: add el into active set
		// store the element after
		IWorkingSet after = null;
		int i = 0;
		if (active.length != 0) {
			IWorkingSet set = active[0];
			while (set.getName().compareTo(el.getName()) < 0) {
				newactive[i] = active[i];
				i++;
				if (i == active.length)
					break;
				set = active[i];
			}
		}
		if (i < active.length)
			after = active[i];
		newactive[i] = el;
		i++;
		for (int j = i; j < newactive.length; j++) {
			newactive[j] = active[j - 1];
		}

		// second: add el to the 'all' set right before 'after'
		if (after == null) {
			// special case: add el at the end of the array
			for (i = 0; i < all.length; i++)
				newall[i] = all[i];
			newall[newall.length - 1] = el;
		} else {
			// we have at least one element in 'all', otherwise 'after' would be
			// null
			i = 0;
			while (!all[i].equals(after)) {
				newall[i] = all[i];
				i++;
			}
			newall[i] = el;
			i++;
			for (int j = i; j < newall.length; j++) {
				newall[j] = all[j - 1];
			}
		}
	}

	// public void perform(File dir) {
	// try {
	// log("Starting import of maven projects from directory " +
	// dir.getCanonicalPath());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// importFile(dir, null, null);
	// }

	private void out(String msg) {
		System.out.println(msg);
		// Activator.log(msg);
	}

	private void err(String msg) {
		System.err.println(msg);
	}

	private void readPom(File dir, String parentGroupID, String parentWorkingSet, Map<String, List<String>> sets,
			List<MavenProjectInfo> projects) {
		// sets: working set -> List of artifactID (= project name)
		// System.out.println(" -- read pom: " + dir.toString());

		// load file
		final IMaven maven = MavenPlugin.getMaven();
		Model model = null;
		File file = null;
		try {
			file = new File(dir, "pom.xml");
			// log(file.toString());
			model = maven.readModel(file);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (model == null) {
			return;
		}

		// get basic infos (artifactID, groupID, workingSet)
		// FIXME: without using reflection I get a
		// java.lang.LinkageError
		// previously initiated loading for a different type with
		// name "org/apache/maven/model/Model"
		// System.out.println(" -- " + model.getGroupId() + ":" +
		// model.getArtifactId());
		String groupID = null;
		String artifactID = null;
		try {
			groupID = (String) model.getClass().getMethod("getGroupId").invoke(model);
			artifactID = (String) model.getClass().getMethod("getArtifactId").invoke(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (groupID == null)
			groupID = parentGroupID;
		// Get the working set from pre-defined set of names. We could also get
		// the name as property from the pom file, but this property is not yet
		// available, and is not in the old releases. However, the method is
		// implemented and works, so we could switch in the future.
		// String workingSet = getWorkingSet(model);
		String workingSet = RepoMgmt.getWorkingSet(artifactID);
		if (workingSet == null)
			workingSet = parentWorkingSet;
		out("Found artifact " + groupID + ":" + artifactID + " for working set "
				+ (workingSet == null ? "Other Projects" : workingSet));

		// store project info for later importing: working sets
		// if the project is already imported and available in Eclipse, we will
		// not move it to the working set defined in the pom file, because we
		// assume that it was already moved by the user for a reason.
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject proj = root.getProject(artifactID);
		if (proj != null) {
			String wstmp = workingSet;
			if (wstmp == null)
				wstmp = "";
			List<String> lst = sets.get(wstmp);
			if (lst == null) {
				lst = new ArrayList<String>();
				sets.put(wstmp, lst);
			}
			lst.add(artifactID);
		}

		// store project info for later importing: projects to import
		MavenProjectInfo info = new MavenProjectInfo("label", file, model, null);
		projects.add(info);

		// process modules (only if not super pom)
		if (!RepoMgmt.superpom.equals(artifactID)) {
			List<String> modules = null;
			try {
				modules = (List<String>) model.getClass().getMethod("getModules").invoke(model);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (modules != null) {
				for (String m : modules) {
					// System.out.println(" -- module: " + m);
					readPom(new File(dir, m), groupID, workingSet, sets, projects);
				}
			}
		}
	}

	/**
	 * Get the working set from the properties of a pom file, or null if not
	 * defined.
	 */
	private String getWorkingSet(Model model) {
		Properties props = null;

		try {
			props = (Properties) model.getClass().getMethod("getProperties").invoke(model);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (props != null) {
			return (String) props.get("workingSet");
			// for (Object o : props.keySet()) {
			// System.out.println(" -- prop: " + o + " - " + props.get(o));
			// }
		}

		return null;
	}
}
