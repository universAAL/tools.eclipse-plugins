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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.universaal.uaalpax.model.ArtifactURL;
import org.universaal.uaalpax.model.BundleEntry;
import org.universaal.uaalpax.model.BundleSet;
import org.universaal.uaalpax.model.LaunchURL;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLVersionProvider implements UAALVersionProvider {
	private static String PROP_VERSIONS = "versions";
	
	private static final String TAG_VERSION = "version";
	private static final String TAG_MIDDLEWARE = "middleware";
	private static final String TAG_BUNDLESET = "bundleset";
	private static final String TAG_BUNDLE = "bundle";
	private static final String TAG_FEATURES = "features";
	private static final String TAG_RELEVANT = "relevant";
	private static final String TAG_INGNORE = "ignore";
	private static final String TAG_ARTIFACTSET = "artifactset";
	private static final String TAG_ARTIFACT = "artifact";
	
	private static final String ATTR_LEVEL = "level";
	private static final String ATTR_NAME = "name";
	
	private Map<String, BundleSet> middlewares = new HashMap<String, BundleSet>();
	private Map<String, Map<String, BundleSet>> features = new HashMap<String, Map<String, BundleSet>>();
	private Map<String, Set<ArtifactURL>> versionSegnificantURLs = new HashMap<String, Set<ArtifactURL>>();
	private Map<String, Set<ArtifactURL>> ignoreSet = new HashMap<String, Set<ArtifactURL>>();
	
	public XMLVersionProvider() {
		Bundle bundle = Platform.getBundle("org.universaal.tools.uaal.runner.ui");
		URL fileURL = FileLocator.find(bundle, new Path("versions/versions.properties"), null);
		try {
			Properties props = new Properties();
			InputStream in = fileURL.openStream();
			props.load(in);
			in.close();
			
			String version_string = props.getProperty(PROP_VERSIONS);
			if (version_string != null) {
				String[] versions = version_string.split(" ");
				for (String v : versions) {
					if (!v.isEmpty()) {
						try {
							loadVersion(v, FileLocator.find(bundle, new Path("versions/" + v + ".xml"), null));
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (ClassCastException e) {
							e.printStackTrace();
						} catch (DOMException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadVersion(String version, URL url) throws IOException, SAXException, ParserConfigurationException {
		if (url == null) {
			System.err.println("could not find configuration file for middleware version " + version);
			return;
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url.openStream());
		
		Element root = (Element) doc.getElementsByTagName(TAG_VERSION).item(0);
		
		NodeList bundlesNodes = ((Element) ((Element) root.getElementsByTagName(TAG_MIDDLEWARE).item(0))
				.getElementsByTagName(TAG_BUNDLESET).item(0)).getElementsByTagName(TAG_BUNDLE);
		
		BundleSet bs = new BundleSet();
		loadBundles(bundlesNodes, bs);
		middlewares.put(version, bs);
		
		final Map<String, BundleSet> featureSets = new HashMap<String, BundleSet>();
		NodeList featureList = ((Element) root.getElementsByTagName(TAG_FEATURES).item(0)).getElementsByTagName(TAG_BUNDLESET);
		traverseElements(featureList, new ElementTraverser() {
			public void traverse(Element set) {
				String featureName = set.getAttribute(ATTR_NAME);
				if (featureName.isEmpty()) {
					// TODO print warning message
					return;
				}
				
				NodeList bundles = set.getElementsByTagName(TAG_BUNDLE);
				BundleSet featureBundles = new BundleSet();
				loadBundles(bundles, featureBundles);
				featureSets.put(featureName, featureBundles);
			}
		});
		features.put(version, featureSets);
		
		final Set<ArtifactURL> relevant = new HashSet<ArtifactURL>();
		NodeList relevantBundles = ((Element) ((Element) root.getElementsByTagName(TAG_RELEVANT).item(0)).getElementsByTagName(
				TAG_ARTIFACTSET).item(0)).getElementsByTagName(TAG_ARTIFACT);
		
		traverseElements(relevantBundles, new ElementTraverser() {
			
			public void traverse(Element e) {
				relevant.add(new ArtifactURL(e.getTextContent()));
			}
		});
		versionSegnificantURLs.put(version, relevant);
		
		final Set<ArtifactURL> ignore = new HashSet<ArtifactURL>();
		NodeList irgnoreBundles = ((Element) ((Element) root.getElementsByTagName(TAG_INGNORE).item(0)).getElementsByTagName(
				TAG_ARTIFACTSET).item(0)).getElementsByTagName(TAG_ARTIFACT);
		traverseElements(irgnoreBundles, new ElementTraverser() {
			
			public void traverse(Element e) {
				ignore.add(new ArtifactURL(e.getTextContent()));
			}
		});
		ignoreSet.put(version, ignore);
	}
	
	private void loadBundles(NodeList bundlesNodes, final BundleSet bs) {
		traverseElements(bundlesNodes, new ElementTraverser() {
			public void traverse(Element bundle) {
				LaunchURL url = new LaunchURL(bundle.getTextContent());
				int level = -1;
				try {
					level = Integer.parseInt(bundle.getAttribute(ATTR_LEVEL));
				} catch (NumberFormatException e) {
				}
				
				bs.add(new BundleEntry(url, true, true, level, false));
			}
		});
	}
	
	public Set<String> getAvailableVersions() {
		return middlewares.keySet();
	}
	
	public BundleSet getBundlesOfVersion(String version) {
		return middlewares.get(version);
	}
	
	public Set<String> getAdditionalFeatures(String version) {
		Map<String, BundleSet> f = features.get(version);
		if (f == null)
			return new HashSet<String>();
		else
			return f.keySet();
	}
	
	public BundleSet getBundlesOfFeature(String version, String feature) {
		Map<String, BundleSet> f = features.get(version);
		if (f == null)
			return null;
		else
			return f.get(feature);
	}
	
	public boolean isIgnoreArtifactOfVersion(String version, ArtifactURL artifactUrl) {
		Set<ArtifactURL> ignore = ignoreSet.get(version);
		if (ignore == null)
			return false;
		
		for (ArtifactURL i : ignore)
			if (artifactUrl.url.startsWith(i.url))
				return true;
		
		return false;
	}
	
	public float getVersionScore(String version, Collection<ArtifactURL> urls) {
		Set<ArtifactURL> vsu = versionSegnificantURLs.get(version);
		if (vsu == null)
			return 0;
		
		int hits = 0;
		for (ArtifactURL url : vsu)
			if (urls.contains(url))
				hits++;
		
		return (float) hits / vsu.size();
	}
	
	private void traverseElements(NodeList nodes, ElementTraverser traverser) {
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;
				traverser.traverse(e);
			}
		}
	}
	
	private interface ElementTraverser {
		void traverse(Element e);
	}
}
