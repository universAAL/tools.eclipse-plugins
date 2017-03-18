/*
	Copyright 2011 FZI, http://www.fzi.de

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

package org.universaal.tools.configurationEditor.utils;

import java.util.HashMap;

import org.eclipse.swt.widgets.Widget;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class WidgetMapping {
	
	public static final int ELEMENT = 1;
	public static final int ATTRIBUTE = 0;
	
	
	private static HashMap<Widget, Element> elementMap = new HashMap<Widget, Element>();
	private static HashMap<Widget, Attribute> attributeMap = new HashMap<Widget, Attribute>();
	
	public static void put(Widget wi, Object ob){
		if(ob instanceof Attribute){
			attributeMap.put(wi, (Attribute) ob);
		} else if(ob instanceof Element){
			elementMap.put(wi, (Element) ob);
		}
	}
	
	
	public static int get(Widget wi){
		if(elementMap.containsKey(wi)){
			return 1;
		} else if(attributeMap.containsKey(wi)){
			return 0;
		} else {
			return -1;
		}
		
	}
	
	public static Attribute getAttribute(Widget wi){
		return attributeMap.get(wi);
	}
	
	public static Element getElement(Widget wi){
		return elementMap.get(wi);
	}
	
	public static Element removeElement(Widget wi){
		return elementMap.remove(wi);
	}
	
}
