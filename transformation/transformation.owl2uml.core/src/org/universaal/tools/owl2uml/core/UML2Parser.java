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
package org.universaal.tools.owl2uml.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.universaal.tools.owl2uml.uml2.UML2Factory;

import com.hp.hpl.jena.ontology.DataRange;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * @author joemoul, billyk
 */
public class UML2Parser {

	private OntModel model;
	private boolean optionReasoner;
	private UML2Factory uml2Factory;
	private MyHashMap dataProperties;
	private MyHashMap parents;
	private Set<OntClass> unsatConcepts;
	private Set<String> visitedObjectProperty;

	// private String NS;

	public UML2Parser() {
		System.out.println("Type... UML2");

	}

	public void loadOntology(String ontology, String uri, boolean reasoner) {

		// NS = uri + "/";
		optionReasoner = reasoner;

		if (!optionReasoner) {
			model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

			System.out.println("Reading..." + ontology);

			String inputFileName = ontology;

			// use the FileManager to find the input file
			InputStream inp = FileManager.get().open(inputFileName);
			if (inp == null) {
				throw new IllegalArgumentException("File: " + inputFileName
						+ " not found");
			}

			// read the RDF/XML file
			model.read(inp, "RDF/XML-ABBREV");
			// System.out.println( "The Ontology " + inputFileName + " imports "
			// + model.listImportedOntologyURIs() );

			System.out.println("Read DONE " + inputFileName);

			System.out.println("Read base name................."
					+ model.getNsPrefixMap()
							.get("")
							.toString()
							.substring(
									0,
									model.getNsPrefixMap().get("").toString()
											.length() - 1));

			uml2Factory = new UML2Factory(
					model.getNsPrefixMap()
							.get("")
							.toString()
							.substring(
									0,
									model.getNsPrefixMap().get("").toString()
											.length() - 1));

			dataProperties = new MyHashMap(10);
			parents = new MyHashMap(30);
			visitedObjectProperty = new HashSet(10);

		} else {

			model = ModelFactory
					.createOntologyModel(PelletReasonerFactory.THE_SPEC);
			// model =
			// ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);

			System.out.println("\t Reading...");
			model.read(ontology);
			System.out.println("\t [ok]");

			// load the model to the reasoner
			System.out.println("\t Preparing...");
			model.prepare();
			System.out.println("\t [ok]");

			System.out.println("Classifying...");
			((PelletInfGraph) model.getGraph()).getKB().classify();
			System.out.println("done");

		}

	}

	public void generateUMLContent() {

		System.out.println(" With reasoner: " + optionReasoner);

		if (!optionReasoner) {
			setAllDateProperty();
			addOWLConcepts();
			addObjectProperties();
		} else {

			Set<String> concepts = parents.keySet();
			Iterator<String> it = concepts.iterator();
			while (it.hasNext()) {
				String concept = it.next();

				OntClass cls = model.getOntClass(concept);

				System.out.println("\n\t\tPreparing concept: "
						+ cls.getLocalName());

				Set<String> objectsProperties = new HashSet<String>(5);
				Set<ObjectPropertyRepresentation> directPropertiesObj = new HashSet<ObjectPropertyRepresentation>(
						5);
				Set<String> directPropertiesData = new HashSet<String>(5);
				ExtendedIterator propertiesDirect = cls
						.listDeclaredProperties(true);

				while (propertiesDirect.hasNext()) {
					OntProperty pro = (OntProperty) propertiesDirect.next();
					if (pro.isDatatypeProperty()) {
						directPropertiesData.add(pro.getLocalName());
					} else if (pro.isObjectProperty()) {
						objectsProperties.add(pro.getURI());
						ObjectPropertyRepresentation objPr = createDirectObjectPropertyRepresentation(pro);
						if (objPr != null)
							directPropertiesObj.add(objPr);
					}
				}

				ExtendedIterator propertiesIt = cls
						.listDeclaredProperties(false); // check false
				while (propertiesIt.hasNext()) {
					OntProperty pro = (OntProperty) propertiesIt.next();

					if (pro.isDatatypeProperty()) {
						if (!directPropertiesData.contains(pro.getLocalName()))
							directPropertiesData.add("(" + pro.getLocalName()
									+ ")");
					} else if (pro.isObjectProperty()) {
						if (!objectsProperties.contains(pro.getURI())) {
							ObjectPropertyRepresentation objPr = createIndirectObjectPropertyRepresentation(
									pro, cls.getURI());
							if (objPr != null)
								directPropertiesObj.add(objPr);
						}
					}
				}

				uml2Factory.createClass(getName(concept), parents.get(concept)
						.iterator(), directPropertiesData.iterator());
				uml2Factory.createObjectProperties(directPropertiesObj
						.iterator());
			}

		}
	}

	private ObjectPropertyRepresentation createDirectObjectPropertyRepresentation(
			OntProperty pr) {
		/*
		 * There are only one property with the same name
		 */

		ObjectPropertyRepresentation obj = new ObjectPropertyRepresentation(
				pr.getURI(), false);

		System.out.println("\t\t\t---Object Property --------------------- ");
		System.out.println("\t\t\tName:" + obj.getLocalName());
		String minCardSource = "0";
		String maxCardSource = "-1"; // equals n
		// 0..n

		String minCardTarget = "0";
		String maxCardTarget = "-1";

		if (pr.isFunctionalProperty()) {
			minCardTarget = "1";
			maxCardTarget = "1";

		}
		if (pr.isInverseFunctionalProperty()) {
			minCardSource = "1";
			maxCardTarget = "1";
		}
		obj.setCardinalitySource(minCardSource, maxCardSource);
		obj.setCardinalityTarget(minCardTarget, maxCardTarget);

		System.out.println("\t\t\tDomain: ");

		ExtendedIterator domains = pr.listDomain();
		while (domains.hasNext()) { // only one time
			OntResource res = (OntResource) domains.next();
			if (res.isClass()) {
				System.out.println("\t\t\t\t" + res.getLocalName());
				obj.putDomain(res.getLocalName());

			}
		}

		System.out.println("\t\t\tRange: ");
		ExtendedIterator ranges = pr.listRange();
		while (ranges.hasNext()) {
			OntResource res = (OntResource) ranges.next();
			if (res.isClass()) {
				OntClass tmp = res.asClass();
				if (tmp.isUnionClass()) {
					System.out.println("\t\t\t  asUnionClass:");

					UnionClass uclass = tmp.asUnionClass();
					ExtendedIterator uIt = uclass.listOperands();
					while (uIt.hasNext()) {
						OntResource cl = (OntResource) uIt.next();
						System.out.println("\t\t\t\t" + cl.getLocalName());
						obj.putRange(cl.getLocalName());
					}

				} else {
					System.out.println("\t\t\t\t" + res.getLocalName());
					obj.putRange(res.getLocalName());
				}
			}
		}
		System.out.println("\t\t\t Is inferred Property: " + obj.isInferred());
		System.out.println("\t\t\t--------------------------- ");
		return obj;
	}

	private ObjectPropertyRepresentation createIndirectObjectPropertyRepresentation(
			OntProperty pr, String fromURI) {

		if (!visitedObjectProperty.contains(pr.toString())) {

			ObjectPropertyRepresentation obj = new ObjectPropertyRepresentation(
					pr.getURI());

			System.out
					.println("\t\t\t---Object Property --------------------- ");
			System.out.println("\t\t\tName:*()" + obj.getLocalName());
			String minCardSource = "0";
			String maxCardSource = "-1";
			// 0..n

			String minCardTarget = "0";
			String maxCardTarget = "-1";

			if (pr.isFunctionalProperty()) {
				minCardTarget = "1";
				maxCardTarget = "1";

			}
			if (pr.isInverseFunctionalProperty()) {
				minCardSource = "1";
				maxCardTarget = "1";
			}
			obj.setCardinalitySource(minCardSource, maxCardSource);
			obj.setCardinalityTarget(minCardTarget, maxCardTarget);

			System.out.println("\t\t\tDomain: ");

			ExtendedIterator domains = pr.listDomain();
			while (domains.hasNext()) {
				OntResource res = (OntResource) domains.next();
				if (res.isClass()) {
					OntClass tmp = res.asClass();
					if (tmp.isUnionClass()) {
						System.out.println("\t\t\t\t  asUnionClass:");

						UnionClass uclass = tmp.asUnionClass();
						ExtendedIterator uIt = uclass.listOperands();
						while (uIt.hasNext()) {
							OntResource cl = (OntResource) uIt.next();
							System.out.println("\t\t\t\t" + cl.getLocalName());
							obj.putDomain(cl.getLocalName());
							if (obj.isInferred())
								if (!cl.getURI().equals(fromURI)) {
									obj.setInferred(true);
								} else {
									obj.setInferred(false);

								}
						}

						if (obj.isInferred())
							return null;
						else {
							visitedObjectProperty.add(pr.toString());
						}

					} else {
						System.out.println("\t\t\t\t" + res.getLocalName());

						if (!res.getURI().equals(fromURI)) {
							obj.setInferred(true);
							obj.putDomain(getName(fromURI));
						} else {
							obj.setInferred(false);
							visitedObjectProperty.add(pr.toString());
							obj.putDomain(res.getLocalName());
						}
					}
				}
			}

			System.out.println("\t\t\tRange: ");
			ExtendedIterator ranges = pr.listRange();
			while (ranges.hasNext()) {
				OntResource res = (OntResource) ranges.next();
				if (res.isClass()) {
					OntClass tmp = res.asClass();
					if (tmp.isUnionClass()) {
						System.out.println("\t\t\t  asUnionClass:");

						UnionClass uclass = tmp.asUnionClass();
						ExtendedIterator uIt = uclass.listOperands();
						while (uIt.hasNext()) {
							OntResource cl = (OntResource) uIt.next();
							System.out.println("\t\t\t\t" + cl.getLocalName());
							obj.putRange(cl.getLocalName());
						}

					} else {
						System.out.println("\t\t\t\t" + res.getLocalName());
						obj.putRange(res.getLocalName());
					}
				}
			}
			System.out.println("\t\t\t Is inferred Property: "
					+ obj.isInferred());
			System.out.println("\t\t\t--------------------------- ");
			return obj;
		}
		return null;
	}

	private OntClass CreateTree(OntClass cls) {
		if (unsatConcepts.contains(cls)) {
			return null;
		}

		System.out.println("\t\tAnalysing : " + cls.getLocalName());

		Set<OntClass> processedSubs = new HashSet<OntClass>();
		// get only direct subclasses
		ExtendedIterator subs = cls.listSubClasses(true);
		while (subs.hasNext()) {

			OntClass sub = (OntClass) subs.next();

			if (sub.isAnon())
				continue;
			if (processedSubs.contains(sub))
				continue;

			OntClass subTree = CreateTree(sub);
			// if set contains owl:Nothing tree will be null
			if (subTree != null) {
				parents.put(subTree.toString(), getName(cls.toString()));
				processedSubs.add(subTree);
			}
		}

		return cls;
	}

	public void write(String file) throws IOException {

		uml2Factory.write(file);
	}

	private void addOWLConcepts() {
		Set<String> processedSubs = new HashSet<String>();

		// Direct class from Thing and equivalent class not appear due to
		// inference troubles.
		String queryString = "PREFIX  rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n"
				// + "PREFIX : \n"
				// +"<"
				// + NS
				// + "> \n"
				+ "SELECT ?subclasses ?superclase \n"
				+ "WHERE { ?subclasses rdfs:subClassOf ?superclase } \n";
		System.out.println(queryString);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		// All Class and direct class from thing
		String queryString2 = "PREFIX  rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX  owl:  <http://www.w3.org/2002/07/owl#> \n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
				// + "PREFIX : <"
				// + NS
				// + "> \n"
				+ "SELECT ?classes  \n"
				+ "WHERE { ?classes rdf:type owl:Class } \n";
		System.out.println(queryString2);
		Query query2 = QueryFactory.create(queryString2);
		QueryExecution qexec2 = QueryExecutionFactory.create(query2, model);

		try {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				String x = soln.get("subclasses").toString();
				String r = soln.get("superclase").toString();

				int xIndex = x.indexOf('#');
				if (xIndex < 1) {
					xIndex = x.lastIndexOf('/');
				}

				int rIndex = x.indexOf('#');
				if (rIndex < 1) {
					rIndex = x.lastIndexOf('/');
				}
				if (xIndex > 1 && rIndex > 1 && !x.equals(r)) {
					String nameChild = x.substring(xIndex + 1);
					String nameParent = x.substring(rIndex + 1);

					if (!processedSubs.contains(nameChild)) {

						// Multiple parents
						System.out.println("\tAnalysing: " + nameChild);
						OntClass cl = model.getOntClass(x);
						ExtendedIterator it = cl.listSuperClasses(true);
						List<String> list = new ArrayList<String>(1);
						while (it.hasNext()) {
							Object val = it.next();
							if (val instanceof OntClass) {
								OntClass cl2 = (OntClass) val;
								String name = cl2.getLocalName();
								if (name != null) {
									System.out.println("\t\t adding Parent: "
											+ name);
									list.add(name);
								}

							}
						}
						// System.out.println("\tPARENT " + nameParent);
						// System.out.println("\t adding CLASS: " +
						// nameChild+ ".hasParent." + nameParent);

						uml2Factory.createClass(nameChild, list.iterator(),
								(dataProperties.get(nameChild) == null ? null
										: dataProperties.get(nameChild)
												.iterator()));

						processedSubs.add(nameChild);
					}
				}
			}

			ResultSet results2 = qexec2.execSelect();
			for (; results2.hasNext();) {
				QuerySolution soln2 = results2.nextSolution();
				String x2 = soln2.get("classes").toString();

				int x2Index = x2.indexOf('#');
				if (x2Index < 1) {
					x2Index = x2.lastIndexOf('/');
				}
				if (x2Index > 1) {
					String name = x2.substring(x2Index + 1);
					if (!processedSubs.contains(name)) {
						OntClass cls = model.getOntClass(x2);
						OntClass s = cls.getEquivalentClass();
						if (s == null) {

							System.out.println("\t adding CLASS: " + name
									+ ".hasParent.Thing");
							if (dataProperties.get(name) != null) {
								uml2Factory.createClass(name, "" + "Thing",
										dataProperties.get(name).iterator());
							} else {
								uml2Factory.createClass(name, "" + "Thing",
										null);
							}
						} else {
							System.out.println("[Warning: Equivalent Class]"
									+ s.toString());
						}
					}
				}
			}
			// System.out.println("\t adding CLASS: Thing");
			// UML2Factory.createClass("Thing", null);

		} finally {
			qexec2.close();
		}
	}

	private String getName(String r) {

		int rIndex = r.indexOf('#');
		if (rIndex < 1) {
			rIndex = r.lastIndexOf('/');
		}

		return r.substring(rIndex + 1);
	}

	private void addObjectProperties() {

		Resource res3 = null;
		Resource res2 = null;

		System.out
				.println("\t--- Adding Object Properties --------------------- ");

		Set<ObjectPropertyRepresentation> directPropertiesObj = new HashSet<ObjectPropertyRepresentation>(
				5);

		// containing super-range...
		String queryString = "PREFIX  rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX  owl:  <http://www.w3.org/2002/07/owl#> \n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
				// + "PREFIX : <#> \n"
				+ "SELECT ?proper ?domain ?range ?super_property ?sp_domain ?sp_range \n"
				+ "WHERE { ?proper rdf:type owl:ObjectProperty  ."
				+ "OPTIONAL{?proper rdfs:subPropertyOf ?super_property. ?super_property rdf:type owl:ObjectProperty. ?super_property rdfs:range ?sp_range}."
				+ "OPTIONAL{?proper rdfs:subPropertyOf ?super_property. ?super_property rdf:type owl:ObjectProperty. ?super_property rdfs:domain ?sp_domain}."
				+ "OPTIONAL {?proper rdfs:domain ?domain}. OPTIONAL{?proper rdfs:range ?range}} \n";

		System.out.println(queryString);

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		try {
			ResultSet results = qexec.execSelect();

			// ResultSetFormatter.out(System.out, results, query);

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				String p = soln.get("proper").toString();

				ObjectPropertyRepresentation obj = new ObjectPropertyRepresentation(
						p, false);
				System.out.println("\tName:" + obj.getLocalName());

				OntProperty pr = model.getOntProperty(p);

				String minCardSource = "0";
				String maxCardSource = "-1";
				// 0..n

				String minCardTarget = "0";
				String maxCardTarget = "-1";

				if (pr.isFunctionalProperty()) {
					minCardTarget = "1";
					maxCardTarget = "1";

				}
				if (pr.isInverseFunctionalProperty()) {
					minCardSource = "1";
					maxCardTarget = "1";
				}
				obj.setCardinalitySource(minCardSource, maxCardSource);
				obj.setCardinalityTarget(minCardTarget, maxCardTarget);

				System.out.println("\tDomain: ");

				if ((Resource) soln.get("domain") != null) {
					res3 = (Resource) soln.get("domain");
				} else {
					System.out
							.println("\t\t Property domain is null checking super-property");
					if ((Resource) soln.get("sp_domain") != null) {
						res3 = (Resource) soln.get("sp_domain");
					} else {
						System.out
								.println("\t\t Super-property domain is also null");
						res3 = null;
					}
				}
				OntResource res = null;
				if (res3 != null) {
					res = model.getOntResource(res3);
					if (res.isClass()) {
						OntClass tmp = res.asClass();
						if (tmp.isUnionClass()) {
							System.out.println("\t  asUnionClass:");

							UnionClass uclass = tmp.asUnionClass();
							ExtendedIterator uIt = uclass.listOperands();
							while (uIt.hasNext()) {
								OntResource cl = (OntResource) uIt.next();
								System.out.println("\t\t" + cl.getLocalName());
								obj.putDomain(cl.getLocalName());

							}

						} else {
							System.out.println("\t\t" + res.getLocalName());
							obj.putDomain(tmp.getLocalName());
						}
					}
				}
				System.out.println("\tRange: ");
				if ((Resource) soln.get("range") != null) {
					res2 = (Resource) soln.get("range");
				} else {
					System.out
							.println("\t\t [WARNING]: Property range is null for "
									+ p + " . Checking super-property...");
					if ((Resource) soln.get("sp_range") != null) {
						res2 = (Resource) soln.get("sp_range");

					} else {
						System.out
								.println("\t\t [WARNING]: Super-property range is also null.");
						res2 = null;

					}
				}

				if (res2 != null) {

					res = model.getOntResource(res2);

					if (res.isClass()) {
						OntClass tmp = res.asClass();
						if (tmp.isUnionClass()) {
							System.out.println("\t  asUnionClass:");

							UnionClass uclass = tmp.asUnionClass();
							ExtendedIterator uIt = uclass.listOperands();
							while (uIt.hasNext()) {
								OntResource cl = (OntResource) uIt.next();
								System.out.println("\t\t" + cl.getLocalName());
								obj.putRange(cl.getLocalName());

							}

						}

						else {
							System.out.println("\t\t" + res.getLocalName());
							obj.putRange(tmp.getLocalName());
						}
					}
				}
				if ((res2 == null)) {
					System.out
							.println("\t\t [WARNING]: Property "
									+ p
									+ " and its super-property has null range and/or null domain.");
				} else {
					if (res3 == null) {
						obj.putDomain("Thing");
					}
					directPropertiesObj.add(obj);
				}

			}

			uml2Factory.createObjectProperties(directPropertiesObj.iterator());

		} finally {
			qexec.close();
		}
	}

	private void setAllDateProperty() {
		String queryString = "PREFIX  rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX  owl:  <http://www.w3.org/2002/07/owl#> \n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
				+ "SELECT ?proper ?range ?domain \n"
				+ "WHERE { ?proper rdf:type owl:DatatypeProperty ."
				+ " ?proper rdfs:domain ?domain . "
				+ " OPTIONAL{?proper rdfs:range ?range . ?proper rdf:type owl:FunctionalProperty .}} \n";
		System.out.println(queryString);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		ResultSet results = qexec.execSelect();
		// ResultSetFormatter.out(System.out, results, query);

		System.out.println("\tAnalysing datatypes properties: ");

		while (results.hasNext()) {

			QuerySolution soln = results.nextSolution();
			String p = soln.get("proper").toString();
			String d = soln.get("domain").toString(); // Get a result

			int pIndex = p.indexOf('#');
			if (pIndex < 1) {
				pIndex = p.lastIndexOf('/');
			}

			int dIndex = d.indexOf('#');
			if (dIndex < 1) {
				dIndex = d.lastIndexOf('/');
			}
			String nameProp = p.substring(pIndex + 1);
			String nameDomain = d.substring(dIndex + 1);
			RDFNode rg = soln.get("range");
			String r = "";
			ArrayList<String> rangeProp = new ArrayList<String>();
			if (rg != null) {
				r = rg.toString();

				if (rg.isAnon()) {

					DatatypeProperty hg = model.getDatatypeProperty(p);
					OntResource hgRange = hg.getRange();
					if (hgRange == null) {
						System.out
								.println("[WARNING]: Unidentified datatype property range for "
										+ p + " .");
						rangeProp.add("any");
					}
					try {
						DataRange dr = hgRange.asDataRange();
						Iterator i = dr.listOneOf();
						while (i.hasNext()) {
							Literal l = (Literal) i.next();
							System.out.println("\t\t\t" + l.getLexicalForm());
							rangeProp.add(l.getLexicalForm());
						}
					} catch (Exception e) {
						System.out
								.println("[WARNING]: Unidentified datatype property range for "
										+ p + " .");
						rangeProp.add("any");
					}
				} else {

					int rIndex = r.indexOf('#');
					if (rIndex < 1) {
						rIndex = r.lastIndexOf('/');
					}

					rangeProp.add(r.substring(rIndex + 1));
				}
			} else {
				rangeProp.add("any");
			}
			System.out.println("\t\t " + nameDomain + " has " + nameProp);
			dataProperties.putRange(nameDomain, nameProp, rangeProp);

		}

	}

	private Set collect(Iterator i) {
		Set set = new HashSet();
		while (i.hasNext()) {
			OntResource res = (OntResource) i.next();
			if (res.isAnon())
				continue;
			set.add(res);
		}
		return set;
	}
}
