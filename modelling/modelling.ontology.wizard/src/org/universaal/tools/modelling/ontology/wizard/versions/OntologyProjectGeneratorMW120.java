package org.universaal.tools.modelling.ontology.wizard.versions;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.universaal.tools.modelling.ontology.wizard.wizards.OntologyProjectModel;

public class OntologyProjectGeneratorMW120 extends OntologyProjectGeneratorMW110 {

	@Override
	public int getMWVersionNumber() {
		return IOntologyProjectGenerator.VER_120;
	}

	static Dependency[] dependencies = new Dependency[] {
		/*dep("org.apache.felix", "org.osgi.core", "1.0.1"),
		dep("org.universAAL.middleware", "mw.data.serialization.osgi", "1.2.0"),
		dep("org.universAAL.middleware", "mw.data.representation.osgi", "1.2.0"),
		dep("org.universAAL.middleware", "mw.bus.model.osgi", "1.2.0"),
		dep("org.universAAL.middleware", "mw.container.xfaces.osgi", "1.2.0"),
		dep("org.universAAL.middleware", "mw.container.osgi","1.2.0" ),
		dep("org.universAAL.middleware", "mw.bus.service.osgi","1.2.0" ),
		dep("org.universAAL.middleware", "mw.bus.context.osgi", "1.2.0"),
		dep("org.universAAL.middleware", "mw.bus.ui.osgi", "1.2.0"),
		dep("org.universAAL.ontology", "ont.phWorld", "1.2.0"),
		dep("org.universAAL.ontology", "ont.profile", "1.2.0"),
		dep("org.coode.owlapi", "owlapi", "3.3")
		*/
		
		dep("org.universAAL.support", "itests"),
		dep("org.apache.felix", "org.osgi.core"),
		dep("org.universAAL.middleware", "mw.data.serialization.core"),
		dep("org.universAAL.middleware", "mw.data.representation.core"),
		//dep("org.universAAL.middleware", "mw.bus.model", "1.1.0"),
		dep("org.universAAL.middleware", "mw.container.xfaces.core"),
		//dep("org.universAAL.middleware", "mw.container.osgi"),
		dep("org.universAAL.middleware", "mw.bus.service.core"),
		//dep("org.universAAL.middleware", "mw.bus.context", "1.1.0"),
		//dep("org.universAAL.middleware", "mw.bus.ui", "1.1.0"),
		dep("org.universAAL.ontology", "ont.phWorld"),
		dep("org.universAAL.ontology", "ont.unit"),
		dep("org.universAAL.ontology", "ont.measurement"),
		//dep("org.universAAL.ontology", "ont.profile", "1.1.0"),
		dep("org.coode.owlapi", "owlapi", "3.3")		
	};	

	
	static Repository[] repositories = new Repository[] {
		rep("central","Central Maven Repository", "http://repo1.maven.org/maven2", true, false, null ),
		rep("apache-snapshots", "Apache Snapshots","http://people.apache.org/repo/m2-snapshot-repository", false, true, "daily" ),
		rep("uaal", "universAAL Repositories", "http://depot.universaal.org/maven-repo/releases/", true, false, null ),
		rep("uaal-snapshots", "universAAL Snapshot Repositories", "http://depot.universaal.org/maven-repo/snapshots/", false, true, null),
		rep("uaal-thirdparty", "universAAL Third Party Repositories", "http://depot.universaal.org/maven-repo/thirdparty/", true, false, null)
	};	
	
	protected Build createBuild(OntologyProjectModel ontologyProjectModel) {
		Build build = new Build();
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.apache.felix");
		plugin.setArtifactId("maven-bundle-plugin");
		plugin.setExtensions(true);
		Xpp3Dom conf = new Xpp3Dom("configuration");
		Xpp3Dom instr = new Xpp3Dom("instructions");
		instr.addChild(dom("Bundle-Name", "${project.name}"));
		instr.addChild(dom("Bundle-Description", "${project.description}"));
		instr.addChild(dom("Bundle-SymbolicName", "${project.artifactId}"));
		instr.addChild(dom("Export-Package", ontologyProjectModel.getPackageName() + ", " + ontologyProjectModel.getPackageName() + ".*")); 
		instr.addChild(dom("Private-Package", ontologyProjectModel.getParentPackageName() + ";-split-package:=first" )); 
		conf.addChild(instr);
		plugin.setConfiguration(conf);
		build.addPlugin(plugin);
		return build;
	}	
	
	@Override
	public Dependency[] getDependencies() {
		return dependencies;
	}
	
	@Override
	public Repository[] getRepositories() {
		return repositories;
	}
	
}
