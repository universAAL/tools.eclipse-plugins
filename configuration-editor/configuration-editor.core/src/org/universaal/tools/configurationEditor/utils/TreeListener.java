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

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.universaal.tools.configurationEditor.editors.ConfigurationEditor;

public class TreeListener implements Listener {

	private ConfigurationEditor ce;
	
	public TreeListener(ConfigurationEditor ce) {
		this.ce = ce;
	}

	@Override
	public void handleEvent(Event event) {
		Tree t = (Tree) event.widget;
		ce.showSelectedElement(WidgetMapping.getElement(t.getSelection()[0]));

	}

}
