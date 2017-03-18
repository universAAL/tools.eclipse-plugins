/*	
	Copyright 2007-2016 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institute for Computer Graphics Research
	
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
package org.universaal.tools.envsetup.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Carsten Stockloew
 *
 */
public class MavenAdapter {

	public void perform() {
		System.out.println("Performing Maven settings adaptation");

		String home = System.getProperty("user.home");
		File m2 = new File(home, ".m2");
		File file = new File(m2, "settings.xml");

		if (file.exists()) {
			backupFile(file, new File(m2, "settings.old"));
			adaptFile(file);
		} else {
			writeNewFile(file);
		}
	}

	private void backupFile(File src, File dst) {
		System.out.println("backup file: " + src.toString() + " to " + dst.toString());
		try {
			Files.copy(src.toPath(), dst.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void adaptFile(File file) {
		boolean changed = false;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		Document doc;
		try {
			doc = builder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// System.out.println(doc.getFirstChild().getTextContent());
		// doc.getDocumentElement().normalize();
		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

		Element root = doc.getDocumentElement();
		NodeList nodeList = root.getChildNodes();
		Element elProfiles = null;

		// go through 1st-level nodes: profiles
		for (int temp = 0; temp < nodeList.getLength(); temp++) {
			Node node = nodeList.item(temp);
			if (!(node.getNodeType() == Node.ELEMENT_NODE))
				continue;
			Element el = (Element) node;

			if ("profiles".equals(node.getNodeName())) {
				elProfiles = el;
				break;
			}
		}

		//System.out.println(elProfiles);
		if (elProfiles == null) {
			elProfiles = doc.createElement("profiles");
			root.appendChild(doc.createTextNode("\n\t"));
			root.appendChild(elProfiles);
			root.appendChild(doc.createTextNode("\n"));
			changed = true;
		}

		// go through 2st-level nodes: profile
		boolean found = false;
		List<Element> lst = getElements(elProfiles, "profile");
		for (Element el : lst) {
			if (hasUaalRepos(el)) {
				found = true;
			}
		}
		//System.out.println(" - hasRepos: " + found);

		if (!found) {
			addProfile(doc, elProfiles);
			changed = true;
		}

		// write
		if (!changed) {
			System.out.println("The maven settings.xml already contains all the universAAL repositories.");
		} else {
			System.out.println("The maven settings.xml is modified to contain the universAAL repositories.");

			// create a String containing the new file content
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = tf.newTransformer();
				transformer.transform(domSource, result);
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			// System.out.println("XML in String format is: \n" +
			// writer.toString());

			// dump string to file
			try {
				PrintWriter out = new PrintWriter(file);
				out.println(writer.toString());
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	// add a new profile with the uaal repos as child of the given 'profiles'
	// node
	private void addProfile(Document doc, Element elProfiles) {
		Element elProf = doc.createElement("profile");
		elProfiles.appendChild(doc.createTextNode("\n\t\t"));
		elProfiles.appendChild(elProf);
		elProfiles.appendChild(doc.createTextNode("\n\t"));

		Element elAct = doc.createElement("activation");
		elProf.appendChild(doc.createTextNode("\n\t\t\t"));
		elProf.appendChild(elAct);
		elProf.appendChild(doc.createTextNode("\n\t\t"));

		Element elAct2 = doc.createElement("activeByDefault");
		elAct2.setTextContent("true");
		elAct.appendChild(doc.createTextNode("\n\t\t\t\t"));
		elAct.appendChild(elAct2);
		elAct.appendChild(doc.createTextNode("\n\t\t\t"));

		Element elRepos = doc.createElement("repositories");
		elProf.appendChild(doc.createTextNode("\n\t\t\t"));
		elProf.appendChild(elRepos);
		elProf.appendChild(doc.createTextNode("\n\t\t"));

		addRepo(doc, elRepos, "uaal", "universAAL Repositories", "http://depot.universaal.org/maven-repo/releases/",
				"snapshots");
		addRepo(doc, elRepos, "uaal-snapshots", "universAAL Snapshot Repositories",
				"http://depot.universaal.org/maven-repo/snapshots/", "releases");
		addRepo(doc, elRepos, "uaal-thirdparty", "universAAL ThirdParty Repositories",
				"http://depot.universaal.org/maven-repo/thirdparty/", "snapshots");
		addRepo(doc, elRepos, "uaal-thirdparty-snapshots", "universAAL ThirdParty Repositories",
				"http://depot.universaal.org/maven-repo/thirdparty-snapshots/", "releases");
		elRepos.appendChild(doc.createTextNode("\n\t\t\t"));
	}

	private void addRepo(Document doc, Element parent, String id, String name, String url, String falsetype) {
		Element elRepo = doc.createElement("repository");
		parent.appendChild(doc.createTextNode("\n\t\t\t\t"));
		parent.appendChild(elRepo);

		add(doc, elRepo, "id", id, "\t\t\t\t");
		add(doc, elRepo, "name", name, "\t\t\t\t");
		add(doc, elRepo, "url", url, "\t\t\t\t");

		Element elType = doc.createElement(falsetype);
		elRepo.appendChild(doc.createTextNode("\n\t\t\t\t\t"));
		elRepo.appendChild(elType);
		add(doc, elType, "enabled", "false", "\t\t\t\t\t");
		elType.appendChild(doc.createTextNode("\n\t\t\t\t\t"));
		elRepo.appendChild(doc.createTextNode("\n\t\t\t\t"));
	}

	private Element add(Document doc, Element parent, String nodeName, String nodeText, String indent) {
		Element el = doc.createElement(nodeName);
		el.setTextContent(nodeText);
		parent.appendChild(doc.createTextNode("\n" + indent + "\t"));
		parent.appendChild(el);
		return el;
	}

	// determines whether a profile node has all the uaal repos and is active by
	// default
	private boolean hasUaalRepos(Element prof) {
		List<Element> lst;

		// test 1: is the profile active by default?
		boolean isActive = false;
		lst = getElements(prof, "activation");
		if (lst.size() == 1) {
			lst = getElements(lst.get(0), "activeByDefault");
			if (lst.size() == 1) {
				Element el = lst.get(0);
				String s = el.getTextContent().trim();
				if ("true".equals(s))
					isActive = true;
			}
		}
		if (isActive == false)
			return false;

		// test 2: has the profile all the uaal repos?
		lst = getElements(prof, "repositories");
		if (lst.size() == 1) {
			lst = getElements(lst.get(0), "repository");
			if (lst.size() >= 4) {
				// we simply check for the 'id' element
				List<String> names = new ArrayList<String>();
				names.add("uaal");
				names.add("uaal-snapshots");
				names.add("uaal-thirdparty");
				names.add("uaal-thirdparty-snapshots");
				for (Element el : lst) {
					List<Element> l = getElements(el, "id");
					if (l.size() == 1) {
						String s = l.get(0).getTextContent().trim();
						// s is now the id of the repo -> remove from the list
						// of names
						names.remove(s);
					}
				}
				if (names.size() == 0) {
					// we found all the members
					return true;
				}
			}
		}
		return false;
	}

	private List<Element> getElements(Node parent, String name) {
		List<Element> ret = new ArrayList<Element>();
		NodeList nodeList = parent.getChildNodes();

		for (int temp = 0; temp < nodeList.getLength(); temp++) {
			Node node = nodeList.item(temp);
			if (!(node.getNodeType() == Node.ELEMENT_NODE))
				continue;
			Element el = (Element) node;

			if (name.equals(node.getNodeName())) {
				ret.add(el);
			}
		}

		return ret;
	}

	private void writeNewFile(File file) {
		// System.out.println(" -- writing new file: " + file.toString());
		// @formatter:off
		String content = "<settings xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 \r\n" + 
				"	http://maven.apache.org/xsd/settings-1.0.0.xsd\">\r\n" + 
				"\r\n" + 
				"	<!-- mirrors>\r\n" + 
				"		<mirror>\r\n" + 
				"			<id>artifactory</id>\r\n" + 
				"			<mirrorOf>*</mirrorOf>\r\n" + 
				"			<url>file:///D:/Saied/.m2/repository</url>\r\n" + 
				"			<name>Artifactory</name>\r\n" + 
				"		</mirror>\r\n" + 
				"	</mirrors -->\r\n" + 
				"\r\n" + 
				"	<profiles>\r\n" + 
				"		<profile>\r\n" + 
				"			<activation>\r\n" + 
				"				<activeByDefault>true</activeByDefault>\r\n" + 
				"			</activation>\r\n" + 
				"			<repositories>\r\n" + 
				"				<repository>\r\n" + 
				"					<id>uaal</id>\r\n" + 
				"					<name>universAAL Repositories</name>\r\n" + 
				"					<url>http://depot.universaal.org/maven-repo/releases/</url>\r\n" + 
				"					<snapshots>\r\n" + 
				"						<enabled>false</enabled>\r\n" + 
				"					</snapshots>\r\n" + 
				"				</repository>\r\n" + 
				"				<repository>\r\n" + 
				"					<id>uaal-snapshots</id>\r\n" + 
				"					<name>universAAL Snapshot Repositories</name>\r\n" + 
				"					<url>http://depot.universaal.org/maven-repo/snapshots/</url>\r\n" + 
				"					<releases>\r\n" + 
				"						<enabled>false</enabled>\r\n" + 
				"					</releases>\r\n" + 
				"				</repository>\r\n" + 
				"				<repository>\r\n" + 
				"					<id>uaal-thirdparty</id>\r\n" + 
				"					<name>universAAL ThirdParty Repositories</name>\r\n" + 
				"					<url>http://depot.universaal.org/maven-repo/thirdparty/</url>\r\n" + 
				"				</repository>\r\n" + 
				"				<repository>\r\n" + 
				"					<id>uaal-thirdparty-snapshots</id>\r\n" + 
				"					<name>universAAL ThirdParty Repositories</name>\r\n" + 
				"					<url>http://depot.universaal.org/maven-repo/thirdparty-snapshots/</url>\r\n" + 
				"					<releases>\r\n" + 
				"						<enabled>false</enabled>\r\n" + 
				"					</releases>\r\n" + 
				"				</repository>\r\n" + 
				"			</repositories>\r\n" + 
				"		</profile>\r\n" + 
				"	</profiles>\r\n" + 
				"\r\n" + 
				"	<!-- proxies>\r\n" + 
				"		<proxy>\r\n" + 
				"			<id>myproxy</id>\r\n" + 
				"			<active>false</active>\r\n" + 
				"			<protocol>http</protocol>\r\n" + 
				"			<host>ip address</host>\r\n" + 
				"			<port>port number</port>\r\n" + 
				"		</proxy>\r\n" + 
				"	</proxies -->\r\n" + 
				"\r\n" + 
				"</settings>";
		// @formatter:on

		Writer writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			writer.write(content);
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
