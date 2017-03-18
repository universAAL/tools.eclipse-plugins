/*
        Copyright 2007-2014 CNR-ISTI, http://isti.cnr.it
        Institute of Information Science and Technologies
        of the Italian National Research Council

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
package org.universaal.tools.packaging.tool.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class TextExt extends org.eclipse.swt.widgets.Text {

	protected void checkSubclass() {
	}
	
	public TextExt(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	
	public void addTooltip(String text){
		final ToolTip t = new ToolTip(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.BALLOON);
		t.setText(text);
		t.setVisible(false);
		
		this.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
				t.setVisible(false);				
			}

			public void focusGained(FocusEvent e) {
				Text actionWidget = (Text) e.widget;
				Point loc = actionWidget.getParent().toDisplay(actionWidget.getLocation());
                t.setLocation(loc.x+((int)actionWidget.getSize().x/4), loc.y+actionWidget.getSize().y-(int)actionWidget.getSize().y/4);
                t.setVisible(true);				
			}
		});
	}
	
}