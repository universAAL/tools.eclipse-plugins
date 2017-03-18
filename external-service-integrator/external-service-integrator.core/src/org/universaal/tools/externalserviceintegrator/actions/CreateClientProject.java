/*
	Copyright 2011-2014 CERTH-ITI, http://www.iti.gr
	Information Technologies Institute (ITI)
	Centre For Research and Technology Hellas (CERTH)
	
	
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

package org.universaal.tools.externalserviceintegrator.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.text.edits.TextEdit;
import org.universAAL.ri.wsdlToolkit.ioApi.ComplexObject;
import org.universAAL.ri.wsdlToolkit.ioApi.NativeObject;
import org.universAAL.ri.wsdlToolkit.ioApi.ParsedWSDLDefinition;
import org.universAAL.ri.wsdlToolkit.ioApi.WSOperation;
import org.universaal.tools.externalserviceintegrator.Activator;

public class CreateClientProject {

	private WSOperation selectedOperation = null;
	private ParsedWSDLDefinition theParsedDefinition = null;

	public CreateClientProject(ParsedWSDLDefinition theParsedDefinition,
			WSOperation selectedOperation) {
		super();
		this.selectedOperation = selectedOperation;
		this.theParsedDefinition = theParsedDefinition;
	}

	public void createProject() {
		final Job job1 = new WorkspaceJob("TEST") {
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				try {
					IProject project = ResourcesPlugin.getWorkspace().getRoot()
							.getProject(selectedOperation.getOperationName());
					if (!project.exists()) {
						project.create(monitor);
						project.open(monitor);
						// Configure the project to be a Java project and a
						// maven project
						IProjectDescription description = project
								.getDescription();
						description.setNatureIds(new String[] {
								JavaCore.NATURE_ID,
								"org.eclipse.m2e.core.maven2Nature" });
						project.setDescription(description, monitor);
						// src
						IFolder src = project.getFolder("src");
						src.create(true, true, monitor);
						// pom file
						IFile pomFile = project.getFile("pom.xml");
						InputStream pomSource = new ByteArrayInputStream(
								createPOMFile().getBytes());
						pomFile.create(pomSource, true, null);
						pomSource.close();

						// src/main
						IFolder srcMain = src.getFolder("main");
						srcMain.create(true, true, monitor);

						// src/main/java
						IFolder srcMainJava = srcMain.getFolder("java");
						srcMainJava.create(true, true, monitor);

						IJavaProject javaProject = JavaCore.create(project);

						// Let's add JavaSE-1.6 to our classpath
						List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
						IExecutionEnvironmentsManager executionEnvironmentsManager = JavaRuntime
								.getExecutionEnvironmentsManager();
						IExecutionEnvironment[] executionEnvironments = executionEnvironmentsManager
								.getExecutionEnvironments();
						for (IExecutionEnvironment iExecutionEnvironment : executionEnvironments) {
							// We will look for JavaSE-1.6 as the JRE container
							// to add to our classpath
							if ("J2SE-1.5"
									.equals(iExecutionEnvironment.getId())) {
								entries.add(JavaCore.newContainerEntry(JavaRuntime
										.newJREContainerPath(iExecutionEnvironment)));
								break;
							}
						}

						// Let's add the maven container to our classpath to let
						// the maven plug-in add the dependencies computed from
						// a pom.xml file to our classpath
						IClasspathEntry mavenEntry = JavaCore
								.newContainerEntry(
										new Path(
												"org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER"),
										new IAccessRule[0],
										new IClasspathAttribute[] { JavaCore
												.newClasspathAttribute(
														"org.eclipse.jst.component.dependency",
														"/WEB-INF/lib") },
										false);
						entries.add(mavenEntry);

						javaProject.setRawClasspath(entries
								.toArray(new IClasspathEntry[entries.size()]),
								null);

						// Let's create our target/classes output folder
						IFolder target = project.getFolder("target");
						target.create(true, true, monitor);

						IFolder classes = target.getFolder("classes");
						classes.create(true, true, monitor);

						// Let's add target/classes as our output folder for
						// compiled ".class"
						javaProject.setOutputLocation(classes.getFullPath(),
								monitor);

						// Now let's add our source folder and output folder to
						// our classpath
						IClasspathEntry[] oldEntries = javaProject
								.getRawClasspath();
						// +1 for our src/main/java entry
						IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
						System.arraycopy(oldEntries, 0, newEntries, 0,
								oldEntries.length);

						IPackageFragmentRoot packageRoot = javaProject
								.getPackageFragmentRoot(srcMainJava);
						newEntries[oldEntries.length] = JavaCore
								.newSourceEntry(packageRoot.getPath(),
										new Path[] {}, new Path[] {},
										classes.getFullPath());

						javaProject.setRawClasspath(newEntries, null);

						// create Activator
						IPackageFragment pack = javaProject
								.getPackageFragmentRoot(srcMainJava)
								.createPackageFragment(
										"org.universAAL."
												+ selectedOperation
														.getOperationName()
														.substring(0, 1)
														.toLowerCase()
												+ selectedOperation
														.getOperationName()
														.substring(1), false,
										null);

						ICompilationUnit cu = pack.createCompilationUnit(
								"Activator.java", createActivator().toString(),
								false, null);
						cu.becomeWorkingCopy(new SubProgressMonitor(monitor, 1));
						formatUnitSourceCode(cu, new SubProgressMonitor(
								monitor, 1));
						cu.commitWorkingCopy(true, new SubProgressMonitor(
								monitor, 1));

						// create Web service client file
						ICompilationUnit client = pack.createCompilationUnit(
								selectedOperation.getOperationName()
										.substring(0, 1).toUpperCase()
										+ selectedOperation.getOperationName()
												.substring(1)
										+ "WebServiceClient.java",
								createClient().toString(), false, null);
						client.becomeWorkingCopy(new SubProgressMonitor(
								monitor, 1));
						formatUnitSourceCode(client, new SubProgressMonitor(
								monitor, 1));
						client.commitWorkingCopy(true, new SubProgressMonitor(
								monitor, 1));

					}
					return Status.OK_STATUS;
				} catch (Exception ex) {
					ex.printStackTrace();
					return new Status(Status.ERROR, Activator.PLUGIN_ID,
							ex.getMessage());
				} finally {
					monitor.done();
				}

			}
		};

		// Get access to workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// Now execute the Jobs

		try {
			// Execute the first job (create maven)
			job1.setRule(MavenPlugin.getProjectConfigurationManager().getRule());
			job1.schedule();
			// MNGECLIPSE-766 wait until new project is created

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private StringBuffer createActivator() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + "org.universAAL."
				+ selectedOperation.getOperationName().substring(0, 1).toLowerCase()
				+ selectedOperation.getOperationName().substring(1) + ";\n");
		buffer.append("\n");
		// buffer.append(source);
		buffer.append("import org.osgi.framework.BundleActivator;\n");
		buffer.append("import org.osgi.framework.BundleContext;\n");
		buffer.append("import org.universAAL.middleware.container.ModuleContext;\n");
		buffer.append("import org.universAAL.middleware.container.osgi.uAALBundleContainer;\n");
		buffer.append("\n");
		buffer.append("public class Activator implements BundleActivator {\n");
		buffer.append("public static BundleContext osgiContext = null;\n");
		buffer.append("public static ModuleContext context = null;\n");
		buffer.append("\n");
		buffer.append("public void start(BundleContext bcontext) throws Exception {\n");
		buffer.append("Activator.osgiContext = bcontext;\n");
		buffer.append("Activator.context = uAALBundleContainer.THE_CONTAINER.registerModule(new Object[] { bcontext });\n");
		buffer.append("}\n");
		buffer.append("\n");
		buffer.append("public void stop(BundleContext arg0) throws Exception {\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("\n");
		return buffer;

	}

	private String createPOMFile() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		buffer.append("    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n");
		buffer.append("  <modelVersion>4.0.0</modelVersion>\n");
		buffer.append("  <groupId>org.universAAL.AALapplication</groupId>\n");
		buffer.append("  <artifactId>" + selectedOperation.getOperationName()
				+ "</artifactId>\n");
		buffer.append("  <version>1.0-SNAPSHOT</version>\n");

		buffer.append("  <name>" + selectedOperation.getOperationName()
				+ "</name>\n");
		if (selectedOperation.getHasDocumentation() != null)
			buffer.append("  <description>"
					+ selectedOperation.getHasDocumentation()
					+ "</description>\n");
		buffer.append("  <packaging>bundle</packaging>\n");

		buffer.append("  <dependencies>\n");

		addDependency(buffer, "org.osgi", "org.osgi.core", "4.1.0");
		addDependency(buffer, "org.universAAL.middleware",
				"mw.data.representation.osgi", "3.4.0");
		addDependency(buffer, "org.universAAL.middleware", "mw.bus.model.osgi",
				"3.4.0");
		addDependency(buffer, "org.universAAL.middleware",
				"mw.container.xfaces.osgi", "3.4.0");
		addDependency(buffer, "org.universAAL.middleware", "mw.container.osgi",
				"3.4.0");
		addDependency(buffer, "org.universAAL.middleware",
				"mw.bus.service.osgi", "3.4.0");
		addDependency(buffer, "org.universAAL.middleware",
				"mw.bus.context.osgi", "3.4.0");
		addDependency(buffer, "org.universAAL.ontology", "ont.phWorld", "3.4.0");
		addDependency(buffer, "org.universAAL.remote", "ri.internetgateway",
				"3.4.0");
		addDependency(buffer, "org.apache.axis2", "org.apache.axis2.osgi",
				"1.5.2");
		addDependency(buffer, "xerces", "xercesImpl", "2.10.0");
		addDependency(buffer, "org.apache.axis", "axis", "1.4");

		buffer.append("  </dependencies>\n");
		buffer.append("  <build>\n");
		buffer.append("  <plugins>\n");
		buffer.append("  	<plugin>\n");
		buffer.append("  		<groupId>org.apache.felix</groupId>\n");
		buffer.append("  		<artifactId>maven-bundle-plugin</artifactId>\n");
		buffer.append("  		<version>2.3.7</version>\n");
		buffer.append("  		<extensions>true</extensions>\n");
		buffer.append("  		<configuration>\n");
		buffer.append("  			<instructions>\n");
		buffer.append("  				<Bundle-Name>${project.name}</Bundle-Name>\n");
		buffer.append("  				<Bundle-Activator>" + "org.universAAL."
				+ selectedOperation.getOperationName()
				+ ".Activator</Bundle-Activator>\n");
		buffer.append("  				<Bundle-Description>${project.description}</Bundle-Description>\n");
		buffer.append("  				<Bundle-SymbolicName>${project.artifactId}</Bundle-SymbolicName>\n");
		buffer.append("  			</instructions>\n");
		buffer.append("  		</configuration>\n");
		buffer.append("  	</plugin>\n");
		buffer.append("  </plugins>\n");
		buffer.append("  </build>\n");
		buffer.append("  <repositories>\n");
		buffer.append("  <repository>\n");
		buffer.append("  <id>central</id>\n");
		buffer.append("  <name>Central Maven Repository</name>\n");
		buffer.append("  <url>http://repo1.maven.org/maven2</url>\n");
		buffer.append("  	<snapshots>\n");
		buffer.append("  				<enabled>false</enabled>\n");
		buffer.append("  			</snapshots>\n");
		buffer.append("  		</repository>\n");
		buffer.append("  		<repository>\n");
		buffer.append("  			<id>apache-snapshots</id>\n");
		buffer.append("  			<name>Apache Snapshots</name>\n");
		buffer.append("  			<url>http://people.apache.org/repo/m2-snapshot-repository</url>\n");
		buffer.append("  			<releases>\n");
		buffer.append("  				<enabled>false</enabled>\n");
		buffer.append("  			</releases>\n");
		buffer.append("  			<snapshots>\n");
		buffer.append("  				<updatePolicy>daily</updatePolicy>\n");
		buffer.append("  			</snapshots>\n");
		buffer.append("  		</repository>\n");
		buffer.append("  		<repository>\n");
		buffer.append("  			<id>uaal</id>\n");
		buffer.append("  			<name>universAAL Repositories</name>\n");
		buffer.append("  			<url>http://depot.universaal.org/maven-repo/releases/</url>\n");
		buffer.append("  			<snapshots>\n");
		buffer.append("  				<enabled>false</enabled>\n");
		buffer.append("  			</snapshots>\n");
		buffer.append("  		</repository>\n");
		buffer.append("  		<repository>\n");
		buffer.append("  			<id>uaal-snapshots</id>\n");
		buffer.append("  			<name>universAAL Snapshot Repositories</name>\n");
		buffer.append("  			<url>http://depot.universaal.org/maven-repo/snapshots/</url>\n");
		buffer.append("  			<releases>\n");
		buffer.append("  				<enabled>false</enabled>\n");
		buffer.append("  			</releases>\n");
		buffer.append("  		</repository>\n");
		buffer.append("  	</repositories>\n");
		buffer.append("</project>\n");
		return buffer.toString();
	}

	private void addDependency(StringBuilder buffer, String groupId,
			String artifactId, String version) {
		buffer.append("    <dependency>\n").append("      <groupId>")
				.append(groupId).append("</groupId>\n")
				.append("      <artifactId>").append(artifactId)
				.append("</artifactId>\n").append("      <version>")
				.append(version).append("</version>\n")
				.append("    </dependency>\n");
	}

	public static void formatUnitSourceCode(ICompilationUnit unit,
			IProgressMonitor monitor) throws JavaModelException {
		CodeFormatter formatter = ToolFactory.createCodeFormatter(null);
		ISourceRange range = unit.getSourceRange();
		TextEdit formatEdit = formatter.format(
				CodeFormatter.K_COMPILATION_UNIT, unit.getSource(),
				range.getOffset(), range.getLength(), 0, null);
		if (formatEdit != null && formatEdit.hasChildren()) {
			unit.applyTextEdit(formatEdit, monitor);
		} else {
			monitor.done();
		}
	}

	private StringBuffer createClient() {

		StringBuffer buffer = new StringBuffer();
		buffer.append(
				"package " + "org.universAAL." + selectedOperation.getOperationName().substring(0, 1).toLowerCase()
						+ selectedOperation.getOperationName().substring(1) + ";\n");
		buffer.append("\n");
		buffer.append("/*\n");
		buffer.append(" * This Java class has been automatically generated by the External Service Integrator universAAL plugin.\n");
		buffer.append(" * It provides the necessary code for parsing and invoking a SOAP web service.\n");
		buffer.append(" * For the successful invocation of a web service the developer should fill in the required input parameter values\n");
		buffer.append(" * of the method fillInInputValues noted as \"yourValue\". The output values can be retrieved by the getOutputValue\n");
		buffer.append(" * method with parameters the output parameter name of the web service.\n");
		buffer.append(" * It should be noted that currently the array web service parameters are not supported.\n");
		buffer.append(" */\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("import java.util.Vector;\n");
		buffer.append("import org.universAAL.ri.internetgateway.InternetGateway;\n");
		buffer.append("import org.universAAL.ri.wsdlToolkit.invocation.Axis2WebServiceInvoker;\n");
		buffer.append("import org.universAAL.ri.wsdlToolkit.invocation.InvocationResult;\n");
		buffer.append("import org.universAAL.ri.wsdlToolkit.ioApi.ComplexObject;\n");
		buffer.append("import org.universAAL.ri.wsdlToolkit.ioApi.NativeObject;\n");
		buffer.append("import org.universAAL.ri.wsdlToolkit.ioApi.ParsedWSDLDefinition;\n");
		buffer.append("import org.universAAL.ri.wsdlToolkit.ioApi.WSOperation;\n");
		buffer.append("import org.universAAL.ri.wsdlToolkit.ioApi.WSOperationInput;\n");
		buffer.append("import org.universAAL.ri.wsdlToolkit.ioApi.WSOperationOutput;\n");
		buffer.append("\n");
		buffer.append("public class "
				+ selectedOperation.getOperationName().substring(0, 1)
						.toUpperCase()
				+ selectedOperation.getOperationName().substring(1)
				+ "WebServiceClient" + " {\n");
		buffer.append("private WSOperation wsoperation = null;\n");
		buffer.append("private ParsedWSDLDefinition parsedWSDLDefinition = null;\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("//constructor\n");
		buffer.append("public "
				+ selectedOperation.getOperationName().substring(0, 1)
						.toUpperCase()
				+ selectedOperation.getOperationName().substring(1)
				+ "WebServiceClient" + "() {\n");
		buffer.append("InternetGateway.registerWebService(\""
				+ theParsedDefinition.getWsdlURL() + "\");\n");
		buffer.append("parsedWSDLDefinition = InternetGateway.getWebServiceDefinition(\""
				+ theParsedDefinition.getWsdlURL() + "\");\n");
		buffer.append("for (int i = 0; i < parsedWSDLDefinition.getWsdlOperations().size(); i++) {\n");
		buffer.append("if (((WSOperation) parsedWSDLDefinition.getWsdlOperations().get(i)).getOperationName().equals(\""
				+ selectedOperation.getOperationName() + "\")) {\n");
		buffer.append("wsoperation = ((WSOperation) parsedWSDLDefinition.getWsdlOperations().get(i));\n");
		buffer.append("break;\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("\n");
		buffer.append("//Method for invoking the web service\n");
		buffer.append("public boolean invokeWebService() {\n");
		buffer.append("try {\n");
		buffer.append("InvocationResult invocationResult = Axis2WebServiceInvoker.invokeWebService(wsoperation, parsedWSDLDefinition);\n");
		buffer.append("return true;\n");
		buffer.append("} catch (Exception ex) {\n");
		buffer.append("ex.printStackTrace();\n");
		buffer.append("return false;\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("\n");
		buffer.append("//Method for filling in the input values of the web service before the invocation\n");
		buffer.append("public void fillInInputValues() {\n");
		buffer.append("WSOperationInput input = wsoperation.getHasInput();\n");
		List<String> inputList = new ArrayList<String>();
		getInputsOfOperationAsList(inputList, selectedOperation.getHasInput()
				.getHasNativeOrComplexObjects());
		for (int i = 0; i < inputList.size(); i++) {
			buffer.append("// TODO: add input value for input parameter: "
					+ inputList.get(i) + "\n");
			buffer.append("addInputValue(input.getHasNativeOrComplexObjects(), \""
					+ inputList.get(i) + "\",\"yourValue\");\n");
		}

		buffer.append("}\n");
		buffer.append("\n");
		buffer.append("public void addInputValue(Vector inputVector, String inputParameterName,String inputParameterValue) {\n");
		buffer.append("for (int i = 0; i < inputVector.size(); i++) {\n");
		buffer.append("if (inputVector.get(i) instanceof NativeObject) {\n");
		buffer.append("NativeObject no = (NativeObject) inputVector.get(i);\n");
		buffer.append("if (no.getObjectName().getLocalPart().equals(inputParameterName)) {\n");
		buffer.append("no.setHasValue(inputParameterValue);\n");
		buffer.append("return;\n");
		buffer.append("}\n");
		buffer.append("} else if (inputVector.get(i) instanceof ComplexObject) {\n");
		buffer.append("ComplexObject co = (ComplexObject) inputVector.get(i);\n");
		buffer.append("addInputValue(co.getHasNativeObjects(), inputParameterName,inputParameterValue);\n");
		buffer.append("addInputValue(co.getHasComplexObjects(), inputParameterName,inputParameterValue);\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("public String getOutputValue(String outputParameterName) {\n");
		buffer.append("WSOperationOutput output = wsoperation.getHasOutput();\n");
		buffer.append("for (int i = 0; i < output.getHasNativeOrComplexObjects().size(); i++) {\n");
		buffer.append("if (output.getHasNativeOrComplexObjects().get(i) instanceof NativeObject) {\n");
		buffer.append("NativeObject no = (NativeObject) output.getHasNativeOrComplexObjects().get(i);\n");
		buffer.append("if (no.getObjectName().getLocalPart().equals(outputParameterName)) {\n");
		buffer.append("return no.getHasValue();\n");
		buffer.append("}\n");
		buffer.append("} else if (output.getHasNativeOrComplexObjects().get(i) instanceof ComplexObject) {\n");
		buffer.append("ComplexObject co = (ComplexObject) output.getHasNativeOrComplexObjects().get(i);\n");
		buffer.append("String res1 = getOutputValueRecursively(co.getHasNativeObjects(), outputParameterName);\n");
		buffer.append("if (!res1.equals(\"\")) {\n");
		buffer.append("return res1;\n");
		buffer.append("} else {\n");
		buffer.append("String res2 = getOutputValueRecursively(co.getHasComplexObjects(), outputParameterName);\n");
		buffer.append("if (!res2.equals(\"\")) {\n");
		buffer.append("return res1;\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("return \"\";\n");
		buffer.append("}\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("public String getOutputValueRecursively(Vector vec,String outputParameterName) {\n");
		buffer.append("for (int i = 0; i < vec.size(); i++) {\n");
		buffer.append("if (vec.get(i) instanceof NativeObject) {\n");
		buffer.append("NativeObject no = (NativeObject) vec.get(i);\n");
		buffer.append("if (no.getObjectName().getLocalPart().equals(outputParameterName)) {\n");
		buffer.append("return no.getHasValue();\n");
		buffer.append("}\n");
		buffer.append("} else if (vec.get(i) instanceof ComplexObject) {\n");
		buffer.append("ComplexObject co = (ComplexObject) vec.get(i);\n");
		buffer.append("String res1 = getOutputValueRecursively(co.getHasNativeObjects(), outputParameterName);\n");
		buffer.append("if (!res1.equals(\"\")) {\n");
		buffer.append("return res1;\n");
		buffer.append("} else {\n");
		buffer.append("String res2 = getOutputValueRecursively(co.getHasComplexObjects(), outputParameterName);\n");
		buffer.append("if (!res2.equals(\"\")) {\n");
		buffer.append("return res1;\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("return \"\";\n");
		buffer.append("}\n");
		buffer.append("\n");

		buffer.append("\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("//main method for testing purposes\n");
		buffer.append("public static void main(String [] args)\n");
		buffer.append("{\n");
		buffer.append(selectedOperation.getOperationName().substring(0, 1)
				.toUpperCase()
				+ selectedOperation.getOperationName().substring(1)
				+ "WebServiceClient client=new "
				+ selectedOperation.getOperationName().substring(0, 1)
						.toUpperCase()
				+ selectedOperation.getOperationName().substring(1)
				+ "WebServiceClient" + "();\n");
		buffer.append("//provide input for the web service\n");
		buffer.append("client.fillInInputValues();\n");
		buffer.append("if(client.invokeWebService()){\n");
		buffer.append("//get the output parameters by the method getOutputValue\n");
		buffer.append("}\n");
		buffer.append("}\n");
		buffer.append("\n");
		buffer.append("}\n");

		return buffer;

	}

	private void getInputsOfOperationAsList(List<String> list, Vector vec) {
		for (int i = 0; i < vec.size(); i++) {
			if (vec.get(i) instanceof NativeObject) {
				NativeObject no = (NativeObject) vec.get(i);
				list.add(no.getObjectName().getLocalPart());

			} else if (vec.get(i) instanceof ComplexObject) {
				ComplexObject co = (ComplexObject) vec.get(i);
				getInputsOfOperationAsList(list, co.getHasNativeObjects());
				getInputsOfOperationAsList(list, co.getHasComplexObjects());
			}
		}
	}

}
