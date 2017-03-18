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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.universaal.uaalpax.model.BundleEntry;

public class BundleComparator extends ViewerComparator {
	private int sortDirection = SWT.NONE;
	private String sortProperty = BundleEntry.PROP_PROJECT;
	
	public void setSortDirection(int direction) {
		sortDirection = direction;
	}
	
	public void toggleSortDirection() {
		sortDirection = (sortDirection == SWT.DOWN) ? SWT.UP : SWT.DOWN;
	}
	
	public int getSortDirection() {
		return sortDirection;
	}
	
	public void setSortProperty(String prop) {
		sortProperty = prop;
	}
	
	public String getSortProperty() {
		return sortProperty;
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (!(e1 instanceof BundleEntry) || !(e2 instanceof BundleEntry))
			throw new IllegalArgumentException("can compare only BundleEntry elements, but got types " + e1.getClass() + ", "
					+ e2.getClass());
		else if (sortDirection == SWT.NONE)
			return 0;
		else {
			BundleEntry be1 = (BundleEntry) e1, be2 = (BundleEntry) e2;
			int comp = 0;
			
			if (sortProperty == BundleEntry.PROP_PROJECT)
				comp = be1.getLaunchUrl().url.compareToIgnoreCase(be2.getLaunchUrl().url);
			else if (sortProperty == BundleEntry.PROP_LEVEL) {
				if (be1.getLevel() < be2.getLevel())
					comp = -1;
				else if (be1.getLevel() == be2.getLevel())
					comp = 0;
				else
					comp = 1;
			}
			
			if (sortDirection == SWT.UP)
				comp *= -1;
			
			return comp;
		}
	}
}
