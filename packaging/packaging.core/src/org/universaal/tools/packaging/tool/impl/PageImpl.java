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
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import org.eclipse.jface.wizard.IWizardPage;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import org.universaal.tools.packaging.tool.api.Page;
import org.universaal.tools.packaging.tool.api.WizardPageMod;
import org.universaal.tools.packaging.tool.gui.GUI;
import org.universaal.tools.packaging.tool.parts.Application;
import org.universaal.tools.packaging.tool.parts.MPA;
import org.universaal.tools.packaging.tool.preferences.EclipsePreferencesConfigurator;
import org.universaal.tools.packaging.tool.util.DefaultLogger;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public abstract class PageImpl extends WizardPageMod implements Page {

	protected Composite container;
	protected GridData gd;

	protected MPA multipartApplication;
	protected Application app;
	protected List<Control> mandatory;

	protected static int otherLicenses = 1;
	protected static int otherGeneralReqs = 1;
//	protected static List<Integer> otherPartReqs;
	protected static int otherPartReqs = 1;
	private String description = "";
	private double percentage;

	protected PageImpl(String pageName, String description){

		super(pageName);
		setTitle(pageName);		
		setDescription(description);
    	this.description = description;

//		otherPartReqs = new ArrayList<Integer>();

		mandatory = new ArrayList<Control>();
		setPageComplete(false);
	}

	public void setMPA(MPA mpa) {
		multipartApplication = mpa;
		app = multipartApplication.getAAL_UAPP();
	}

	public boolean validate(){
		
		for(int i = 0; i < mandatory.size(); i++){
			if(mandatory.get(i) instanceof Text)
				if(((Text)mandatory.get(i)).getText().trim().isEmpty())
					return false;
			if(mandatory.get(i) instanceof Combo)
				if(((Combo)mandatory.get(i)).getText().trim().isEmpty())
					return false;
		}
		return true;
	}

	public boolean isValid(Control c1, Control c2, Control c3){

		if(c1 == null && c2 == null && c3 == null)
			return false;

		if(c1 != null){
			if(c1 instanceof Text)
				if(((Text)c1).getText().isEmpty())
					return false;
			if(c1 instanceof Combo)
				if(((Combo)c1).getText().isEmpty())
					return false;
		}

		if(c2 != null){
			if(c2 instanceof Text)
				if(((Text)c2).getText().isEmpty())
					return false;
			if(c2 instanceof Combo)
				if(((Combo)c2).getText().isEmpty())
					return false;
		}

		if(c3 != null){
			if(c3 instanceof Text)
				if(((Text)c3).getText().isEmpty())
					return false;
			if(c3 instanceof Combo)
				if(((Combo)c3).getText().isEmpty())
					return false;
		}

		return true;
	}

	public boolean isValid(Control c1){
		return isValid(c1, null, null);
	}

	public boolean isValid(Control c1, Control c2){
		return isValid(c1, c2, null);
	}

	public String removeBlanks(String input){

		if(input != null && !input.isEmpty()){
			input = input.replace(" ", "%20");
			return input;
		}

		return "";
	}
	
	public void addPageCustom(IWizardPage caller, IWizardPage newPage){ 

		GUI gui = GUI.getInstance();
		gui.addPage(newPage, gui.getPageNumber(caller)+1);
	}

	public void setArtifact(IProject p){}

	public void setDialogHelpAvailable(boolean v){
		this.setDialogHelpAvailable(v);
	}
	
	public abstract class QL implements KeyListener{

		public void keyPressed(KeyEvent e) {
			setPageComplete(validate());
		}

		public abstract void keyReleased(KeyEvent e);	
	}

	public class FullListener extends QL{

		@Override
		public void keyReleased(KeyEvent e) {
			setPageComplete(validate());
		}}
	
	protected void serializeMPA(){
		if ( ! EclipsePreferencesConfigurator.local.isPersistanceEnabled() ) {
		    return;
		}
		if(GUI.getInstance().recoveryStorage != null){
			
			try {
				DefaultLogger.getInstance().log("writing recovery file");
				FileOutputStream fos = new FileOutputStream( GUI.getInstance().recoveryStorage, false );
				ObjectOutputStream oos = new ObjectOutputStream( fos );
				oos.writeObject(this.multipartApplication);
				oos.flush();
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    @Override
    public void setVisible(boolean visible){
    	super.setVisible(visible);
    	if(visible){ 
    		app.setCurrentPageTitle(getTitle());
    	}
    	serializeMPA();
    }
    
    public void setPercentage(double percentage){
    	percentage = percentage * 100.0;
    	setDescription(description + " - "+(int)percentage+"% completed");
    }
    
    public double getPercentage(){
    	return percentage;
    }
}