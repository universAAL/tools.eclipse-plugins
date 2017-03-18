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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author isaac
 * 
 */
public class MyHashMap extends HashMap<String, List<String>> {

	public MyHashMap() {
		super();
	}

	public MyHashMap(int size) {
		super(size);
	}

	public List<String> get(String key) {
		return super.get(key);
	}

	public List<String> put(String key, String value) {
		List<String> list = this.get(key);
		if (list == null) {
			List<String> valList = new ArrayList<String>(1);
			valList.add(value);

			super.put(key, valList);
		} else {
			list.add(value);
			super.remove(key);
			super.put(key, list);
		}
		return null;
	}

	public List<String> putRange(String key, String value,
			ArrayList<String> type) {
		int sz = type.size();
		if (sz == 1) {

			List<String> list = this.get(key);
			if (list == null) {
				List<String> valList = new ArrayList<String>(1);
				valList.add(value + '#' + type.get(0));

				super.put(key, valList);
			} else {
				list.add(value + '#' + type.get(0));
				super.remove(key);
				super.put(key, list);
			}
		} else {// enumerated datatype property
			List<String> list = this.get(key);
			String serializedEnumarations = "";
			for (int j = 0; j < sz; j++) {
				serializedEnumarations = serializedEnumarations + type.get(j)
						+ "#";// serializing enumeration
			}
			if (list == null) {
				List<String> valList = new ArrayList<String>(1);
				valList.add(value + '#' + serializedEnumarations);

				super.put(key, valList);
			} else {
				list.add(value + '#' + serializedEnumarations);
				super.remove(key);
				super.put(key, list);
			}

		}
		return null;
	}
}
