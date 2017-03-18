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

package org.universaal.uaalpax.ui;/*

 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.universaal.uaalpax.model.BundleEntry;

public class ProjectCellModifier implements ICellModifier {
	
	private boolean enabled = false;
	
	private final CheckboxTableViewer viewer;
	
	public ProjectCellModifier(CheckboxTableViewer viewer) {
		if(viewer == null)
			throw new NullPointerException("viewer must not be null");
		this.viewer = viewer;
	}
	
	/**
	 * Modify listener.
	 */
	private ModifyListener m_modifyListener;
	
	/**
	 * All cells beside "url" are modifiable.
	 * 
	 * @see ICellModifier#canModify(Object, String)
	 */
	public boolean canModify(Object element, String property) {
		return enabled && !BundleEntry.PROP_PROJECT.equals(property) && !viewer.getGrayed(element);
	}
	
	/**
	 * @see ICellModifier#getValue(Object, String)
	 */
	public Object getValue(final Object element, final String property) {
		if (element instanceof BundleEntry) {
			final BundleEntry projectURL = (BundleEntry) element;
			
			if (BundleEntry.PROP_LEVEL.equals(property)) {
				if (projectURL.getLevel() < 0) {
					return "";
				}
				return String.valueOf(projectURL.getLevel());
			}
		}
		return null;
	}
	
	/**
	 * @see ICellModifier#modify(Object, String, Object)
	 */
	public void modify(final Object element, final String property, final Object value) {
		final TableItem item = (TableItem) element;
		if (item.getData() instanceof BundleEntry) {
			final BundleEntry projectURL = (BundleEntry) item.getData();
			if (BundleEntry.PROP_LEVEL.equals(property)) {
				if (value == null || ((String) value).trim().length() == 0) {
					projectURL.setLevel(-1);
				} else {
					try {
						projectURL.setLevel(new Integer((String) value));
					} catch (NumberFormatException ignore) {
						// just not set the value
					}
				}
			}
			
			if (m_modifyListener != null) {
				m_modifyListener.modified(projectURL);
			}
		}
	}
	
	/**
	 * Sets the modify listener.
	 * 
	 * @param modifyListener
	 *            a listener
	 */
	public void setModifyListener(final ModifyListener modifyListener) {
		m_modifyListener = modifyListener;
	}
	
	/**
	 * A modify listener is notified on succesfull update of a modified cell.
	 */
	public interface ModifyListener {
		/**
		 * Notification on succesfull update.
		 * 
		 * @param element
		 *            modified element.
		 */
		void modified(BundleEntry element);
		
	}
	
	public void setEnabled(boolean b) {
		enabled = b;
	}
}
