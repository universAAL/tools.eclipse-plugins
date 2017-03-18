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
package org.universaal.tools.packaging.tool.impl;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.universaal.tools.packaging.tool.gui.GUI;
import org.universaal.tools.packaging.tool.parts.MPA;
import org.universaal.tools.packaging.tool.preferences.EclipsePreferencesConfigurator;

/**
 * This a decorator class for {@link PageImpl} that introduces that stores 
 * automatically the progress of the packaging for a later recovery<br>
 * <b>TODO</b>: This class should also handle the loading of the data
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class PersistencePageDecorator extends PageImpl {

    protected PageImpl localPage;
    
    public PersistencePageDecorator(PageImpl page) {	
	super(page.getName(), null);
	localPage = page;
	setDescription(page.getDescription());
	setTitle(page.getTitle());
	setPageComplete(false);
    }

    public boolean nextPressed() {
    	final boolean flag;
		try{
		    flag = localPage.nextPressed();
		    if ( flag ) serializeMPA();
		}catch(Exception ex) {
		    ex.printStackTrace();
		    return false;
		}
		return flag;
    }

    public void setPageComplete(boolean complete) {
    	
	if ( localPage == null ) return;
	
	localPage.setPageComplete(complete);
    }

    public void setDescription(String description) {
	if ( localPage == null ) return;
	localPage.setDescription(description);
    }

    public void setTitle(String title) {
	if ( localPage == null ) return;
	localPage.setTitle(title);
    }
    
    /*
    
    private void serializeMPA() throws Exception {
	if ( ! Configurator.local.isPersistanceEnabled() ) {
	    return;
	}
	System.out.println("writing recovery file");
	FileOutputStream fos = new FileOutputStream( GUI.getInstance().recoveryStorage, false );
	ObjectOutputStream oos = new ObjectOutputStream( fos );
	oos.writeObject(this.multipartApplication);
	oos.flush();
	oos.close();	
    }
    
    */
    
    
    /*
     * START OF DELEGATOR METHODS
     */

    public void createControl(Composite parent) {
	localPage.createControl(parent);
    }

    public boolean backPressed() {
	return localPage.backPressed();
    }

    public void addPageCustom(IWizardPage caller, IWizardPage newPage) {
	localPage.addPageCustom(caller, newPage);
    }

    public boolean canFlipToNextPage() {
	return localPage.canFlipToNextPage();
    }

    public Image getImage() {
	return localPage.getImage();
    }

    public void dispose() {
	localPage.dispose();
    }

    public boolean equals(Object obj) {
	return localPage.equals(obj);
    }

    public String getName() {
	return localPage.getName();
    }

    public IWizardPage getNextPage() {
	return localPage.getNextPage();
    }

    public IWizardPage getPreviousPage() {
	return localPage.getPreviousPage();
    }

    public Shell getShell() {
	return localPage.getShell();
    }

    public Control getControl() {
	return localPage.getControl();
    }

    public IWizard getWizard() {
	return localPage.getWizard();
    }

    public String getDescription() {
	return localPage.getDescription();
    }

    public String getErrorMessage() {
	return localPage.getErrorMessage();
    }

    public boolean isPageComplete() {
	return localPage.isPageComplete();
    }

    public String getMessage() {
	return localPage.getMessage();
    }

    public int getMessageType() {
	return localPage.getMessageType();
    }

    public String getTitle() {
	return localPage.getTitle();
    }

    public int hashCode() {
	return localPage.hashCode();
    }

    public void setMPA(MPA mpa) {
	localPage.setMPA(mpa);
    }

    public boolean validate() {
	return localPage.validate();
    }

    public boolean isValid(Control c1, Control c2, Control c3) {
	return localPage.isValid(c1, c2, c3);
    }

    public boolean isValid(Control c1) {
	return localPage.isValid(c1);
    }

    public boolean isValid(Control c1, Control c2) {
	return localPage.isValid(c1, c2);
    }

    public String removeBlanks(String input) {
	return localPage.removeBlanks(input);
    }

    public void setArtifact(IProject p) {
	localPage.setArtifact(p);
    }

    public void setDialogHelpAvailable(boolean v) {
	localPage.setDialogHelpAvailable(v);
    }

    public void setErrorMessage(String newMessage) {
	localPage.setErrorMessage(newMessage);
    }

    public void setImageDescriptor(ImageDescriptor image) {
	localPage.setImageDescriptor(image);
    }

    public void setMessage(String newMessage, int newType) {
	localPage.setMessage(newMessage, newType);
    }

    public void setPreviousPage(IWizardPage page) {
	localPage.setPreviousPage(page);
    }

    public void setWizard(IWizard newWizard) {
	localPage.setWizard(newWizard);
    }

    public String toString() {
	return localPage.toString();
    }

    public void performHelp() {
	localPage.performHelp();
    }

    public void setMessage(String newMessage) {
	localPage.setMessage(newMessage);
    }

    public void setVisible(boolean visible) {
	localPage.setVisible(visible);
    }
    
    /*
     * END OF DELEGATORS METHOD
     */
}
