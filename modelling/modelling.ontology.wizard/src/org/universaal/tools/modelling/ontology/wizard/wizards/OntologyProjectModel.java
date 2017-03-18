package org.universaal.tools.modelling.ontology.wizard.wizards;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Model;
import org.universaal.tools.modelling.ontology.wizard.versions.IOntologyProjectGenerator;
import org.universaal.tools.modelling.ontology.wizard.versions.OntologyProjectGeneratorFactory;

public class OntologyProjectModel  {
	public static final String DEFAULT_ONTOLOGY_NAME = "MyOntology";
	public static final String DEFAULT_PARENT_PACKAGE_NAME = "org.universAAL.ontology";

	String ontologyName = "ont";
	String parentPackageName = "pck";
	
	boolean useSimpleMode = true;
	boolean generateJavaToOWL = true;

	String projectName;
	String packageName;
	String ontologyNamespace;
	
	String mavenGroupId;
	String mavenName;
	
	int mwVersion = IOntologyProjectGenerator.VER_LATEST; 

	public int getMwVersion() {
		return mwVersion;
	}

	public void setMwVersion(int mwVersion) {
		if (this.mwVersion != mwVersion) {		
			support.firePropertyChange("mwVersion", this.mwVersion, this.mwVersion = mwVersion);
		}
	}

	
	List<String> middlewareVersions;
	
	public List<String> getMiddlewareVersions() {
		if (middlewareVersions == null)
			middlewareVersions = Arrays.asList(OntologyProjectGeneratorFactory.getAllVersonNames());
		return middlewareVersions;
		
	}
	
	
	List<String> importedOntologies = new ArrayList<String>();	
	Model mavenModel = new Model();

	public OntologyProjectModel() {
		setOntologyName(DEFAULT_ONTOLOGY_NAME);
		setParentPackageName(DEFAULT_PARENT_PACKAGE_NAME);
		mavenModel.setModelVersion("4.0.0"); //$NON-NLS-1$
		mavenModel.setVersion("0.1.0-SNAPSHOT");
	}
	
	public String getOntologyName() {
		return ontologyName;
	}
	public void setOntologyName(String ontologyName) {
		if (this.ontologyName != ontologyName) {
			support.firePropertyChange("ontologyName", this.ontologyName, this.ontologyName = ontologyName);
			if (useSimpleMode) {
				String fullPkgName = parentPackageName + "." + ontologyName.toLowerCase();
				setProjectName(fullPkgName);
				setPackageName(fullPkgName);
				setOntologyNamespace("http://"+revertDomainName(parentPackageName) + "/" + getOntologyName() + ".owl");
				setMavenName(ontologyName);
			}
		}
	}
	public String getParentPackageName() {
		return parentPackageName;
	}

	public void setParentPackageName(String parentPackageName) {
		if (this.parentPackageName != parentPackageName) {
			support.firePropertyChange("parentPackageName", this.parentPackageName, this.parentPackageName = parentPackageName);
			if (useSimpleMode) {
				String fullPkgName = parentPackageName + "." + ontologyName.toLowerCase();
				setProjectName(fullPkgName);
				setPackageName(fullPkgName);
				setOntologyNamespace("http://"+revertDomainName(parentPackageName) + "/" + ontologyName + ".owl");
				setMavenGroupId(parentPackageName);
			}
		}
	}

	public boolean isUseSimpleMode() {
		return useSimpleMode;
	}
	
	public void setUseSimpleMode(boolean useDerivedValues) {
		if (this.useSimpleMode != useDerivedValues) {
			support.firePropertyChange("useSimpleMode", this.useSimpleMode, this.useSimpleMode = useDerivedValues);			
		}
	}
	
	public boolean isGenerateJavaToOWL() {
		return generateJavaToOWL;
	}
	
	public void setGenerateJavaToOWL(boolean useDerivedValues) {
		if (this.generateJavaToOWL != useDerivedValues) {
			support.firePropertyChange("generateJavaToOWL", this.generateJavaToOWL, this.generateJavaToOWL = useDerivedValues);			
		}
	}

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		if (this.projectName != projectName) {
			support.firePropertyChange("projectName", this.projectName, this.projectName = projectName);
			mavenModel.setArtifactId(projectName);
		}
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		if (this.packageName != packageName) {
			support.firePropertyChange("packageName", this.packageName, this.packageName = packageName);
		}
	}

	public String getMavenGroupId() {
		return mavenGroupId;
	}
	public void setMavenGroupId(String mavenGroupId) {
		if (this.mavenGroupId != mavenGroupId) {
			support.firePropertyChange("mavenGroupId", this.mavenGroupId, this.mavenGroupId = mavenGroupId);
			mavenModel.setGroupId(mavenGroupId);
		}
	}	
	
	public String getMavenName() {
		return mavenName;
	}

	public void setMavenName(String mavenName) {
		if (this.mavenName != mavenName) {
			support.firePropertyChange("mavenName", this.mavenName, this.mavenName = mavenName);
			mavenModel.setName(mavenName);
		}
	}

	public String revertDomainName(String packageName) {
		String[] path = packageName.split("\\.");
		StringBuffer buffer = new StringBuffer();
		for (int x=path.length-1; x>=0; x--) {
			if (x < path.length-1)
				buffer.append(".");
			buffer.append(path[x]);
		}
		return buffer.toString();
	}
	
	public String getOntologyNamespace() {
		return ontologyNamespace;
	}

	public void setOntologyNamespace(String ontologyNamespace) {
		if (this.ontologyNamespace != ontologyNamespace) {
			support.firePropertyChange("ontologyNamespace", this.ontologyNamespace, this.ontologyNamespace = ontologyNamespace);
		}
	}
	
	public List<String> getImportedOntologies() {
		return importedOntologies;
	}
	public Model getMavenModel() {
		return mavenModel;
	}
	
	transient PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
    	support.addPropertyChangeListener(propertyName, listener);
    }
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
		support.removePropertyChangeListener(propertyName,
                listener);
    }
}
