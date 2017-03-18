package org.universaal.tools.modelling.ontology.wizard.wizards;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.Binding;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.universaal.tools.modelling.ontology.wizard.versions.OntologyProjectGeneratorFactory;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.UpdateListStrategy;

public class OntologyMainPage extends WizardPage {
	private Binding projectNameBinding;
	private DataBindingContext m_bindingContext;
	private Text txtOntologyname;
	private Text txtParentPackage;
	private Text txtProjectname;
	private Text txtNamespace;

	/**
	 * Create the wizard.
	 */
	public OntologyMainPage() {
		super("wizardPage");
		setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.universaal.tools.modelling.ontology.wizard", "icons/ic-uAAL-hdpi.png"));
		setTitle("Ontology project properties");
		setDescription("Set the name and packaging for the ontology and the Eclipse project");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblOntologyName = new Label(container, SWT.NONE);
		lblOntologyName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblOntologyName.setText("Ontology name");
		
		txtOntologyname = new Text(container, SWT.BORDER);
		txtOntologyname.setText("ontologyName");
		txtOntologyname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblParentPackage = new Label(container, SWT.NONE);
		lblParentPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblParentPackage.setText("Parent package");
		
		txtParentPackage = new Text(container, SWT.BORDER);
		txtParentPackage.setText("parentPackageName");
		txtParentPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblMiddlewareVersion = new Label(container, SWT.NONE);
		lblMiddlewareVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMiddlewareVersion.setText("Middleware version");
		
		comboMiddlewareVersion = new Combo(container, SWT.NONE);
		comboMiddlewareVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		btnUseDefaultValues = new Button(container, SWT.CHECK);
		btnUseDefaultValues.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = !btnUseDefaultValues.getSelection();
				txtProjectname.setEnabled(enabled);
				txtNamespace.setEnabled(enabled);
				txtJavaPackageName.setEnabled(enabled);
				txtGroupid.setEnabled(enabled);
				txtMavenname.setEnabled(enabled);
			}
		});
		btnUseDefaultValues.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnUseDefaultValues.setText("Use derived values");
		
		Label lblProjectName = new Label(container, SWT.NONE);
		lblProjectName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProjectName.setText("Project name");
		
		txtProjectname = new Text(container, SWT.BORDER);
		txtProjectname.setEnabled(false);
		txtProjectname.setText("projectName");
		txtProjectname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblJavaPackageName = new Label(container, SWT.NONE);
		lblJavaPackageName.setToolTipText("The package name used for the classes of the ontology");
		lblJavaPackageName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblJavaPackageName.setText("Package name");
		
		txtJavaPackageName = new Text(container, SWT.BORDER);
		txtJavaPackageName.setEnabled(false);
		txtJavaPackageName.setText("javaPackageName");
		txtJavaPackageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblOntologyNamespace = new Label(container, SWT.NONE);
		lblOntologyNamespace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblOntologyNamespace.setText("Namespace");
		
		txtNamespace = new Text(container, SWT.BORDER);
		txtNamespace.setEnabled(false);
		txtNamespace.setText("namespace");
		txtNamespace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblGroupId = new Label(container, SWT.NONE);
		lblGroupId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGroupId.setText("Maven group id");
		
		txtGroupid = new Text(container, SWT.BORDER);
		txtGroupid.setEnabled(false);
		txtGroupid.setText("groupID");
		txtGroupid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblMavenName = new Label(container, SWT.NONE);
		lblMavenName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMavenName.setText("Maven name");
		
		txtMavenname = new Text(container, SWT.BORDER);
		txtMavenname.setEnabled(false);
		txtMavenname.setText("mavenName");
		txtMavenname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		lblMavenVersion = new Label(container, SWT.NONE);
		lblMavenVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMavenVersion.setText("Maven version");
		
		txtVersion = new Text(container, SWT.BORDER);
		txtVersion.setText("version");
		txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblMavenDescription = new Label(container, SWT.NONE);
		lblMavenDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMavenDescription.setText("Maven description");
		
		txtMavendescription = new Text(container, SWT.BORDER);
		txtMavendescription.setText("mavenDescription");
		txtMavendescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label label_2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		btnGenerateJavaToOWL = new Button(container, SWT.CHECK);
		btnGenerateJavaToOWL.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGenerateJavaToOWL.setText("Generate Java to OWL support file");
		
		m_bindingContext = initDataBindings();
	}
	
	OntologyProjectModel model;
	private Button btnUseDefaultValues;
	private Text txtVersion;
	private Text txtGroupid;
	private Text txtMavenname;
	private Text txtMavendescription;
	private Label lblParentPackage;
	private Label lblMavenVersion;
	private Label lblJavaPackageName;
	private Text txtJavaPackageName;
	private Button btnGenerateJavaToOWL;
	private Combo comboMiddlewareVersion;
	private Label lblMiddlewareVersion;
	
	public void setModel(OntologyProjectModel model) {
		this.model = model;
	}
	
	public void updateFromModel() {
		
	}
	
	public void updateModel() {
		
	}	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue txtOntologynameObserveTextObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeText(txtOntologyname, SWT.Modify));
		IObservableValue modelOntologyNameObserveValue = BeansObservables.observeValue(model, "ontologyName");
		bindingContext.bindValue(txtOntologynameObserveTextObserveWidget, modelOntologyNameObserveValue, null, null);
		//
		IObservableValue txtParentPackageObserveTextObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeText(txtParentPackage, SWT.Modify));
		IObservableValue modelParentPackageNameObserveValue = BeansObservables.observeValue(model, "parentPackageName");
		bindingContext.bindValue(txtParentPackageObserveTextObserveWidget, modelParentPackageNameObserveValue, null, null);
		//
		IObservableValue btnUseDefaultValuesObserveSelectionObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeSelection(btnUseDefaultValues));
		IObservableValue modelUseSimpleModeObserveValue = BeansObservables.observeValue(model, "useSimpleMode");
		bindingContext.bindValue(btnUseDefaultValuesObserveSelectionObserveWidget, modelUseSimpleModeObserveValue, null, null);
		//
		IObservableValue txtProjectnameObserveTextObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeText(txtProjectname, SWT.Modify));
		IObservableValue modelProjectNameObserveValue = BeansObservables.observeValue(model, "projectName");
		bindingContext.bindValue(txtProjectnameObserveTextObserveWidget, modelProjectNameObserveValue, null, null);
		//
		IObservableValue txtNamespaceObserveTextObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeText(txtNamespace, SWT.Modify));
		IObservableValue modelOntologyNamespaceObserveValue = BeansObservables.observeValue(model, "ontologyNamespace");
		bindingContext.bindValue(txtNamespaceObserveTextObserveWidget, modelOntologyNamespaceObserveValue, null, null);
		//
		IObservableValue txtGroupidObserveTextObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeText(txtGroupid, SWT.Modify));
		IObservableValue modelPackageNameObserveValue = BeansObservables.observeValue(model, "mavenGroupId");
		bindingContext.bindValue(txtGroupidObserveTextObserveWidget, modelPackageNameObserveValue, null, null);
		//
		IObservableValue txtMavennameObserveTextObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeText(txtMavenname, SWT.Modify));
		IObservableValue modelMavenNameObserveValue = BeansObservables.observeValue(model, "mavenName");
		bindingContext.bindValue(txtMavennameObserveTextObserveWidget, modelMavenNameObserveValue, null, null);
		//
		IObservableValue txtVersionObserveTextObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeText(txtVersion, SWT.Modify));
		IObservableValue modelMavenModelversionObserveValue = PojoObservables.observeValue(model, "mavenModel.version");
		bindingContext.bindValue(txtVersionObserveTextObserveWidget, modelMavenModelversionObserveValue, null, null);
		//
		IObservableValue txtMavendescriptionObserveTextObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeText(txtMavendescription, SWT.Modify));
		IObservableValue modelMavenModeldescriptionObserveValue = PojoObservables.observeValue(model, "mavenModel.description");
		bindingContext.bindValue(txtMavendescriptionObserveTextObserveWidget, modelMavenModeldescriptionObserveValue, null, null);
		//
		IObservableValue txtJavaPackageNameObserveTextObserveWidget = SWTObservables.observeText(txtJavaPackageName, SWT.Modify);
		IObservableValue modelPackageNameObserveValue_1 = BeansObservables.observeValue(model, "packageName");
		bindingContext.bindValue(txtJavaPackageNameObserveTextObserveWidget, modelPackageNameObserveValue_1, null, null);
		//
		IObservableValue btnGenerateJavaToOWLObserveSelectionObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeSelection(btnGenerateJavaToOWL));
		IObservableValue modelbtnGenerateJavaToOWLObserveValue = BeansObservables.observeValue(model, "generateJavaToOWL");
		bindingContext.bindValue(btnGenerateJavaToOWLObserveSelectionObserveWidget, modelbtnGenerateJavaToOWLObserveValue, null, null);
		//
		IObservableList comboMiddlewareVersionObserveItemsObserveListWidget = SWTObservables.observeItems(comboMiddlewareVersion);
		IObservableList modelMiddlewareVersionsObserveList = BeansObservables.observeList(Realm.getDefault(), model, "middlewareVersions");
		bindingContext.bindList(comboMiddlewareVersionObserveItemsObserveListWidget, modelMiddlewareVersionsObserveList, null, null);
		//
		IObservableValue comboMiddlewareVersionObserveSingleSelectionIndexObserveWidget = SWTObservables.observeDelayedValue(100, SWTObservables.observeSingleSelectionIndex(comboMiddlewareVersion));
		IObservableValue modelMwVersionObserveValue = BeansObservables.observeValue(model, "mwVersion");
		bindingContext.bindValue(comboMiddlewareVersionObserveSingleSelectionIndexObserveWidget, modelMwVersionObserveValue, null, null);
		//
		return bindingContext;
	}
}
