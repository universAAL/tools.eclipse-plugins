/*	
	Copyright 2007-2016 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institute for Computer Graphics Research
	
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
package org.universaal.tools.envsetup.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Carsten Stockloew
 *
 */
public class EclipseAdapter {

	List<String> lines = new ArrayList<String>();
	File file;

	/**
	 * 
	 * @param jdk
	 * @return true, if eclipse.ini has been modified (which may require a
	 *         restart of eclipse)
	 */
	public boolean perform(String jdk) {
		init();
		if (file == null)
			return false;

		readFile();
		boolean changed = false;
		if (addVM(jdk))
			changed = true;
		if (adaptXXMaxPermSize())
			changed = true;
		// out();
		if (changed) {
			System.out.println("EclipseAdapter - writing file " + file.toString());
			write();
		} else {
			System.out.println("EclipseAdapter - no changes necessary");
		}
		return changed;
	}

	public static String getJDK() {
		try {
			EclipseAdapter e = new EclipseAdapter();
			// first try to the JDK from eclipse.ini
			String jdk = "";
			e.init();
			if (e.file != null) {
				e.readFile();
				jdk = e.getVM();
				if (jdk == null)
					jdk = "";
			}
			if (jdk.endsWith("bin/javaw.exe") || jdk.endsWith("bin\\javaw.exe")) {
				jdk = jdk.substring(0, jdk.length() - "bin/javaw.exe".length());
				return jdk;
			}
			// if this did not work, do a simple file search; this method is
			// windows-specific
			File base = new File("c:/Program Files/Java");
			if (base.exists()) {
				String[] lst = base.list();
				for (String s : lst) {
					if (s.startsWith("jdk")) {
						File dir = new File(base, s);
						if (isValidJDKDir(dir))
							return dir.toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isValidJDKDir(String dir) {
		return isValidJDKDir(new File(dir));
	}

	public static boolean isValidJDKDir(File dir) {
		if (dir.isDirectory()) {
			File file = new File(dir, "/bin/javaw.exe");
			if (file.exists()) {
				System.out.println("isValidJDKDir is valid: " + dir.toString());
				return true;
			}
			System.out.println("ERROR isValidJDKDir: " + file.toString() + " does not exist.");
		} else {
			System.out.println("ERROR isValidJDKDir: " + dir.toString() + " is not a valid directory.");
		}
		return false;
	}

	private void write() {
		Writer writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			for (int i = 0; i < lines.size(); i++) {
				writer.write(lines.get(i));
				writer.write("\r\n");
			}
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * initialize file - get eclipse.ini
	 */
	private void init() {
		String eclipseExe = System.getProperty("eclipse.launcher");
		// eclipseExe =
		// "D:\\IGD\\uaal\\_test\\tools_eclipse4\\eclipse\\eclipse.exe";

		File fileExe = new File(eclipseExe);
		String path = fileExe.getParent();
		file = new File(path, "eclipse.ini");
		if (!file.exists()) {
			System.out.println("EclipseAdapter - ERROR: could not find eclipse.ini at " + file.toString());
			return;
		}
	}

	private String getVM() {
		int i = getIndex("-vm");
		if (i == -1)
			return null;
		i++;
		if (i >= lines.size())
			return null;
		return lines.get(i).trim();
	}

	// private void out() {
	// for (int i = 0; i < lines.size(); i++) {
	// System.out.println(lines.get(i));
	// }
	// }

	private boolean addVM(String jdk) {
		if (jdk == null)
			return false;

		File f = new File(new File(jdk, "bin"), "javaw.exe");
		if (!f.exists()) {
			System.out.println("EclipseAdapter - ERROR: jdk not valid, not setting jdk: " + jdk);
			return false;
		}

		int i = getIndex("-vm");
		if (i == -1) {
			// the parameter is not yet set -> add before 'vmargs'
			i = getIndex("-vmargs");
			if (i == -1)
				i = lines.size() - 1;
			if (i < lines.size())
				lines.add(i, "-vm");
			else
				lines.add("-vm");
			if (i + 1 < lines.size())
				lines.add(i + 1, f.toString());
			else
				lines.add(f.toString());
			return true;
		}
		return false;
	}

	private boolean adaptXXMaxPermSize() {
		boolean changed = false;
		// this parameter appears twice, check the line afterwards to be >512m
		for (int i = 0; i < lines.size(); i++) {
			if ("--launcher.XXMaxPermSize".equals(lines.get(i).trim())) {
				i++;
				if (i >= lines.size())
					break;
				// check here
				long l = getValue(lines.get(i));
				// System.out.println(" val: " + lines.get(i) + " " + l);
				if (l < getValue("512m")) {
					System.out.println("adapting XXMaxPermSize");
					lines.set(i, "512m");
					changed = true;
				}
			}
		}
		return changed;
	}

	private long getValue(String s) {
		String number = s.substring(0, s.length() - 1);
		long l = Long.valueOf(number);
		if (s.endsWith("k") || s.endsWith("K")) {
			l = l * 1024;
		}
		if (s.endsWith("m") || s.endsWith("M")) {
			l = l * 1024 * 1024;
		}
		if (s.endsWith("g") || s.endsWith("G")) {
			l = l * 1024 * 1024 * 1024;
		}
		return l;
	}

	private int getIndex(String line) {
		for (int i = 0; i < lines.size(); i++) {
			if (line.equals(lines.get(i).trim()))
				return i;
		}
		return -1;
	}

	private void readFile() {
		if (file == null)
			return;
		// read the file contents in an arrayList, each line is an element
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("EclipseAdapter - loaded " + lines.size() + " lines from " + file.toString());
	}
}
