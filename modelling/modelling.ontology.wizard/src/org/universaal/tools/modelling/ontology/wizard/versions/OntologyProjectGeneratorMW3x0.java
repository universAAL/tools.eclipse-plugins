package org.universaal.tools.modelling.ontology.wizard.versions;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;

public class OntologyProjectGeneratorMW3x0 extends OntologyProjectGeneratorMW200 {

	private int ver;
	
	OntologyProjectGeneratorMW3x0(int ver) {
		this.ver = ver;
	}
	
	@Override
	public int getMWVersionNumber() {
		return ver;
	}

	static Dependency[] dependencies = new Dependency[] {
		dep("org.universAAL.support", "itests"),
		dep("org.osgi", "org.osgi.core", "4.1.0"),
		dep("org.universAAL.middleware", "mw.data.serialization.core"),
		dep("org.universAAL.middleware", "mw.data.representation.core"),
		dep("org.universAAL.middleware", "mw.container.xfaces.core"),
		dep("org.universAAL.middleware", "mw.bus.service.core"),
		dep("org.universAAL.ontology", "ont.phWorld"),
		dep("org.universAAL.ontology", "ont.device"),
		dep("org.universAAL.ontology", "ont.unit"),
		dep("org.universAAL.ontology", "ont.measurement"),
		dep("org.coode.owlapi", "owlapi", "3.3")
	};

	
	static Repository[] repositories = new Repository[] {
		rep("central","Central Maven Repository", "http://repo1.maven.org/maven2", true, false, null ),
		rep("apache-snapshots", "Apache Snapshots","http://people.apache.org/repo/m2-snapshot-repository", false, true, "daily" ),
		rep("uaal", "universAAL Repositories", "http://depot.universaal.org/maven-repo/releases/", true, false, null ),
		rep("uaal-snapshots", "universAAL Snapshot Repositories", "http://depot.universaal.org/maven-repo/snapshots/", false, true, null),
		rep("uaal-thirdparty", "universAAL Third Party Repositories", "http://depot.universaal.org/maven-repo/thirdparty/", true, false, null)
	};
	
	@Override
	public Dependency[] getDependencies() {
		return dependencies;
	}
	
	@Override
	public Repository[] getRepositories() {
		return repositories;
	}
}
