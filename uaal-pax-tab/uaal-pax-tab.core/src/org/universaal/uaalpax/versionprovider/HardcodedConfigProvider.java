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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.universaal.uaalpax.model.ArtifactURL;
import org.universaal.uaalpax.model.BundleEntry;
import org.universaal.uaalpax.model.BundleSet;
import org.universaal.uaalpax.model.LaunchURL;

public class HardcodedConfigProvider implements UAALVersionProvider {
	private static Map<String, BundleSet> middlewares = new HashMap<String, BundleSet>();
	private static Map<String, Map<String, BundleSet>> features = new HashMap<String, Map<String, BundleSet>>();
	private static Map<String, Set<ArtifactURL>> versionSegnificantURLs = new HashMap<String, Set<ArtifactURL>>();
	private static Set<String> ignoreSet;
	
	private static final BundleEntry[] m111_snapshot = new BundleEntry[] {
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.configadmin/1.2.4"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("mvn:org.osgi/org.osgi.compendium/4.2.0"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.acl.interfaces/1.1.1-SNAPSHOT"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.context/1.1.1-SNAPSHOT"), "true@true@5@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.model/1.1.1-SNAPSHOT"), "true@true@3@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.service/1.1.1-SNAPSHOT"), "true@true@5@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.container.osgi/1.1.1-SNAPSHOT"), "true@true@3@true"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.container.xfaces/1.1.1-SNAPSHOT"), "true@true@2@true"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.data.representation/1.1.1-SNAPSHOT"), "true@true@4@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.data.serialization/1.1.1-SNAPSHOT"), "true@true@4@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.ui/1.1.1-SNAPSHOT"), "true@true@6@false"),
			
			new BundleEntry(new LaunchURL("wrap:mvn:java3d/j3d-core/1.3.1"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:java3d/vecmath/1.3.1"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:jp.go.ipa/jgcl/1.0"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:org.bouncycastle/jce.jdk13/144"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:org.ops4j.pax.confman/pax-confman-propsloader/0.2.2"), "true@true@3@false"),
			
			new BundleEntry(new LaunchURL("wrap:mvn:org.ops4j.pax.logging/pax-logging-api/1.6.2"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:org.ops4j.pax.logging/pax-logging-service/1.6.2"), "true@true@3@false"), };
	
	private static final String[] vsu111_snapshot = new String[] { "org.universAAL.middleware/mw.acl.interfaces/1.1.1-SNAPSHOT",
			"org.universAAL.middleware/mw.bus.context/1.1.1-SNAPSHOT", "org.universAAL.middleware/mw.bus.model/1.1.1-SNAPSHOT",
			"org.universAAL.middleware/mw.bus.service/1.1.1-SNAPSHOT", "org.universAAL.middleware/mw.container.osgi/1.1.1-SNAPSHOT",
			"org.universAAL.middleware/mw.container.xfaces/1.1.1-SNAPSHOT",
			"org.universAAL.middleware/mw.data.representation/1.1.1-SNAPSHOT",
			"org.universAAL.middleware/mw.data.serialization/1.1.1-SNAPSHOT", };
	
	private static final BundleEntry[] m120 = new BundleEntry[] {
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.container.xfaces.osgi/1.2.0"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.acl.interfaces.osgi/1.2.0"), "true@true@3@false"),
			new BundleEntry(new LaunchURL("mvn:org.ops4j.pax.logging/pax-logging-api/1.6.2"), "true@true@4@false"),
			new BundleEntry(new LaunchURL("mvn:org.ops4j.pax.logging/pax-logging-service/1.6.2"), "true@true@5@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.osgi.compendium/1.4.0"), "true@true@6@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.log/1.0.1"), "true@true@7@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.configadmin/1.2.4"), "true@true@8@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.fileinstall/3.1.10"), "true@true@9@false"),
			new BundleEntry(new LaunchURL("mvn:org.ops4j.pax.confman/pax-confman-propsloader/0.2.2"), "true@true@10@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.bundlerepository/1.4.2"), "true@true@11@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.container.osgi/1.2.0"), "true@true@12@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:org.bouncycastle/jce.jdk13/144"), "true@true@13@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.acl.upnp.osgi/1.2.0"), "true@true@14@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.upnp.basedriver/0.8.0"), "true@true@15@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.model.osgi/1.2.0"), "true@true@16@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.data.representation.osgi/1.2.0"), "true@true@17@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.service.osgi/1.2.0"), "true@true@18@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.context.osgi/1.2.0"), "true@true@19@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.data.serialization.osgi/1.2.0"), "true@true@20@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.ui.osgi/1.2.0"), "true@true@19@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:java3d/vecmath/1.3.1"), "true@true@21@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:java3d/j3d-core/1.3.1"), "true@true@22@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:jp.go.ipa/jgcl/1.0"), "true@true@23@false"), };
	
	private static final String[] vsu120 = new String[] { "org.universAAL.middleware/mw.container.xfaces.osgi/1.2.0",
			"org.universAAL.middleware/mw.acl.interfaces.osgi/1.2.0", "org.universAAL.middleware/mw.container.osgi/1.2.0",
			"org.universAAL.middleware/mw.bus.model.osgi/1.2.0", "org.universAAL.middleware/mw.data.representation.osgi/1.2.0",
			"org.universAAL.middleware/mw.bus.service.osgi/1.2.0", "org.universAAL.middleware/mw.bus.context.osgi/1.2.0",
			"org.universAAL.middleware/mw.data.serialization.osgi/1.2.0" };
	
	private static final BundleEntry[] m121_snapshot = new BundleEntry[] {
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.container.xfaces.osgi/1.2.1-SNAPSHOT"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.acl.interfaces.osgi/1.2.1-SNAPSHOT"), "true@true@3@false"),
			new BundleEntry(new LaunchURL("mvn:org.ops4j.pax.logging/pax-logging-api/1.6.2"), "true@true@4@false"),
			new BundleEntry(new LaunchURL("mvn:org.ops4j.pax.logging/pax-logging-service/1.6.2"), "true@true@5@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.osgi.compendium/1.4.0"), "true@true@6@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.log/1.0.1"), "true@true@7@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.configadmin/1.2.4"), "true@true@8@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.fileinstall/3.1.10"), "true@true@9@false"),
			new BundleEntry(new LaunchURL("mvn:org.ops4j.pax.confman/pax-confman-propsloader/0.2.2"), "true@true@10@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.bundlerepository/1.4.2"), "true@true@11@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.container.osgi/1.2.1-SNAPSHOT"), "true@true@12@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:org.bouncycastle/jce.jdk13/144"), "true@true@13@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.acl.upnp.osgi/1.2.1-SNAPSHOT"), "true@true@14@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.upnp.basedriver/0.8.0"), "true@true@15@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.model.osgi/1.2.1-SNAPSHOT"), "true@true@16@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.data.representation.osgi/1.2.1-SNAPSHOT"), "true@true@17@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.service.osgi/1.2.1-SNAPSHOT"), "true@true@18@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.context.osgi/1.2.1-SNAPSHOT"), "true@true@19@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.data.serialization.osgi/1.2.1-SNAPSHOT"), "true@true@20@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.ui.osgi/1.2.1-SNAPSHOT"), "true@true@19@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:java3d/vecmath/1.3.1"), "true@true@21@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:java3d/j3d-core/1.3.1"), "true@true@22@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:jp.go.ipa/jgcl/1.0"), "true@true@23@false"), };
	
	private static final String[] vsu121_snapshot = new String[] { "org.universAAL.middleware/mw.container.xfaces.osgi/1.2.1-SNAPSHOT",
			"org.universAAL.middleware/mw.acl.interfaces.osgi/1.2.1-SNAPSHOT",
			"org.universAAL.middleware/mw.container.osgi/1.2.1-SNAPSHOT", "org.universAAL.middleware/mw.bus.model.osgi/1.2.1-SNAPSHOT",
			"org.universAAL.middleware/mw.data.representation.osgi/1.2.1-SNAPSHOT",
			"org.universAAL.middleware/mw.bus.service.osgi/1.2.1-SNAPSHOT", "org.universAAL.middleware/mw.bus.context.osgi/1.2.1-SNAPSHOT",
			"org.universAAL.middleware/mw.data.serialization.osgi/1.2.1-SNAPSHOT" };
	
	private static final BundleEntry[] m130 = new BundleEntry[] {
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.container.xfaces.osgi/1.3.0"), "true@true@2@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.acl.interfaces.osgi/1.3.0"), "true@true@3@false"),
			new BundleEntry(new LaunchURL("mvn:org.ops4j.pax.logging/pax-logging-api/1.6.2"), "true@true@4@false"),
			new BundleEntry(new LaunchURL("mvn:org.ops4j.pax.logging/pax-logging-service/1.6.2"), "true@true@5@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.osgi.compendium/1.4.0"), "true@true@6@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.log/1.0.1"), "true@true@7@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.configadmin/1.2.4"), "true@true@8@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.fileinstall/3.1.10"), "true@true@9@false"),
			new BundleEntry(new LaunchURL("mvn:org.ops4j.pax.confman/pax-confman-propsloader/0.2.2"), "true@true@10@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.bundlerepository/1.4.2"), "true@true@11@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.container.osgi/1.3.0"), "true@true@12@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:org.bouncycastle/jce.jdk13/144"), "true@true@13@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.acl.upnp.osgi/1.3.0"), "true@true@14@false"),
			new BundleEntry(new LaunchURL("mvn:org.apache.felix/org.apache.felix.upnp.basedriver/0.8.0"), "true@true@15@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.model.osgi/1.3.0"), "true@true@16@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.data.representation.osgi/1.3.0"), "true@true@17@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.service.osgi/1.3.0"), "true@true@18@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.context.osgi/1.3.0"), "true@true@19@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.data.serialization.osgi/1.3.0"), "true@true@20@false"),
			new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.ui.osgi/1.3.0"), "true@true@19@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:java3d/vecmath/1.3.1"), "true@true@21@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:java3d/j3d-core/1.3.1"), "true@true@22@false"),
			new BundleEntry(new LaunchURL("wrap:mvn:jp.go.ipa/jgcl/1.0"), "true@true@23@false"), };
	
	private static final String[] vsu130 = new String[] { "org.universAAL.middleware/mw.container.xfaces.osgi/1.3.0",
			"org.universAAL.middleware/mw.acl.interfaces.osgi/1.3.0", "org.universAAL.middleware/mw.container.osgi/1.3.0",
			"org.universAAL.middleware/mw.bus.model.osgi/1.3.0", "org.universAAL.middleware/mw.data.representation.osgi/1.3.0",
			"org.universAAL.middleware/mw.bus.service.osgi/1.3.0", "org.universAAL.middleware/mw.bus.context.osgi/1.3.0",
			"org.universAAL.middleware/mw.data.serialization.osgi/1.3.0" };
	
	static {
		registerVersion("1.1.1-SNAPSHOT", m111_snapshot, vsu111_snapshot);
		registerVersion("1.2.0", m120, vsu120);
		registerVersion("1.2.1-SNAPSHOT", m121_snapshot, vsu121_snapshot);
		registerVersion("1.3.0", m130, vsu130);
		
		// create ignore set
		ignoreSet = new HashSet<String>();
		ignoreSet.add("org.apache.felix/org.apache.felix.main");
		
		ignoreSet.add("org.apache.felix/org.apache.felix.framework");
		ignoreSet.add("org.apache.felix/org.osgi.core");
		ignoreSet.add("org.apache.felix/org.apache.felix.bundlerepository");
		ignoreSet.add("org.apache.felix/org.apache.felix.shell");
		ignoreSet.add("org.apache.felix/org.apache.felix.shell.tui");
		ignoreSet.add("org.apache.felix/org.osgi.compendium");
		// ignoreSet.add("org.ops4j.pax.logging/pax-logging-api/1.6.2");
		// ignoreSet.add("org.ops4j.pax.logging/pax-logging-service/1.6.2");
		ignoreSet.add("org.ops4j.pax.logging/pax-logging-api");
		ignoreSet.add("org.ops4j.pax.logging/pax-logging-service");
		ignoreSet.add("org.apache.felix/javax.servlet");
		
		ignoreSet.add("wrap:mvn:org.ops4j.pax.logging/pax-logging-service");
		ignoreSet.add("wrap:mvn:org.ops4j.pax.logging/pax-logging-api");
		
		ignoreSet.add("java3d/j3d-core/1.3.1");
		ignoreSet.add("java3d/vecmath/1.3.1");
		ignoreSet.add("jp.go.ipa/jgcl/1.0");
		ignoreSet.add("org.bouncycastle/jce.jdk13/144");
		
		ignoreSet.add("org.universAAL.middleware/mw.composite");
		
		ignoreSet.add("javax.media/jmf");
		ignoreSet.add("org.apache.felix/org.osgi.foundation");
		ignoreSet.add("org.apache.felix/org.apache.felix.log");
		
		ignoreSet.add("org.osgi/org.osgi.core");
		ignoreSet.add("org.osgi/org.osgi.osgi");
		ignoreSet.add("org.osgi/org.osgi.compendium");
		
		Map<String, BundleSet> f111 = new HashMap<String, BundleSet>();
		f111.put("UI Bus",
				new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.ui/1.1.1-SNAPSHOT"), "")));
		f111.put("CHE",
				new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.context/ctxt.che/1.1.1-SNAPSHOT"), "")).add(
						new BundleEntry(new LaunchURL("mvn:org.universAAL.ontology/ont.che/1.1.1-SNAPSHOT"), "")));
		features.put("1.1.1-SNAPSHOT", f111);
		
		Map<String, BundleSet> f120 = new HashMap<String, BundleSet>();
		f120.put("UI Bus", new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.ui.osgi/1.2.0"), "")));
		f120.put("CHE",
				new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.context/ctxt.che/1.2.0"), "")).add(
						new BundleEntry(new LaunchURL("mvn:org.universAAL.ontology/ont.che/1.2.0"), "")));
		f120.put("Log Monitor", new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.tools/tools.log_mon/1.2.0"), "")));
		features.put("1.2.0", f120);
		
		Map<String, BundleSet> f121 = new HashMap<String, BundleSet>();
		f121.put("UI Bus",
				new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.ui.osgi/1.2.1-SNAPSHOT"), "")));
		f121.put("CHE",
				new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.context/ctxt.che/1.2.1-SNAPSHOT"), "")).add(
						new BundleEntry(new LaunchURL("mvn:org.universAAL.ontology/ont.che/1.2.1-SNAPSHOT"), "")));
		f121.put("Log Monitor", new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.tools/tools.log_mon/1.2.1-SNAPSHOT"), "")));
		features.put("1.2.1-SNAPSHOT", f121);
		
		Map<String, BundleSet> f130 = new HashMap<String, BundleSet>();
		f130.put("UI Bus", new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.middleware/mw.bus.ui.osgi/1.3.0"), "")));
		f130.put("CHE",
				new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.context/ctxt.che/1.3.0"), "")).add(
						new BundleEntry(new LaunchURL("mvn:org.universAAL.ontology/ont.che/1.3.0"), "")));
		f130.put("Log Monitor", new BundleSet().add(new BundleEntry(new LaunchURL("mvn:org.universAAL.tools/tools.log_mon/1.3.0"), "")));
		features.put("1.3.0", f130);
	}
	
	private static void registerVersion(String version, BundleEntry[] bundles, String[] significant) {
		BundleSet bs = new BundleSet();
		for (BundleEntry pu : bundles)
			bs.add(pu);
		middlewares.put(version, bs);
		
		Set<ArtifactURL> s = new HashSet<ArtifactURL>();
		for (String sig : significant)
			s.add(new ArtifactURL(sig));
		versionSegnificantURLs.put(version, s);
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
		for (String ignore : ignoreSet)
			if (artifactUrl.url.startsWith(ignore))
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
}
