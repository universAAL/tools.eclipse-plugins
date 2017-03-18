/**
    OWL2XMI,
    Copyright (C) 2009 Isaac Lera, Mehdi Khouja and Swap's Members
    http://swap.uib.es    

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package org.universaal.tools.owl2uml.core;

import java.util.Iterator;

/**
 * @author isaac
 * 
 */
public class ObjectPropertyRepresentation {

	private String uri;

	private boolean isInferred;

	private MyHashMap domains = new MyHashMap();
	private MyHashMap ranges = new MyHashMap();

	private String cardinalitySource[] = { "0..*", "0..*" };
	private String cardinalityTarget[] = { "0..*", "0..*" };

	public ObjectPropertyRepresentation(String name) {
		uri = name;
		isInferred = true; // this param always will change whether the domain
							// doesn't contain the property
	}

	public ObjectPropertyRepresentation(String name, boolean inferred) {
		uri = name;
		isInferred = inferred; // this param always will change whether the
								// domain doesn't contain the property
	}

	public String getLocalName() {
		if (isInferred)
			return "(" + uri.substring(uri.indexOf('#') + 1) + ")";
		else
			return uri.substring(uri.indexOf('#') + 1);
	}

	public void putDomain(String name) {
		domains.put(uri, name);
	}

	public void putRange(String name) {
		ranges.put(uri, name);
	}

	public Iterator<String> getDomains() {
		if (domains.get(uri) != null)
			return domains.get(uri).iterator();
		return null; // Warning bad definitions in ontology
	}

	public Iterator<String> getRanges() {
		if (ranges.get(uri) != null)
			return ranges.get(uri).iterator();
		return null; // Warning bad definitions in ontology
	}

	/**
	 * @return the cardinalitySource
	 */
	public String[] getCardinalitySource() {
		return cardinalitySource;
	}

	/**
	 * @param cardinalitySource
	 *            the cardinalitySource to set
	 */
	public void setCardinalitySource(String min, String max) {
		this.cardinalitySource[0] = min;
		this.cardinalitySource[1] = max;
	}

	/**
	 * @return the cardinalityTarget
	 */
	public String[] getCardinalityTarget() {
		return cardinalityTarget;
	}

	/**
	 * @param cardinalityTarget
	 *            the cardinalityTarget to set
	 */
	public void setCardinalityTarget(String min, String max) {
		this.cardinalityTarget[0] = min;
		this.cardinalityTarget[1] = max;
	}

	/**
	 * @return the isInferred
	 */
	public boolean isInferred() {
		return isInferred;
	}

	/**
	 * @param isInferred
	 *            the isInferred to set
	 */
	public void setInferred(boolean isInferred) {
		this.isInferred = isInferred;
	}

}
