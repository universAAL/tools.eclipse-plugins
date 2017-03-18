/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut für Graphische Datenverarbeitung
	
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

package org.universaal.uaalpax.ui;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.universaal.uaalpax.model.BundleEntry;


public class ProjectContentProvider implements IStructuredContentProvider, IContentProvider {
	
	/**
	 * Returns an array of ProvisionURL's.
	 * 
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(final Object inputElement) {
		if (inputElement instanceof List)
			return ((List<?>) inputElement).toArray();
		else if (inputElement instanceof Set)
			return ((Set<?>) inputElement).toArray();
		
		throw new IllegalArgumentException("Input element must be List of " + BundleEntry.class.getName());
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
