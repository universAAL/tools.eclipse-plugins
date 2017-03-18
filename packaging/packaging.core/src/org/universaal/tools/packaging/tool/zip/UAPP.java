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
package org.universaal.tools.packaging.tool.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.universaal.tools.packaging.tool.util.DefaultLogger;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class UAPP {

	public void createUAPPfile(String sourcePath, String destinationPath) {

		try{
			File directoryToZip = new File(sourcePath);

			List<File> fileList = new ArrayList<File>();
			
			DefaultLogger.getInstance().log("[Application Packager] - Getting references to all files in: " + directoryToZip.getCanonicalPath(), 1);
			getAllFiles(directoryToZip, fileList);
			DefaultLogger.getInstance().log("[Application Packager] - Creating zip file", 1);
			writeZipFile(destinationPath, directoryToZip, fileList);
			DefaultLogger.getInstance().log("[Application Packager] - Done", 1);
			rmDir(directoryToZip);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void getAllFiles(File dir, List<File> fileList) {

		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					DefaultLogger.getInstance().log("[Application Packager] - directory:" + file.getCanonicalPath(), 1);
					getAllFiles(file, fileList);
				} else {
					DefaultLogger.getInstance().log("[Application Packager] -      file:" + file.getCanonicalPath(), 1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeZipFile(String destinationPath, File directoryToZip, List<File> fileList) {

		try {
			FileOutputStream fos = new FileOutputStream(destinationPath);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addToZip(File directoryToZip, File file, ZipOutputStream zos) {

		try{
			FileInputStream fis = new FileInputStream(file);

			// we want the zipEntry's path to be a relative path that is relative
			// to the directory being zipped, so chop off the rest of the path
			String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1, file.getCanonicalPath().length());
			DefaultLogger.getInstance().log("[Application Packager] - Writing '" + zipFilePath + "' to zip file", 1);
			ZipEntry zipEntry = new ZipEntry(zipFilePath);
			zos.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}

			zos.closeEntry();
			fis.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	static private boolean rmDir(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	        	 rmDir(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }
}