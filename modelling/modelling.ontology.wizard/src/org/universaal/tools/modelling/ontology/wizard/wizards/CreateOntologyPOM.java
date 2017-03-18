package org.universaal.tools.modelling.ontology.wizard.wizards;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;

public class CreateOntologyPOM {
//	static final String[] folders = new String[] {
//		"src/main/java", "src/test/java", "src/main/resources", "src/test/resources" };

	/**
	 * Create a Maven POM file set up for the content of the ontology project
	 * @param ontModel The model with information about the ontology project
	 * @param project The Eclipse project
	 * @param configuration A Maven project configuration
	 * @param monitor The progress monitor for the job this is performed in
	 * @throws CoreException
	 */

	/*	
	
	public static void createPOM(OntologyProjectModel ontModel,
			IProject project, ProjectImportConfiguration configuration,
			IProgressMonitor monitor) throws CoreException {

		// Here we use the maven plugin to create and shape the
		// project
		Model mavenModel = ontModel.getMavenModel();
		for (Dependency dep : dependencies) {
			mavenModel.addDependency(dep);
		}
		for (Repository rep : repositories) {
			mavenModel.addRepository(rep);
		}
		mavenModel.setBuild(createBuild(ontModel));
		MavenPlugin.getProjectConfigurationManager().createSimpleProject(
				project, null,// was: location
				mavenModel, folders, //
				configuration, monitor);
	}

	
	protected static Dependency dep(String groupId, String artifactId, String version) {
		Dependency d = new Dependency();
		d.setGroupId(groupId);
		d.setArtifactId(artifactId);
		d.setVersion(version);
		return d;
	}
	
	
	static Dependency[] dependencies = new Dependency[] {
			dep("org.apache.felix", "org.osgi.core", "1.0.1"),
			dep("org.universAAL.middleware", "mw.data.serialization", "1.1.0"),
			dep("org.universAAL.middleware", "mw.data.representation", "1.1.0"),
			dep("org.universAAL.middleware", "mw.bus.model", "1.1.0"),
			dep("org.universAAL.middleware", "mw.container.xfaces", "1.1.0"),
			dep("org.universAAL.middleware", "mw.container.osgi","1.1.0" ),
			dep("org.universAAL.middleware", "mw.bus.service","1.1.0" ),
			dep("org.universAAL.middleware", "mw.bus.context", "1.1.0"),
			dep("org.universAAL.middleware", "mw.bus.ui", "1.1.0"),
			dep("org.universAAL.ontology", "ont.phWorld", "1.1.0"),
			dep("org.universAAL.ontology", "ont.profile", "1.1.0"),
			dep("org.coode.owlapi", "owlapi", "3.3")
	};	
	
	static Repository[] repositories = new Repository[] {
			rep("central","Central Maven Repository", "http://repo1.maven.org/maven2", true, false, null ),
			rep("apache-snapshots", "Apache Snapshots","http://people.apache.org/repo/m2-snapshot-repository", false, true, "daily" ),
			rep("uaal", "universAAL Repositories", "http://depot.universaal.org/maven-repo/releases/", true, false, null ),
			rep("uaal-snapshots", "universAAL Snapshot Repositories", "http://depot.universaal.org/maven-repo/snapshots/", false, true, null),
			rep("uaal-thirdparty", "universAAL Third Party Repositories", "http://depot.universaal.org/maven-repo/thirdparty/", true, false, null)
	};
	
	protected static Repository rep(String id, String name, String url, boolean releases, boolean snapshots, String snapshotPolicy ) {
		Repository rep = new Repository();
		rep.setId(id);
		rep.setName(name);
		rep.setUrl(url);
		if (!releases) {
			RepositoryPolicy pol = new RepositoryPolicy();
			pol.setEnabled(false);
			rep.setReleases(pol);
		}
		if ((!snapshots) || (snapshotPolicy != null)) {
			RepositoryPolicy pol = new RepositoryPolicy();
			pol.setEnabled(snapshots);
			if (snapshotPolicy != null)
				pol.setUpdatePolicy(snapshotPolicy);
			rep.setSnapshots(pol);
		}
		return rep;
	}
*/	
	/*
	protected static Xpp3Dom dom(String name, String value) {
		Xpp3Dom dom = new Xpp3Dom(name);
		dom.setValue(value);
		return dom;
	}
	
	
	static protected Build createBuild(OntologyProjectModel ontologyProjectModel) {
		Build build = new Build();
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.apache.felix");
		plugin.setArtifactId("maven-bundle-plugin");
		plugin.setExtensions(true);
		Xpp3Dom conf = new Xpp3Dom("configuration");
		Xpp3Dom instr = new Xpp3Dom("instructions");
		instr.addChild(dom("Bundle-Name", "${project.name}"));
		instr.addChild(dom("Bundle-Activator", ontologyProjectModel.getParentPackageName() + ".osgi.Activator")); 
		instr.addChild(dom("Bundle-Description", "${project.description}"));
		instr.addChild(dom("Bundle-SymbolicName", "${project.artifactId}"));
		instr.addChild(dom("Export-Package", ontologyProjectModel.getPackageName() + ", " + ontologyProjectModel.getPackageName() + ".*")); 
		instr.addChild(dom("Private-Package", ontologyProjectModel.getParentPackageName() + ", " + ontologyProjectModel.getParentPackageName() + ".osgi")); 
		conf.addChild(instr);
		plugin.setConfiguration(conf);
		build.addPlugin(plugin);
		return build;
	}
		
	*/
}
