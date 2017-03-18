package org.universaal.tools.modelling.ontology.wizard.versions;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;

public class OntologyProjectGeneratorMW200 extends OntologyProjectGeneratorMW130 {

	@Override
	public int getMWVersionNumber() {
		return IOntologyProjectGenerator.VER_200;
	}

	static Dependency[] dependencies = new Dependency[] {
/*		dep("org.apache.felix", "org.osgi.core", "1.0.1"),
		dep("org.universAAL.middleware", "mw.data.serialization.osgi", "1.3.2-SNAPSHOT"),
		dep("org.universAAL.middleware", "mw.data.representation.osgi", "1.3.2-SNAPSHOT"),
		dep("org.universAAL.middleware", "mw.bus.model.osgi", "1.3.2-SNAPSHOT"),
		dep("org.universAAL.middleware", "mw.container.xfaces.osgi", "1.3.2-SNAPSHOT"),
		dep("org.universAAL.middleware", "mw.container.osgi","1.3.2-SNAPSHOT" ),
		dep("org.universAAL.middleware", "mw.bus.service.osgi","1.3.2-SNAPSHOT" ),
		dep("org.universAAL.middleware", "mw.bus.context.osgi", "1.3.2-SNAPSHOT"),
		dep("org.universAAL.middleware", "mw.bus.ui.osgi", "1.3.2-SNAPSHOT"),
		dep("org.universAAL.ontology", "ont.phWorld", "1.3.2-SNAPSHOT"),
		dep("org.universAAL.ontology", "ont.profile", "1.3.2-SNAPSHOT"),
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
		dep("org.universAAL.ontology", "ont.device"),
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
	
	@Override
	public Dependency[] getDependencies() {
		return dependencies;
	}
	
	@Override
	public Repository[] getRepositories() {
		return repositories;
	}		
	
}
