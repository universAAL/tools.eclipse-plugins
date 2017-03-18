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
package org.universaal.tools.packaging.tool.validators;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class AlphabeticV implements VerifyListener {

	public void verifyText(VerifyEvent e) {

		e.doit = e.character == '\b' || e.text == "" || e.text.matches("[A-Za-z0-9.,:\b -_]{1,}");
		/*
		 e.doit = true;
				(Character.isLetter(e.character) || 
						Character.isDigit(e.character) || 
						e.character == '-' || 
						e.character == '\b' || 
						e.character == '.' || 
						e.character == ' ' || 
						e.character == ',' ||
						e.character == ':') && 
						(e.character != '<' && 
						e.character != '>');*/
	}
}