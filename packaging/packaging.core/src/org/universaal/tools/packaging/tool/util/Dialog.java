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
package org.universaal.tools.packaging.tool.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class Dialog {

	private static String lastOpenDir = null;
	
	public File open(Shell shell, String[] filterExt, boolean open, String topText){
		return this.open(shell, "", filterExt, open, topText);
	}

	
	public File open(Shell shell, String filename, String[] filterExt, boolean open, String topText){

		FileDialog fd;
		if(open)
			fd = new FileDialog(shell, SWT.OPEN);
		else
			fd = new FileDialog(shell, SWT.SAVE);
		fd.setText(topText);
		fd.setFilterPath(lastOpenDir);
		fd.setFileName(filename);
		//String[] filterExt = {"*.uapp"};
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if ( selected == null ) {
			return null;
		} else {
			File toBeReturned = new File(selected);
			lastOpenDir = onlyPath(toBeReturned.getAbsolutePath());
			return toBeReturned;
		}
	}
	
	public File[] openMulti(Shell shell, String topText){
		File[] list = null;
		JFileChooser fileChooser = new JFileChooser();
        try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        fileChooser.updateUI(); //Create UI objects
	 	fileChooser.setMultiSelectionEnabled(true);
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    fileChooser.setDialogTitle(topText);
	    fileChooser.setCurrentDirectory(new File(lastOpenDir));
	    int showOpenDialog = fileChooser.showOpenDialog(null);
        if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
        	list = fileChooser.getSelectedFiles();
        	lastOpenDir = onlyPath(fileChooser.getSelectedFile().getAbsolutePath());
        }
        return list;
	}

	private String onlyPath(String path){
		String[] segments = path.split("/");
		path = "";
		
		for(int i=0; i<segments.length-1; i++)
			path += "/" + segments[i];
		
		return path;
	}
	
}