/**
 * 
 */
package com.seefusion.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.SwingUtilities;

import com.seefusion.Password;

/**
 * @author Daryl
 * 
 */
public class Main implements Runnable {

	public static void printHelp() {
		System.out.println("-? -h --help: print this help text");
		System.out.println("--hash [password]: print a salted hash of a password provided via argument or stdin");
		System.out.println("--install / --uninstall / --is-installed : must be followed by the name of the servlet engine, and its path:");
		System.out.println("  cf [coldfusion 10+ instance dir, eg /opt/coldfusion/cfusion]");
		System.out.println("  tomcat [tomcat base dir, eg /var/lib/tomcat8]");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			install(args);
		}
		catch(InstallationException e) {
			System.out.println("Operation failed: " + e.getMessage());
			System.exit(1);
		}
		catch(Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InstallationException 
	 */
	public static void install(String[] args) throws InstallationException, IOException {
			if(args.length == 0) {
				System.out.println("Opening installer window.");
				SwingUtilities.invokeLater(new Main());
			}
			else if ( "--hash".equals(args[0]) ) {
				final String password;
				if (args.length > 2) {
					printHelp();
					return;
				}
				else if (args.length == 1) {
					password = new BufferedReader(new InputStreamReader(System.in)).readLine();
				}
				else {
					password = args[1];
				}
				System.out.print(new Password(password).toString());
			}
			else if(args.length == 3) {
				Installer installer;
				File dir = new File(args[2]);
				File xmlFile;
				// get the correct installer
				if(args[1].equalsIgnoreCase("tomcat")) {
					xmlFile = new File(dir, "conf/server.xml");
					if(!xmlFile.exists()) {
						xmlFile = new File(dir, "server.xml");
						if(!xmlFile.exists()) {
							throw new InstallationException("Could not locate Tomcat configuration file at: " + xmlFile.getCanonicalPath());
						}
					}
					String xml = Util.readFile(xmlFile);
					installer = new InstallTomcatValve(xml);
				}
				else if(args[1].equalsIgnoreCase("cf")) {
					xmlFile = new File(dir, "runtime/conf/server.xml");
					if(!xmlFile.exists()) {
						xmlFile = new File(dir, "server.xml");
						if(!xmlFile.exists()) {
							throw new InstallationException("Could not locate ColdFusion configuration file at: " + xmlFile.getCanonicalPath());
						}
					}
					String xml = Util.readFile(xmlFile);
					installer = new InstallTomcatValve(xml);
				}
				else {
					throw new InstallationException("Unknown installation type: " + args[1]);
				}
				
				// perform whatever operation
				if(args[0].equalsIgnoreCase("--install")) {
					System.out.println("Installing SeeFusion");
					installer.install();
					Util.writeFile(xmlFile, installer.getXml());
				}
				else if(args[0].equalsIgnoreCase("-uninstall")) {
					System.out.println("Removing SeeFusion");
					installer.install();
					Util.writeFile(xmlFile, installer.getXml());
				}
				else if(args[0].equalsIgnoreCase("--is-installed")) {
					if(installer.isInstalled()) {
						System.out.println("SeeFusion installed at " + xmlFile.getCanonicalPath());
						System.exit(0);
					}
					else {
						System.out.println("SeeFusion is NOT installed at " + xmlFile.getCanonicalPath());
						System.exit(1);
					}
				}
			}
			else {
				printHelp();
			}

	}

	@Override
	public void run() {
		new MainFrame();
	}

	
}
