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
package org.universaal.tools.owl2uml;

import java.io.File;
import java.io.IOException;

import org.universaal.tools.owl2uml.core.UML2Parser;
import org.universaal.tools.owl2uml.uml2.UML2Factory;

/**
 * @author joemoul, billyk
 */
public class OWL2UML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String ontologyFile = args[0];
		String umlFile = args[1];

		UML2Parser parser = new UML2Parser();

		File XMLFile = new File(
				org.universaal.tools.owl2uml.uml2.UML2Factory.XMLFilePath);
		if (XMLFile.exists()) {
			parser.loadOntology(ontologyFile, "", false);
			UML2Factory.readXML();
			parser.generateUMLContent();

			try {
				parser.write(umlFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			UML2Factory.createXML();
			System.out
					.println("The required XML file for the definition of universAAL properties has not been defined.\n"
							+ "An XML template called \""
							+ org.universaal.tools.owl2uml.uml2.UML2Factory.XMLFilePath
									.substring(org.universaal.tools.owl2uml.uml2.UML2Factory.XMLFilePath
											.lastIndexOf("/") + 1)
							+ "\" has been created under the same folder containing the OWL file.\n"
							+ "Please fill in the required properties and select the \"Transform2UML\" option again!");
		}

	}

}
