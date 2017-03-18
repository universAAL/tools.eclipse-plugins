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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.submodule.SubmoduleStatus;
import org.universaal.tools.envsetup.core.RepoMgmt.Repo;

/**
 * 
 * @author Carsten Stockloew
 *
 */
public class Downloader {

	private static class ProgMon implements ProgressMonitor {
		private IProgressMonitor mon;

		ProgMon(IProgressMonitor mon) {
			this.mon = mon;
		}

		@Override
		public void beginTask(String title, int totalWork) {
			// System.out.println(" - beginTask: " + title + " " +
			// totalWork);
			mon.beginTask(title, totalWork);
		}

		@Override
		public void endTask() {
			// System.out.println(" - endTask: ");
			mon.done();
		}

		@Override
		public boolean isCancelled() {
			return mon.isCanceled();
		}

		@Override
		public void start(int totalTasks) {
			// System.out.println(" - start: " + totalTasks);
		}

		@Override
		public void update(int completed) {
			// System.out.println(" - update: " + completed);
			mon.worked(completed);
		}
	};

	/**
	 * Download a complete repo -> clone
	 */
	public static void downloadRepo(String url, String branch, File localPath, final IProgressMonitor mon) {
		// File localPath = new File("D:\\temp\\Git");
		// localPath.delete();
		// System.out.println("Cloning from " + url + " to " + localPath);
		try {
			Git result = Git.cloneRepository().setURI(url).setDirectory(localPath).setProgressMonitor(new ProgMon(mon))
					.setBranch(branch).call();
					// Note: the call() returns an opened repository already
					// which needs to be closed to avoid file handle leaks!
					// System.out.println("Having repository: " +
					// result.getRepository().getDirectory());

			// workaround for
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=474093
			result.getRepository().close();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Donwload a submodule of the platform aggregator project.
	 * 
	 * @param r
	 *            the repo
	 * @param branch
	 *            the branch name
	 * @param localPath
	 *            the local path to the platform repo, e.g.
	 *            "C:\\universAAL\\ws\\platform"
	 * @param mon
	 *            a progress monitor
	 * @return the relative path of the submodule, e.g. for "samples", it
	 *         returns "xtras\samples"
	 */
	public static String downloadSubmodule(Repo r, String branch, File localPath, final IProgressMonitor mon) {
		String ret = null;
		Git git = null;
		try {
			git = Git.open(localPath);
		} catch (IOException e1) {
			e1.printStackTrace();
			return ret;
		}

		// Submodules
		Map<String, SubmoduleStatus> submodules = getSubmodules(git);

		// init, update, pull
		File workDir = git.getRepository().getWorkTree();
		// System.out.println(" -- workDir: " + workDir);
		for (SubmoduleStatus mod : submodules.values()) {
			try {
				// System.out.println(" -- init " + mod.getPath());
				if (mod.getPath().endsWith(r.getFolder())) {
					git.submoduleInit().addPath(mod.getPath()).call();
					git.submoduleUpdate().addPath(mod.getPath()).call();

					Git libModule = Git.open(new File(workDir, ".git/modules/" + mod.getPath()));
					if (branch == null)
						branch = "master";
					libModule.checkout().setName(branch).call();
					//libModule.checkout().call();
					libModule.pull().call();
					libModule.close();
					ret = mod.getPath();
					break;
				}
			} catch (GitAPIException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		git.getRepository().close();
		return ret;
	}

	private static Map<String, SubmoduleStatus> getSubmodules(Git git) {
		Map<String, SubmoduleStatus> submodules = null;
		try {
			submodules = git.submoduleStatus().call();
			// for (String s : submodules.keySet()) {
			// SubmoduleStatus status = submodules.get(s);
			// System.out.println("submodule " + s);
			// System.out.println(" Path: " + status.getPath());
			// System.out.println(" HeadId: " + status.getHeadId());
			// System.out.println(" IndexId: " + status.getIndexId());
			// System.out.println(" Type: " + status.getType());
			// }
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}

		if (submodules == null)
			submodules = new HashMap<String, SubmoduleStatus>();
		return submodules;
	}
}
