/*
	Copyright 2012 CERTH, http://www.certh.gr
	
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
package org.universaal.tools.owl2uml.uml2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResourceFactory;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author joemoul, billyk
 * 
 */
public class UML2Factory {
	private static String packageName;
	private static String modelName;
	private org.eclipse.uml2.uml.Package rootModel;
	private org.eclipse.uml2.uml.Package owl2Model;
	private Profile profileTypes;
	private PrimitiveType booleanPrimitive, stringPrimitive, integerPrimitive,
			unlimitedNaturalPrimitive;
	public File file;
	public static Map<String, String> XMLHeader = new HashMap<String, String>();
	public static Map<String, String> XMLAbstractClasses = new HashMap<String, String>();
	protected static final ResourceSet RESOURCE_SET = new ResourceSetImpl();
	public static String XMLFilePath;
	public static String XMLModelName;
	public static String XMLPackageName;

	public UML2Factory(String owlURI) {
		/*
		 * if (!readXML()) { System.out
		 * .println("Additional properties from XML file were Ignored!"); } ;
		 */
		readXML();
		modelName = XMLModelName;
		packageName = XMLPackageName;

		System.out.println("Creating model...");

		owl2Model = createModel(modelName, owlURI);

		profileTypes = createProfile("profileTypes");
		booleanPrimitive = importPrimitiveType(profileTypes, "Boolean");
		stringPrimitive = importPrimitiveType(profileTypes, "String");
		integerPrimitive = importPrimitiveType(profileTypes, "Integer");
		unlimitedNaturalPrimitive = importPrimitiveType(profileTypes,
				"UnlimitedNatural");
		defineProfile(profileTypes);
		applyProfile(owl2Model, profileTypes);

		PackageImport prims = UMLFactory.eINSTANCE.createPackageImport();
		prims.setImportedPackage(owl2Model);

		rootModel = owl2Model;
		owl2Model = (org.eclipse.uml2.uml.Package) getPackage(rootModel,
				packageName);
		createClass(owl2Model, "Thing", false);
	}

	protected static ModelSet createAndInitResourceSet() {
		ModelSet resourceSet = new ModelSet();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(".notation", new GMFResourceFactory());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(".uml", new GMFResourceFactory());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(".di", new GMFResourceFactory());
		return resourceSet;
	}

	public static Model createModel(String name, String owlURI) {

		ResourceSet resourceSet = createAndInitResourceSet();

		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI,
				EcorePackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI,
				UMLPackage.eINSTANCE);

		URL url = null;
		try {
			url = new URL(
					"platform:/plugin/org.universaal.tools.owl2uml/profiles/model.uml");
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}

			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		java.net.URI javaURI = null;
		try {
			javaURI = url.toURI();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// URI uriPath = URI.createFileURI("../../profiles/model.uml");
		org.eclipse.emf.common.util.URI URIPath = org.eclipse.emf.common.util.URI
				.createURI(javaURI.toString());

		Resource resource = resourceSet.getResource(URIPath, true);
		// Resource resource = resourceSet.getResource(uriPath, true);
		EcoreUtil.resolveAll(resourceSet);
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		replaceMap.put("$$MODEL_NAME$$", name);
		replaceMap.put("$$PACKAGE_NAME$$", packageName);

		for (Iterator it = resourceSet.getAllContents(); it.hasNext();) {
			Object element = it.next();
			if (element instanceof NamedElement) {
				String newName = replaceMap.get(((NamedElement) element)
						.getName());
				if (newName != null) {
					((NamedElement) element).setName(newName);
				}
				if (element instanceof org.eclipse.uml2.uml.Model) {
					EList<Comment> c = ((NamedElement) element)
							.getOwnedComments();
					Comment comm = null;
					if (!c.isEmpty()) {
						comm = c.get(0);
					}
					if (comm != null) {
						comm.setBody(XMLHeader.get("XMLComment"));
					}
				}
				if (element instanceof org.eclipse.uml2.uml.Package) {
					Stereotype s = ((NamedElement) element)
							.getAppliedStereotype("OWL::owlOntology");

					if (s != null) {

						UMLUtil.setTaggedValue((Element) element, s,
								"defaultNamespace", owlURI);
						UMLUtil.setTaggedValue((Element) element, s,
								"versionInfo", XMLHeader.get("XMLVersionInfo"));

					}
				}
			}
		}

		EList<EObject> el = null;
		try {
			resource.load(null);
			el = resource.getContents();
		} catch (Exception e) {
			System.out.println("failed to load content of file : " + URIPath);
		}

		Model model = (Model) EcoreUtil.getObjectByType(el,
				UMLPackage.eINSTANCE.getModel());

		System.out.println("Model '" + model.getQualifiedName() + "' created.");

		return model;
	}

	protected static org.eclipse.uml2.uml.Package createPackage(
			org.eclipse.uml2.uml.Package nestingPackage, String name) {
		org.eclipse.uml2.uml.Package package_ = nestingPackage
				.createNestedPackage(name);

		System.out.println("Package '" + package_.getQualifiedName()
				+ "' created.");

		return package_;
	}

	protected static PrimitiveType createPrimitiveType(
			org.eclipse.uml2.uml.Package package_, String name) {
		PrimitiveType primitiveType = (PrimitiveType) package_
				.createOwnedPrimitiveType(name);

		System.out.println("Primitive type '"
				+ primitiveType.getQualifiedName() + "' created.");

		return primitiveType;
	}

	protected static Enumeration createEnumeration(
			org.eclipse.uml2.uml.Package package_, String name) {
		Enumeration enumeration = (Enumeration) package_
				.createOwnedEnumeration(name);

		System.out.println("Enumeration '" + enumeration.getQualifiedName()
				+ "' created.");

		return enumeration;
	}

	protected static EnumerationLiteral createEnumerationLiteral(
			Enumeration enumeration, String name) {
		EnumerationLiteral enumerationLiteral = enumeration
				.createOwnedLiteral(name);

		System.out.println("Enumeration literal '"
				+ enumerationLiteral.getQualifiedName() + "' created.");

		return enumerationLiteral;
	}

	public static Generalization createGeneralization(
			Classifier specificClassifier, Classifier generalClassifier) {
		Generalization generalization = specificClassifier
				.createGeneralization(generalClassifier);

		System.out.println("Generalization "
				+ specificClassifier.getQualifiedName() + " ->> "
				+ generalClassifier.getQualifiedName() + " created.");

		return generalization;
	}

	protected static org.eclipse.uml2.uml.Classifier getClass(
			org.eclipse.uml2.uml.Package package_, String name) {
		PackageableElement pe = null;
		System.out.println("Trying to get Class with name " + name);

		pe = package_.getPackagedElement(name);
		if (pe != null) {
			System.out.println("Trying to get Class with name '" + name
					+ "'... Found:" + pe.getQualifiedName());
		}

		return (Classifier) pe;
	}

	protected static org.eclipse.uml2.uml.Package getPackage(
			org.eclipse.uml2.uml.Package package_, String name) {
		PackageableElement pe = null;
		System.out.println("Trying to get Class with name " + name);

		pe = package_.getPackagedElement(name);
		if (pe != null) {
			System.out.println("Trying to get Class with name '" + name
					+ "'... Found:" + pe.getQualifiedName());
		}

		return (org.eclipse.uml2.uml.Package) pe;
	}

	public void write(String file) throws IOException {

		System.out.println("Saving model to file: " + file);
		/*
		 * String umlPath = file.substring(file.lastIndexOf("/") + 1,
		 * (file.lastIndexOf("\\") + 1)); String umlFile =
		 * file.substring(file.lastIndexOf("\\") + 1, file.lastIndexOf("."));
		 */
		save(rootModel, URI.createFileURI(file));
		// URI.createFileURI(umlPath).appendSegment(umlFile)
		// .appendFileExtension(UMLResource.FILE_EXTENSION));
	}

	public void createClass(String name, String nameParent) {

		if (nameParent != null) {

			createClass(owl2Model, name, false);

		}

	}

	public static org.eclipse.uml2.uml.Classifier createClass(
			org.eclipse.uml2.uml.Package package_, String name,
			boolean isAbstract) {
		if (XMLAbstractClasses.get(name) != null) {
			org.eclipse.uml2.uml.Class class_ = package_.createOwnedClass(name,
					true);
			System.out.println("Abstract Class '" + class_.getQualifiedName()
					+ "' created.");
			return class_;
		} else {
			org.eclipse.uml2.uml.Class class_ = package_.createOwnedClass(name,
					isAbstract);

			System.out.println("Class '" + class_.getQualifiedName()
					+ "' created.");
			return class_;
		}

	}

	public void createClass(String name, String nameParent,
			Iterator dataProperties) {

		if (nameParent != null) {

			if (getClass(owl2Model, name) == null) {
				createClass(owl2Model, name, false);
			}
			if (getClass(owl2Model, nameParent) == null) {
				createClass(owl2Model, nameParent, false);
			}

			createGeneralization(getClass(owl2Model, name),
					getClass(owl2Model, nameParent));

		}

		// adding data-properties
		if (dataProperties != null) {
			if (getClass(owl2Model, name) == null) {
				createClass(owl2Model, name, false);
			}
			while (dataProperties.hasNext()) {
				String nameProp = (String) dataProperties.next();
				System.out.println("\t\t adding property: " + nameProp);

				String typeOfProp = nameProp
						.substring(nameProp.indexOf('#') + 1);
				String nameOfProp = nameProp
						.substring(0, nameProp.indexOf('#'));
				String enumerations[] = null;
				if (typeOfProp.indexOf("#") > 0) {
					enumerations = typeOfProp.split("#");

					Enumeration enumer = createEnumeration(owl2Model,
							nameOfProp + "Enumeration");
					for (int jj = 0; jj < enumerations.length; jj++) {
						createEnumerationLiteral(enumer, enumerations[jj]);
					}

					createAttribute((Class) getClass(owl2Model, name),
							nameOfProp, enumer, 0, 1);
				} else {

					if (typeOfProp.equals("boolean")) {
						createAttribute((Class) getClass(owl2Model, name),
								nameOfProp, booleanPrimitive, 0, 1);
					} else if (typeOfProp.equals("string")) {
						createAttribute((Class) getClass(owl2Model, name),
								nameOfProp, stringPrimitive, 0, 1);
					} else if (typeOfProp.equals("int")) {
						createAttribute((Class) getClass(owl2Model, name),
								nameOfProp, integerPrimitive, 0, 1);
					} else if ((PrimitiveType) getClass(owl2Model, typeOfProp) != null) {
						createAttribute(
								(Class) getClass(owl2Model, name),
								nameOfProp,
								(PrimitiveType) getClass(owl2Model, typeOfProp),
								0, 1);
					} else {

						createAttribute((Class) getClass(owl2Model, name),
								nameOfProp, unlimitedNaturalPrimitive, 0, 1);
					}
				}

			}
		}

	}

	/**
	 * Creating class with multiple parents.
	 * 
	 * @param name
	 * @param parents
	 * @param dataProperties
	 */
	public void createClass(String name, Iterator<String> parents,
			Iterator<String> dataProperties) {

		// adding data-properties
		if (dataProperties != null) {
			if (getClass(owl2Model, name) == null) {
				createClass(owl2Model, name, false);
			}
			while (dataProperties.hasNext()) {
				String nameProp = (String) dataProperties.next();
				System.out.println("\t\t adding property: " + nameProp);

				String typeOfProp = nameProp
						.substring(nameProp.indexOf('#') + 1);
				String nameOfProp = nameProp
						.substring(0, nameProp.indexOf('#'));
				String enumerations[] = null;
				if (typeOfProp.indexOf("#") > 0) {
					enumerations = typeOfProp.split("#");

					Enumeration enumer = createEnumeration(owl2Model,
							nameOfProp + "Enumeration");
					for (int jj = 0; jj < enumerations.length; jj++) {
						createEnumerationLiteral(enumer, enumerations[jj]);
					}

					createAttribute((Class) getClass(owl2Model, name),
							nameOfProp, enumer, 0, 1);
				} else {
					if (typeOfProp.equals("boolean")) {
						createAttribute((Class) getClass(owl2Model, name),
								nameOfProp, booleanPrimitive, 0, 1);
					} else if (typeOfProp.equals("string")) {
						createAttribute((Class) getClass(owl2Model, name),
								nameOfProp, stringPrimitive, 0, 1);
					} else if (typeOfProp.equals("int")) {
						createAttribute((Class) getClass(owl2Model, name),
								nameOfProp, integerPrimitive, 0, 1);
					} else if ((PrimitiveType) getClass(owl2Model, typeOfProp) != null) {
						createAttribute(
								(Class) getClass(owl2Model, name),
								nameOfProp,
								(PrimitiveType) getClass(owl2Model, typeOfProp),
								0, 1);
					} else {

						createAttribute((Class) getClass(owl2Model, name),
								nameOfProp, unlimitedNaturalPrimitive, 0, 1);
					}
				}

			}
		}

		// String generalization = "";
		while (parents.hasNext()) {

			String nameParent = parents.next();

			if (getClass(owl2Model, name) == null) {
				createClass(owl2Model, name, false);
			}

			if (getClass(owl2Model, nameParent) == null) {
				createClass(owl2Model, nameParent, false);
			}

			createGeneralization(getClass(owl2Model, name),
					getClass(owl2Model, nameParent));

		}

	}

	public void createObjectProperties(
			Iterator<org.universaal.tools.owl2uml.core.ObjectPropertyRepresentation> iterator) {
		while (iterator.hasNext()) {
			org.universaal.tools.owl2uml.core.ObjectPropertyRepresentation obj = iterator
					.next();

			String name = obj.getLocalName();
			// System.out.println(" CREATING OBJECT: " + name);

			Iterator<String> domains = obj.getDomains();
			if (domains != null) {
				while (domains.hasNext()) {
					String domain = domains.next();
					// System.out.println(" \t Domain " + domain);
					if (domain == null) {
						System.out
								.println("[Warning] Domain property not specified: "
										+ name);
						break;
					}
					Iterator<String> ranges = obj.getRanges();
					while (ranges.hasNext()) {
						String range = ranges.next();
						// System.out.println("\t\t Range " + range);

						createAssociation((Class) getClass(owl2Model, domain),
								true, AggregationKind.NONE_LITERAL, name,
								Integer.valueOf(obj.getCardinalityTarget()[0]),
								Integer.valueOf(obj.getCardinalityTarget()[1]),
								getClass(owl2Model, range), false,
								AggregationKind.NONE_LITERAL, "",
								Integer.valueOf(obj.getCardinalitySource()[0]),
								Integer.valueOf(obj.getCardinalitySource()[1]));
					}

				}
			}
		}

	}

	public static Profile createProfile(String name) {
		Profile profile = UMLFactory.eINSTANCE.createProfile();
		profile.setName(name);

		System.out.println("Profile '" + profile.getQualifiedName()
				+ "' created.");

		return profile;
	}

	public static PrimitiveType importPrimitiveType(
			org.eclipse.uml2.uml.Package package_, String name) {
		Model umlLibrary = (Model) load(URI
				.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI));

		PrimitiveType primitiveType = (PrimitiveType) umlLibrary
				.getOwnedType(name);

		package_.createElementImport(primitiveType);

		System.out.println("Primitive type '"
				+ primitiveType.getQualifiedName() + "' imported.");

		return primitiveType;
	}

	protected static void defineProfile(Profile profile) {
		profile.define();

		System.out.println("Profile '" + profile.getQualifiedName()
				+ "' defined.");
	}

	protected static void applyProfile(org.eclipse.uml2.uml.Package package_,
			Profile profile) {
		package_.applyProfile(profile);

		System.out
				.println("Profile '" + profile.getQualifiedName()
						+ "' applied to package '"
						+ package_.getQualifiedName() + "'.");
	}

	protected static Property createAttribute(
			org.eclipse.uml2.uml.Class class_, String name, Type type,
			int lowerBound, int upperBound) {
		Property attribute = class_.createOwnedAttribute(name, type,
				lowerBound, upperBound);

		if (attribute instanceof org.eclipse.uml2.uml.Property) {
			Stereotype s = attribute
					.getApplicableStereotype("OWL::datatypeProperty");
			attribute.applyStereotype(s);

			type.getQualifiedName();
			String pt = type.getQualifiedName();
			if (s != null) {
				if (pt.equals("UMLPrimitiveTypes::UnlimitedNatural")) {
					UMLUtil.setTaggedValue(attribute, s, "isFunctional", false);
				} else {
					UMLUtil.setTaggedValue(attribute, s, "isFunctional", true);
				}

			}
		}

		StringBuffer sb = new StringBuffer();

		sb.append("Attribute '");

		sb.append(attribute.getQualifiedName());

		sb.append("' : ");

		sb.append(type.getQualifiedName());

		sb.append(" [");
		sb.append(lowerBound);
		sb.append("..");
		sb.append(LiteralUnlimitedNatural.UNLIMITED == upperBound ? "*"
				: String.valueOf(upperBound));
		sb.append("]");

		sb.append(" created.");

		System.out.println(sb.toString());

		return attribute;
	}

	protected static Association createAssociation(Type type1,
			boolean end1IsNavigable, AggregationKind end1Aggregation,
			String end1Name, int end1LowerBound, int end1UpperBound,
			Type type2, boolean end2IsNavigable,
			AggregationKind end2Aggregation, String end2Name,
			int end2LowerBound, int end2UpperBound) {

		Association association = type1.createAssociation(end1IsNavigable,
				end1Aggregation, end1Name, end1LowerBound, end1UpperBound,
				type2, end2IsNavigable, end2Aggregation, end2Name,
				end2LowerBound, end2UpperBound);

		StringBuffer sb = new StringBuffer();

		sb.append("Association ");

		if (null == end1Name || 0 == end1Name.length()) {
			sb.append('{');
			sb.append(type1.getQualifiedName());
			sb.append('}');
		} else {
			sb.append("'");
			sb.append(type1.getQualifiedName());
			sb.append(NamedElement.SEPARATOR);
			sb.append(end1Name);
			sb.append("'");
		}

		sb.append(" [");
		sb.append(end1LowerBound);
		sb.append("..");
		sb.append(LiteralUnlimitedNatural.UNLIMITED == end1UpperBound ? "*"
				: String.valueOf(end1UpperBound));
		sb.append("] ");

		sb.append(end2IsNavigable ? '<' : '-');
		sb.append('-');
		sb.append(end1IsNavigable ? '>' : '-');
		sb.append(' ');

		if (null == end2Name || 0 == end2Name.length()) {
			sb.append('{');
			sb.append(type2.getQualifiedName());
			sb.append('}');
		} else {
			sb.append("'");
			sb.append(type2.getQualifiedName());
			sb.append(NamedElement.SEPARATOR);
			sb.append(end2Name);
			sb.append("'");
		}

		sb.append(" [");
		sb.append(end2LowerBound);
		sb.append("..");
		sb.append(LiteralUnlimitedNatural.UNLIMITED == end2UpperBound ? "*"
				: String.valueOf(end2UpperBound));
		sb.append("]");

		sb.append(" created.");

		System.out.println(sb.toString());

		return association;
	}

	/*
	 * public static void registerPathmaps(URI uri) {
	 * URIConverter.URI_MAP.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP),
	 * uri.appendSegment("libraries").appendSegment(""));
	 * 
	 * URIConverter.URI_MAP.put(URI.createURI(UMLResource.METAMODELS_PATHMAP),
	 * uri.appendSegment("metamodels").appendSegment(""));
	 * 
	 * URIConverter.URI_MAP.put(URI.createURI(UMLResource.PROFILES_PATHMAP),
	 * uri.appendSegment("profiles").appendSegment("")); }
	 */

	public static void save(org.eclipse.uml2.uml.Package package_, URI uri) {
		Resource resource = RESOURCE_SET.createResource(uri);
		EList contents = resource.getContents();

		contents.add(package_);

		for (Iterator allContents = UMLUtil.getAllContents(package_, true,
				false); allContents.hasNext();) {

			EObject eObject = (EObject) allContents.next();

			if (eObject instanceof Element) {
				contents.addAll(((Element) eObject).getStereotypeApplications());
			}
		}

		try {
			resource.save(null);

			System.out.println("Done.");
		} catch (IOException ioe) {
			err(ioe.getMessage());
		}
	}

	protected static org.eclipse.uml2.uml.Package load(URI uri) {
		org.eclipse.uml2.uml.Package package_ = null;

		try {
			Resource resource = RESOURCE_SET.getResource(uri, true);

			package_ = (org.eclipse.uml2.uml.Package) EcoreUtil
					.getObjectByType(resource.getContents(),
							UMLPackage.Literals.PACKAGE);
		} catch (WrappedException we) {
			err(we.getMessage());
			System.exit(1);
		}

		return package_;
	}

	protected static void err(String error) {
		System.err.println(error);
	}

	public static void createXML() {

		InputStream inputStream = null;
		OutputStream outputStream = null;
		URL url;
		try {

			url = new URL(
					"platform:/plugin/org.universaal.tools.owl2uml/samples/empty.xml");
			inputStream = url.openConnection().getInputStream();

			File target = new File(XMLFilePath);

			outputStream = new FileOutputStream(target);

			byte[] buffer = new byte[1024];

			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}

			if (inputStream != null)

				inputStream.close();
			if (outputStream != null)

				outputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read and validate XML file before transformation
	 * 
	 */
	public static boolean readXML() {

		try {

			File XMLFile = new File(XMLFilePath);

			XMLFile = new File(XMLFilePath);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			dbFactory.setNamespaceAware(true);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XMLFile);

			doc.getDocumentElement().normalize();
			String validXML = XMLvalidate.validateXML(doc);
			if (!validXML.equals("OK")) {
				System.out.println(validXML);
				return false;

			} else {
				System.out.println("XML Validated against Schema");
			}

			System.out.println("for ontology "
					+ doc.getDocumentElement().getAttribute("Name")
					+ " of "
					+ doc.getDocumentElement().getAttribute("Package")
					+ " Version Info: "
					+ doc.getDocumentElement().getAttribute("versionInfo")
					+ " Comment: "
					+ doc.getElementsByTagName("Comment").item(0)
							.getFirstChild().getNodeValue());

			XMLModelName = doc.getDocumentElement().getAttribute("Name");
			XMLPackageName = doc.getDocumentElement().getAttribute("Package");

			XMLHeader.put("XMLName",
					doc.getDocumentElement().getAttribute("Name"));
			XMLHeader.put("XMLPackage",
					doc.getDocumentElement().getAttribute("Package"));
			XMLHeader.put("XMLVersionInfo", doc.getDocumentElement()
					.getAttribute("versionInfo"));
			XMLHeader.put("XMLComment", doc.getElementsByTagName("Comment")
					.item(0).getFirstChild().getNodeValue());

			NodeList nList = doc.getElementsByTagName("Name");
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					System.out.println(" Abstract Class Name: "
							+ nNode.getFirstChild().getNodeValue());
					XMLAbstractClasses.put(
							nNode.getFirstChild().getNodeValue(), "Abstract");

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
