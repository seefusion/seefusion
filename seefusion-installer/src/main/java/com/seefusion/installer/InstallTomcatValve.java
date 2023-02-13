/**
 * 
 */
package com.seefusion.installer;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Daryl
 * 
 */
class InstallTomcatValve implements Installer {

	public static final String CLASS_NAME = "com.seefusion.SeeFusionValve";
	
	Document doc;

	String error = null;

	InstallTomcatValve(String xml) throws InstallationException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			doc = docBuilder.parse(in);
		} catch (Exception e) {
			throw new InstallationException(e);
		}
	}

	public String getXml() {
		try {
			return Util.xmlToString(doc);
		} catch (Exception e) {
			return null;
		}
	}

	public String toString() {
		return getXml();
	}

	public boolean isInstalled() throws InstallationException {
			Node valve = getValve();
			return valve != null;
	}
	
	public void uninstall() throws InstallationException {
		if(!isInstalled()) return;
		Node valve = getValve();
		valve.getParentNode().removeChild(valve);
	}
	
	Node getValve() {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			return (Node) xpath.evaluate("Server/Service/Engine/Valve[@className='" + CLASS_NAME + "']", doc, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// should never happen
			throw new RuntimeException(e);
		}		
	}
	
	Node getEngine() {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			return (Node) xpath.evaluate("Server/Service/Engine[@name='Catalina']", doc, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// should never happen
			throw new RuntimeException(e);
		}
		
	}

	public void install() throws InstallationException {
		if(isInstalled()) return;
		try {
			Node engine = getEngine();
			Element valve = doc.createElement("Valve");
			valve.setAttribute("className", CLASS_NAME);
			engine.insertBefore(valve, engine.getFirstChild());
		}
		catch(Exception e) {
			throw new InstallationException(e);
		}
	}

}