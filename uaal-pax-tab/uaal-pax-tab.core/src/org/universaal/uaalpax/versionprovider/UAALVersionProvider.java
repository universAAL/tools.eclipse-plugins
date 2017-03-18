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

package org.universaal.uaalpax.versionprovider;

import java.util.Collection;
import java.util.Set;

import org.universaal.uaalpax.model.ArtifactURL;
import org.universaal.uaalpax.model.BundleSet;


/**
 * Uses to determine available uAAL middleware versions and the needed bundles for those versions.
 * 
 * @author Mark Prediger
 */
public interface UAALVersionProvider {
	/**
	 * Do not modify the returned set!!!
	 * 
	 * @return Set with all available middleware versions, e.g. [ "1.1.1", "1.1.2", ...]. Never null.
	 */
	public Set<String> getAvailableVersions();
	
	/**
	 * Do not modify the returned set!!!
	 * 
	 * @param version
	 *            version string of uAAL middleware
	 * @return Set of needed bundles as ProjectURLs or null if no entry for given version exists.
	 */
	public BundleSet getBundlesOfVersion(String version);
	
	/**
	 * Do not modify the returned set!!!
	 * 
	 * @param version
	 *            version of uAAL middleware
	 * @return Set with all available features for given middleware version, e.g. [ "CHE", "UI-Framework", ...]. Never null.
	 */
	public Set<String> getAdditionalFeatures(String version);
	
	/**
	 * Do not modify the returned set!!!
	 * 
	 * @param version
	 *            version of uAAL middleware
	 * @param feature
	 *            feature
	 * @return Set of needed bundles as ProjectURLs or null if no entry for given version exists.
	 */
	public BundleSet getBundlesOfFeature(String version, String feature);
	
	/**
	 * Checks whether given bundle should be ignored (-> not added to the run config since it is not necessary) using given middleware version.
	 * 
	 * @param artifactUrl launch url
	 * @param version middleware version
	 * @return true if bundle should be ignored, otherwise false
	 */
	public boolean isIgnoreArtifactOfVersion(String version, ArtifactURL artifactUrl);
	
	/**
	 * Computes how probably the given bundle urls represent a run config of a certain uaal version.
	 * 
	 * @param version version to check against
	 * @param urls set of bundle urls in run config
	 * @return version hit score for given version, between 0.0 and 1.0
	 */
	public float getVersionScore(String version, Collection<ArtifactURL> urls);
}
