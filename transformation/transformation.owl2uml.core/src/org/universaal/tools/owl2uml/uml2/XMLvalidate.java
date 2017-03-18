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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.core.runtime.FileLocator;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLvalidate {

	public static String newline = System.getProperty("line.separator");

	public static String validateXML(Document doc) {

		MyErrorHandler errorHandler = new MyErrorHandler();
		URL url = null;
		try {
			url = new URL(
					"platform:/plugin/org.universaal.tools.owl2uml/profiles/OWL2UML.xsd");

		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		String fileUrlStr = null;

		try {
			URL fileUrl = FileLocator.toFileURL(url);
			fileUrlStr = fileUrl.toString().substring(5);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			Schema schema = factory.newSchema(new File(fileUrlStr));

			Validator validator = schema.newValidator();
			validator.setErrorHandler(errorHandler);
			validator.validate(new DOMSource(doc));

		} catch (SAXParseException e) {

			return e.getMessage();

		} catch (Exception e) {

			return e.getMessage();

		}

		String getResult = errorHandler.getResult();
		if (getResult.isEmpty())
			return "OK";
		return getResult;

	}

	static class MyErrorHandler implements ErrorHandler {

		StringBuilder result = new StringBuilder();

		public void fatalError(SAXParseException e) throws SAXException {
			result.append("FatalError - Line " + e.getLineNumber() + ", "
					+ e.getColumnNumber() + ": " + e.toString() + newline);
		}

		public void error(SAXParseException e) throws SAXException {
			result.append("Error - Line " + e.getLineNumber() + ", "
					+ e.getColumnNumber() + ": " + e.toString() + newline);
		}

		public void warning(SAXParseException e) throws SAXException {
			result.append("Warning - Line " + e.getLineNumber() + ", "
					+ e.getColumnNumber() + ": " + e.toString() + newline);
		}

		public String getResult() {
			return result.toString();
		}
	}
}