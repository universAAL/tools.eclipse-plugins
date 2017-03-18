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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.universaal.uaalpax.shared.Attribute;

public class BundleSet implements Iterable<BundleEntry> {
	private Map<Object, BundleEntry> bundles; // String is either artifact url or launch url if an artifact url could not be parsed
	
	public BundleSet() {
		bundles = new HashMap<Object, BundleEntry>();
	}
	
	/*
	 * public BundleSet(Map<ArtifactURL, BundleEntry> bundles) { this.bundles = new HashMap<String, BundleEntry>(); for
	 * (Map.Entry<ArtifactURL, BundleEntry> e : bundles.entrySet()) this.bundles.put(e.getKey(), e.getValue()); }
	 */
	
	public BundleSet(ILaunchConfiguration configuration) {
		updateBundles(configuration);
	}
	
	public BundleSet(BundleSet bundleset) {
		this.bundles = new HashMap<Object, BundleEntry>(bundleset.bundles);
	}
	
	public Iterator<BundleEntry> iterator() {
		return bundles.values().iterator();
	}
	
	public void updateBundles(ILaunchConfiguration configuration) {
		bundles = new HashMap<Object, BundleEntry>();
		try {
			Map<String, String> launch = configuration.getAttribute(Attribute.PROVISION_ITEMS, new HashMap<String, String>());
			
			for (Map.Entry<String, String> e : launch.entrySet()) {
				BundleEntry be = new BundleEntry(new LaunchURL(e.getKey()), e.getValue());
				try {
					bundles.put(be.getArtifactUrl(), be);
				} catch (UnknownBundleFormatException e1) {
					bundles.put(be.getLaunchUrl(), be);
				}
			}
		} catch (CoreException e) {
		}
	}
	
	public BundleSet add(BundleEntry e) {
		try {
			bundles.put(e.getArtifactUrl(), e);
		} catch (UnknownBundleFormatException e1) {
			bundles.put(e.getLaunchUrl(), e);
		}
		
		return this;
	}
	
	public Collection<BundleEntry> allBundles() {
		return bundles.values();
	}
	
	public boolean containsArtifactURL(ArtifactURL url) {
		return bundles.containsKey(url);
	}
	
	public boolean containsBundle(BundleEntry be) {
		try {
			return bundles.containsKey(be.getArtifactUrl());
		} catch (UnknownBundleFormatException e) {
			return bundles.containsKey(be.getLaunchUrl());
		}
	}
	
	public BundleEntry find(ArtifactURL url) {
		return bundles.get(url);
	}
	
	public Set<ArtifactURL> allArtifactURLs() {
		Set<ArtifactURL> urls = new HashSet<ArtifactURL>();
		for (Object o : bundles.keySet())
			if (o instanceof ArtifactURL)
				urls.add((ArtifactURL) o);
		
		return urls;
	}
	
	public int size() {
		return bundles.size();
	}
	
	public boolean isEmpty() {
		return bundles.isEmpty();
	}
	
	public boolean remove(BundleEntry be) {
		try {
			return bundles.remove(be.getArtifactUrl()) != null;
		} catch (UnknownBundleFormatException e) {
			return bundles.remove(be.getLaunchUrl()) != null;
		}
	}
	
	@Override
	public String toString() {
		return bundles.toString();
	}
	
	public void removeAll(Collection<BundleEntry> entries) {
		for (BundleEntry e : entries)
			remove(e);
	}
	
	public void addAll(Collection<BundleEntry> entries) {
		for (BundleEntry e : entries)
			add(e);
	}
	
	public void updateBundleOptions(BundleEntry be) {
		Object url;
		try {
			url = be.getArtifactUrl();
		} catch (UnknownBundleFormatException e) {
			url = be.getLaunchUrl();
		}
		
		if (!bundles.containsKey(url)) // to be sure
			throw new IllegalArgumentException("can only update options of already existing bundle");
		
		bundles.put(url, be); // be is immutable, so its ok
	}

	public void clear() {
		bundles.clear();
	}
}
