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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.universaal.uaalpax.model.BundleEntry;

public class ProjectLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider {
	private static Image[] icons;
	
	final private CheckboxTableViewer parentViewer;
	
	static {
		icons = new Image[4];
		
		ImageData ideaImage = new ImageData(ProjectLabelProvider.class.getResourceAsStream("/images/yy.jpg"));
		icons[3] = new Image(Display.getCurrent(), ideaImage);
		ImageData ideaImage2 = new ImageData(ProjectLabelProvider.class.getResourceAsStream("/images/yn.jpg"));
		icons[1] = new Image(Display.getCurrent(), ideaImage2);
		ImageData ideaImage3 = new ImageData(ProjectLabelProvider.class.getResourceAsStream("/images/ny.jpg"));
		icons[2] = new Image(Display.getCurrent(), ideaImage3);
		ImageData ideaImage4 = new ImageData(ProjectLabelProvider.class.getResourceAsStream("/images/nn.jpg"));
		icons[0] = new Image(Display.getCurrent(), ideaImage4);
	}
	
	public ProjectLabelProvider(CheckboxTableViewer parentViewer) {
		if (parentViewer == null)
			throw new NullPointerException("parentViewer must not be null");
		
		this.parentViewer = parentViewer;
	}
	
	/**
	 * Expects every element to be a {@link BundleEntry}.
	 * 
	 * @see ITableLabelProvider#getColumnText(Object, int)
	 */
	public String getColumnText(final Object element, final int columnIndex) {
		if (!(element instanceof BundleEntry)) {
			throw new IllegalArgumentException("Elements must be instances of " + BundleEntry.class.getName());
		}
		final BundleEntry provisionURL = (BundleEntry) element;
		switch (columnIndex) {
		case 0:
			return provisionURL.getProjectName();
		case 1:
			return provisionURL.getLevel() < 0 ? "default" : String.valueOf(provisionURL.getLevel());
		default:
			return null;
		}
	}
	
	/**
	 * Images are not used.
	 * 
	 * @see ITableLabelProvider#getColumnImage(Object, int)
	 */
	public Image getColumnImage(final Object element, final int columnIndex) {
		if (columnIndex != 0)
			return null;
		
		if (!(element instanceof BundleEntry)) {
			throw new IllegalArgumentException("Elements must be instances of " + BundleEntry.class.getName());
		}
		
		final BundleEntry provisionURL = (BundleEntry) element;
		
		int i = 0;
		if (provisionURL.isStart())
			i |= (1 << 0);
		if (provisionURL.isUpdate())
			i |= (1 << 1);
		
		return icons[i];
	}
	
	public Color getForeground(Object element) {
		if (parentViewer.getGrayed(element))
			return new Color(Display.getCurrent(), 127, 127, 127);
		else
			return null;
	}
	
	public Color getBackground(Object element) {
		return null; // always use default
	}
	
	public Font getFont(Object element) {
		if (parentViewer.getGrayed(element))
			return JFaceResources.getFontRegistry().getItalic(JFaceResources.TEXT_FONT);
		else
			return null;
	}
}
