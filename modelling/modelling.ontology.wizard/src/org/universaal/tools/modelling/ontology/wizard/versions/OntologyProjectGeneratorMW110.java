package org.universaal.tools.modelling.ontology.wizard.versions;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.universaal.tools.modelling.ontology.wizard.Activator;
import org.universaal.tools.modelling.ontology.wizard.wizards.OntologyProjectModel;
import org.universaal.tools.modelling.ontology.wizard.wizards.OntologyUMLArtefactFactory;

public class OntologyProjectGeneratorMW110 implements IOntologyProjectGenerator {
	static final String[] folders = new String[] {
		"src/main/java", "src/test/java", "src/main/resources", "src/test/resources" };

	public static final String MODEL_DIR = "models";
	public static final String DI_FILE = "model.di";
	
	@Override
	public int getMWVersionNumber() {
		return IOntologyProjectGenerator.VER_110;
	}
	

	@Override
	public String getMWVersionName() {
		return OntologyProjectGeneratorFactory.getVersonName(getMWVersionNumber());
	}	

	@Override
	public void createPOM(OntologyProjectModel ontModel, IProject project,
			ProjectImportConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {
		this.createPOMImpl(ontModel, project, configuration, monitor);
	}
	
	protected static Dependency dep(String groupId, String artifactId, String version) {
		Dependency d = new Dependency();
		d.setGroupId(groupId);
		d.setArtifactId(artifactId);
		if (version != null)
			d.setVersion(version);
		return d;
	}	
	
	protected static Dependency dep(String groupId, String artifactId) {
		return dep(groupId, artifactId, null);
	}	
	
	public void createPOMImpl(OntologyProjectModel ontModel,
			IProject project, ProjectImportConfiguration configuration,
			IProgressMonitor monitor) throws CoreException {

		// Here we use the maven plugin to create and shape the
		// project
		Model mavenModel = ontModel.getMavenModel();
		
		mavenModel.setParent(createParent(ontModel));
		mavenModel.setPackaging("bundle");
		
		Dependency[] deps = getDependencies();
		for (Dependency dep : deps) {
			mavenModel.addDependency(dep);
		}
		Repository[] reps = getRepositories();
		for (Repository rep : reps) {
			mavenModel.addRepository(rep);
		}
		mavenModel.setBuild(createBuild(ontModel));
		MavenPlugin.getProjectConfigurationManager().createSimpleProject(
				project, null,// was: location
				mavenModel, folders, //
				configuration, monitor);
	}	
	
	protected static Xpp3Dom dom(String name, String value) {
		Xpp3Dom dom = new Xpp3Dom(name);
		dom.setValue(value);
		return dom;
	}
	
	
	protected Parent createParent(OntologyProjectModel ontologyProjectModel) {
		Parent parent = new Parent();
		parent.setGroupId("org.universAAL.ontology");
		parent.setArtifactId("ont.pom");
		parent.setVersion(getMWVersionName());
		parent.setRelativePath("../ont.pom");
		return parent;
	}
	
	protected Build createBuild(OntologyProjectModel ontologyProjectModel) {
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
	
	static Dependency[] dependencies = new Dependency[] {
		dep("org.universAAL.support", "itests"),
		dep("org.apache.felix", "org.osgi.core"),
		dep("org.universAAL.middleware", "mw.data.serialization"),
		dep("org.universAAL.middleware", "mw.data.representation"),
		//dep("org.universAAL.middleware", "mw.bus.model", "1.1.0"),
		dep("org.universAAL.middleware", "mw.container.xfaces"),
		//dep("org.universAAL.middleware", "mw.container.osgi"),
		dep("org.universAAL.middleware", "mw.bus.service"),
		//dep("org.universAAL.middleware", "mw.bus.context", "1.1.0"),
		//dep("org.universAAL.middleware", "mw.bus.ui", "1.1.0"),
		dep("org.universAAL.ontology", "ont.phWorld"),
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
	
	public Dependency[] getDependencies() {
		return dependencies;
	}
	
	public Repository[] getRepositories() {
		return repositories;
	}
	
	/**
	 * Get the subdirectory name within the model folder to copy template UML files from for this version
	 * @return The simple subdirectory name (not full path)
	 */
	protected String getModelVersionDirectory() {
		return "1.0.0";
	}
	

	@Override
	public void createUMLArtefacts(OntologyProjectModel model) {
		// TODO Auto-generated method stub
		URI fromUri = URI.createPlatformPluginURI("/" + Activator.PLUGIN_ID + "/" + MODEL_DIR + "/" + getModelVersionDirectory() + "/" + DI_FILE, true);
		OntologyUMLArtefactFactory.createUMLArtefacts(model, fromUri);
	}


}
