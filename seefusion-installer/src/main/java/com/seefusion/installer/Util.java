/**
 * 
 */
package com.seefusion.installer;

import java.io.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * @author Daryl
 * 
 */
class Util {

	static String readFile(String fn) throws IOException {
		return readFile(new File(fn));
	}

	static String readFile(File f) throws IOException {
		StringWriter out = new StringWriter((int) f.length());
		Reader in = new FileReader(f);
		char[] buf = new char[2048];
		int len;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
		in.close();
		return out.toString();
	}

	static void writeFile(String fn, String content) throws IOException {
		writeFile(new File(fn), content);
	}

	static String writeFile(File f, String content) throws IOException {
		StringReader in = new StringReader(content);
		Writer out = new FileWriter(f);
		char[] buf = new char[2048];
		int len;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
		out.close();
		return out.toString();
	}

	static void copyFile(File src, File dest) throws InstallationException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			byte[] buf = new byte[2048];
			int len;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		}
		catch (IOException e) {
			throw new InstallationException("Unable to copy " + src.getName() + " from " + src.getAbsolutePath() + " to " + dest.getAbsolutePath() + ": " + e.toString());
		}		
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					// ignore
				}
			}
			if (out != null) {
				try {
					out.close();
				}
				catch (IOException e) {
					// ignore
				}
			}
		}
	}
	
	static void blindCopy(String fn, File srcDir, File destDir)  throws InstallationException {
		if(!destDir.isDirectory()) {
			throw new InstallationException("Destination directory " + destDir.getAbsolutePath() + " not found.");
		}
		File srcFile = new File(srcDir, fn);
		if(!srcFile.exists()) {
			throw new InstallationException("Source file " + srcFile.getAbsolutePath() + " not found.");
		}
		File destFile = new File(destDir, fn);
		copyFile(srcFile, destFile);
	}
	
	static File getThisJarFile() {
		return new File(Util.class.getProtectionDomain().getCodeSource().getLocation().getFile());
	}
	
	static boolean isSeeJava() {
		return "SeeJava" == "SeeFusion";
	}

	public static void blindDelete(String jarDirectory, String fn) {
		File f = new File(jarDirectory, fn);
		if (f.exists()) {
			f.delete();
		}
	}
	
	public static String xmlToString(Document doc) throws IOException, TransformerException {
		StringWriter sw = new StringWriter();
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc), new StreamResult(sw));
	    return sw.toString();
	}
	
}
