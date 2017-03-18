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

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.universaal.uaalpax.maven.MavenDependencyResolver;

public class BundleEntry {
	public static final String PROP_PROJECT = "Project";
	public static final String PROP_LEVEL = "Level";
	
	public static int DEFAULT_STARTLEVEL = -1;
	
	private final LaunchURL launchUrl;
	private boolean selected;
	private boolean start;
	private int level;
	private boolean update;
	private final String projectName;
	
	public static LaunchURL launchUrlFromArtifact(Artifact a) {
		String url = "mvn:" + a.getGroupId() + "/" + a.getArtifactId() + "/" + a.getBaseVersion();
		if (MavenDependencyResolver.getResolver().isWrapArtifact(a))
			url = "wrap:" + url;
		return new LaunchURL(url);
	}
	
	public static ArtifactURL artifactUrlFromArtifact(Artifact a) {
		return new ArtifactURL(a.getGroupId() + "/" + a.getArtifactId() + "/" + a.getBaseVersion());
	}
	
	public static Artifact artifactFromURL(LaunchURL url) throws UnknownBundleFormatException {
		url = removeScanBundle(url);
		
		int mvn = url.url.indexOf("mvn:");
		if (mvn >= 0) {
			String[] s = url.url.substring(mvn + 4).split("/");
			
			String ext = "jar";
			if (url.url.contains("scan-composite:"))
				ext = "composite";
			
			if (s.length == 2)
				return new DefaultArtifact(s[0], s[1], ext, null);
			else if (s.length == 3)
				return new DefaultArtifact(s[0], s[1], ext, s[2]);
			else if (s.length == 4)
				return new DefaultArtifact(s[0], s[1], s[3], s[2]);
		}
		
		throw new UnknownBundleFormatException("unknown format of " + url);
	}
	
	private static LaunchURL removeScanBundle(LaunchURL url) {
		return new LaunchURL(url.url.replaceAll("scan-bundle:", ""));
	}
	
	public static boolean isCompositeURL(LaunchURL url) {
		return url.url.contains("scan-composite:");
	}
	
	public BundleEntry(LaunchURL url, String projectName, int startLevel, boolean update) {
		this.launchUrl = removeScanBundle(url);
		this.selected = true;
		this.start = true;
		this.level = startLevel;
		this.update = update;
		this.projectName = projectName;
	}
	
	public BundleEntry(LaunchURL url, boolean selected, boolean start, int startLevel, boolean update) {
		this.launchUrl = removeScanBundle(url);
		this.selected = selected;
		this.start = start;
		this.level = startLevel;
		this.update = update;
		this.projectName = null;
	}
	
	public BundleEntry(LaunchURL url, String settingsStr) {
		this.launchUrl = removeScanBundle(url);
		
		String[] settings = settingsStr.split("@");
		int level = -1;
		boolean update = true;
		boolean start = true;
		boolean selected = true;
		if (settings.length == 4) {
			try {
				selected = Boolean.parseBoolean(settings[0]);
				start = Boolean.parseBoolean(settings[1]);
				try {
					level = Integer.parseInt(settings[2]);
				} catch (NumberFormatException e) {
					level = -1;
				}
				update = Boolean.parseBoolean(settings[3]);
			} catch (NumberFormatException e) {
			}
		}
		
		this.level = level;
		this.update = update;
		this.start = start;
		this.selected = selected;
		this.projectName = null;
	}
	
	public BundleEntry(Artifact a) {
		this.launchUrl = launchUrlFromArtifact(a);
		this.selected = true;
		this.start = true;
		this.level = DEFAULT_STARTLEVEL;
		this.update = true;
		this.projectName = this.launchUrl.url;
	}
	
	public BundleEntry(Artifact a, int level) {
		this.launchUrl = launchUrlFromArtifact(a);
		this.selected = true;
		this.start = true;
		this.level = level;
		this.update = true; 
		this.projectName = this.launchUrl.url;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void setStart(boolean start) {
		this.start = start;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setUpdate(boolean update) {
		this.update = update;
	}
	
	public boolean isComposite() {
		return isCompositeURL(getLaunchUrl());
	}
	
	public Artifact toArtifact() throws UnknownBundleFormatException {
		return artifactFromURL(getLaunchUrl());
	}
	
	public String getOptions() {
		return String.valueOf(selected) + "@" + String.valueOf(start) + "@" + (level < 0 ? "default" : String.valueOf(level)) + "@"
				+ String.valueOf(update);
	}
	
	public BundleEntry(String projectName, LaunchURL url, String settingsStr) {
		this.launchUrl = url;
		
		String[] settings = settingsStr.split("@");
		int level = DEFAULT_STARTLEVEL;
		boolean update = true;
		boolean start = true;
		boolean selected = true;
		if (settings.length == 4) {
			try {
				selected = Boolean.parseBoolean(settings[0]);
				start = Boolean.parseBoolean(settings[1]);
				try {
					level = Integer.parseInt(settings[2]);
				} catch (NumberFormatException e) {
					level = DEFAULT_STARTLEVEL;
				}
				update = Boolean.parseBoolean(settings[3]);
			} catch (NumberFormatException e) {
			}
		}
		this.level = level;
		this.update = update;
		this.start = start;
		this.selected = selected;
		this.projectName = projectName;
	}
	
	public ArtifactURL getArtifactUrl() throws UnknownBundleFormatException {
		return artifactUrlFromArtifact(toArtifact());
	}
	
	public String getProjectName() {
		if (projectName != null)
			return projectName;
		else
			return launchUrl.url;
	}
	
	public LaunchURL getLaunchUrl() {
		return launchUrl;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public boolean isStart() {
		return start;
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean isUpdate() {
		return update;
	}
	
	public boolean equalsURL(BundleEntry other) {
		return getLaunchUrl().equals(other.getLaunchUrl());
	}
	
	public boolean isMavenBundle() {
		return launchUrl.url.replace("wrap:", "").contains("mvn:") && launchUrl.url.replace("wrap:", "").lastIndexOf(':') == 3;
	}
	
	/**
	 * Note: bundles are equal if and only if they have same launch urls. Start level etc. doesn't count.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof BundleEntry))
			return false;
		
		BundleEntry pu = (BundleEntry) obj;
		return this.getLaunchUrl().equals(pu.getLaunchUrl());
//		return this.getLevel() == pu.getLevel() && this.getLaunchUrl().equals(pu.getLaunchUrl()) && this.isSelected() == pu.isSelected()
//				&& this.isStart() == pu.isStart() && this.isUpdate() == pu.isUpdate();
	}
	
	@Override
	public int hashCode() {
		return launchUrl.hashCode();
		
//		int hashcode = 1;
//		hashcode *= 13 * getLevel();
//		hashcode *= 23 * getLaunchUrl().hashCode();
//		hashcode *= 17 * (isSelected() ? 1 : 3);
//		hashcode *= 11 * (isUpdate() ? 1 : 3);
//		hashcode *= 19 * (isStart() ? 1 : 3);
//		return hashcode;
	}
	
	@Override
	public String toString() {
		return "[BundleEntry: " + getProjectName() + " | " + isStart() + " | " + getLevel() + " | " + isUpdate() + "]";
	}
}
