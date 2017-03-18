package org.universaal.tools.modelling.ontology.wizard.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.ResourceManager;

public class OntologyImportPage extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public OntologyImportPage() {
		super("wizardPage");
		setMessage("The upper ontologies (listed below) will be imported into the model.\nTo import other ontologies, use \"Import Package from Workspace\" in the Model Explorer context menu.");
		setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.universaal.tools.modelling.ontology.wizard", "icons/ic-uAAL-hdpi.png"));
		setTitle("Ontology import");
		setDescription("The universAAL upper ontologies (listed below) will be imported into the generated model.\nTo import other ontologies, select \"Import Package from Workspace\" in the context menu of the Model Explorer view.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		ListViewer listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL);
		List list = listViewer.getList();
		list.setItems(new String[] {"Middleware Ontology (org.universaal.middleware.owl)", "Service Ontology (org.universaal.middleware.service.owl)", "Physical World Ontology (org.universaal.ontology.phThing)", "Device Ontology (org.universaal.ontology.device)", "Unit Ontology (org.universaal.ontology.unit)", "Measurement Ontology (org.universaal.ontology.measurement)", "Primitive Types (org.universaal.ontology.datatypes)"});
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}
	
	OntologyProjectModel model;
	
	public void setModel(OntologyProjectModel model) {
		this.model = model;
	}
	
	public void updateFromModel() {
		
	}
	
	public void updateModel() {
		
	}

}
