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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.universaal.tools.configurationEditor.editors.ConfigurationEditor;

public class WidgetListener implements ModifyListener {
	
	private ConfigurationEditor ce;
	
	public WidgetListener(ConfigurationEditor mpe) {
		this.ce = mpe;
	}

	@Override
	public void modifyText(ModifyEvent e) {
				
		if(e.widget instanceof Text) {
			Text wi = (Text) e.widget;
			
			if(WidgetMapping.get(wi) == WidgetMapping.ELEMENT){
				
				WidgetMapping.getElement(wi).setText(wi.getText());
				//System.out.println(WidgetMapping.getElement(wi).getValue());
				
			} else if (WidgetMapping.get(wi) == WidgetMapping.ATTRIBUTE) {
				
				WidgetMapping.getAttribute(wi).setValue(wi.getText());
				//System.out.println(WidgetMapping.getAttribute(wi).getValue());
			}
		
			
			
		}

		ce.editorChanged();
	}

}
