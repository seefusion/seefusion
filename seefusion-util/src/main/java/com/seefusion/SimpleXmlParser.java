package com.seefusion;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleXmlParser {
	/*
		Premise: XML is a collection of zero or more "branch" tags
		    that contain one or more "leaf" tags, which may contain data.
		
		find a valid tag
		find the next end tag
		is the next tag the end tag for this tag?
		--yes: the data you want is between the tags.  (It's a "leaf" tag.)
		--no: push the current tag onto the stack and find the next tag. (It's a "branch" tag.)
		
		The stack is implemented as the array "aStructPtr".
		
		Note that if you have tags without end tags, then
		(a) this will fail, and 
		(b) that's not well-formed XML.

	*/

	/*
		// replace usual suspects
		// (If your dtd defines other entity references, add them here.)
		// (note that &amp; is done last-- see the end of the function.)


	 */
	
	static Pattern hexPattern = Pattern.compile("^[0-9a-fA-F]$");
	static Pattern decimalPattern = Pattern.compile("^[0-9]+$");
	
	static boolean isDecimal(String s) {
		if(s==null)
			return false;
		return decimalPattern.matcher(s).matches();
	}
	static boolean isDecimal(char c) {
		return "01234566789".indexOf(c) != -1;
	}
	static boolean isHexChar(char c) {
		return "01234566789ABCDEFabcdef".indexOf(c) != -1;
	}
	static int hex2int(char c) {
		int ret = "abcdef".indexOf(c);
		if(ret == -1)
			ret = "ABCDEF".indexOf(c);
		if(ret != -1) {
			return 10+ret;
		}
		ret = "0123456789".indexOf(c);
		if(ret != -1) {
			return ret;
		}
		throw new IndexOutOfBoundsException();
	}
	
	static String xmlStringDecode(String xml) {
		int semiPos=0;
		String escString="";
		String decimalString="";
		int ascii=0;
		char char1, char2;
		int chrPos = xml.indexOf("&#");
		xml = xml.replace("&lt;", "<");
		xml = xml.replace("&gt;", ">");
		xml = xml.replace("&quot;", "\"");
		xml = xml.replace("&apos;", "'");
		// replace extended chars in either "&#x80;" (hex) or "&#128;" (decimal) format
		while(chrPos != -1) {
			semiPos = xml.indexOf(';',chrPos);
			if(semiPos != -1 && (semiPos-chrPos < 6)) {
				escString = xml.substring(chrPos,semiPos+1);
				if(escString.charAt(2) == 'x' || escString.charAt(2) == 'X') {
					// &#x41;
					char1 = escString.charAt(3);
					char2 = escString.charAt(4);
					if(isHexChar(char1) && isHexChar(char2)) {
						ascii = hex2int(char1) * 16 + hex2int(char2);
						xml = xml.replace(escString, Character.toString((char)ascii));
					}
				} else {
					// &#65;
					decimalString = escString.substring(2,escString.length()-1);
					if(isDecimal(decimalString)) {
						xml = xml.replace(escString, Character.toString((char)Integer.parseInt(decimalString)));
					}
				}
			}
			chrPos = xml.indexOf("&#");
		}
		// always replace amersand last:
		xml = xml.replace("&amp;", "&");
		// note that there might be problems if someone encodes ampersand as &#038; then follows it with "amp;".
		// but if you're doing that, you get what you deserve ;-P
		return xml;
	}

	static void decodeXMLattributes(SimpleXml xml, String tagAttributesString) {
		String attributeName = "";
		int equalsPos = 0;
		char delim;
		int endPos = 0;
		// returns a struct of attributes, as you'd expect
		if(tagAttributesString.length() == 0) {
			// short circuit on empty string
			return;
		}
		else {
			equalsPos = tagAttributesString.indexOf('=');
			while(equalsPos != -1) {
				// stuff before equals is attribute name
				attributeName = tagAttributesString.substring(0, equalsPos).trim();
				tagAttributesString = tagAttributesString.substring(equalsPos+1).trim();
				// first nonspace after equals should be single or doublequote.
				delim = tagAttributesString.charAt(0);
				endPos = tagAttributesString.indexOf(delim, 1);
				if( (delim == '"' || delim == '\'') && endPos != -1) {
					// decode into attribute
					xml.setAttribute(attributeName, xmlStringDecode(tagAttributesString.substring(1,endPos)));
				}
				else {
					// strictly speaking, we're working with bad XML here.  But we'll try, anyway.
					xml.setAttribute(attributeName, xmlStringDecode(tagAttributesString.substring(1)));
				}
				tagAttributesString = tagAttributesString.substring(endPos+1);
				equalsPos = tagAttributesString.indexOf('=');
			}
		}
	}

	static Pattern whitespacePattern = Pattern.compile("[[:space:]]");
	
	public static SimpleXml parseXml(String xml) {
		// This function will "read" an xml file, and return a structure that
		//    represents the xml data structure.
		// It does not seek begin-end tag pairs, but rather assumes ["requires"]
		//    that all begin tags have matching end tags (or are self-ending.)
		// In other words, it only works well for well-formed xml.  What did you expect?

		// stack to keep track of what depth we're at in nested tags:
		LinkedList<SimpleXml> xmlTagPath = new LinkedList<SimpleXml>();
		// string positions:
		int xmlStartTagPos;
		int xmlStartTagEndPos;
		int spacePos;
		// current start tag:
		String xmlStartTag="";
		// current tag name:
		String xmlTagName="";
		String tagAttributesString;
		// pointer to "current" struture in xmlStruct
		// remember, struct assignments in CF are done by reference, not by value
		SimpleXml curXml = null;
		
		xmlStartTagPos = xml.indexOf('<');
		while(xmlStartTagPos != -1) {	
			// find end of this tag
			xmlStartTagEndPos = xml.indexOf('>',xmlStartTagPos);
			// get entire start tag
			xmlStartTag = xml.substring(xmlStartTagPos, xmlStartTagEndPos + 1);
			// ignore comments, etc; only bother with this tag if it looks like a "normal" tag (second char is alpha or "/" (end tag)):
			if("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz/".indexOf(xmlStartTag.charAt(1)) != -1) {
				// are there spaces within the start tag?
				Matcher match = whitespacePattern.matcher(xmlStartTag);
				tagAttributesString = "";
				if(match.matches()) {
					spacePos = match.start();
					xmlTagName = xmlStartTag.substring(1, spacePos-1);
					tagAttributesString = xmlStartTag.substring(spacePos+1).trim();
					
				}
				else {
					xmlTagName = xmlStartTag.substring(1, xmlStartTag.length()-1);
					if( xmlTagName.charAt(0) == '/' ) {
						// self-ending tag.  we'll handle that later.
						// for now, remove the slash
						xmlTagName=xmlTagName.substring(0,xmlTagName.length()-1);
					}
				}
				if(xmlTagName.charAt(0) == '/') {  // non-leaf end tag.  Pop a level off the stack.
					// this will always be true, for well-formed XML.  Silently ignore, otherwise.
					if(!xmlTagPath.isEmpty()) {
						SimpleXml tag = xmlTagPath.removeLast();
						if(xmlTagPath.isEmpty()) {
							return tag;
						} else {
							curXml = xmlTagPath.getLast();
						}
					}
				}
				else { // start tag
					// Is this a self-ending tag?
					if(xml.charAt(xmlStartTagEndPos-1) == '/') { // is self-closing
						// create tag
						SimpleXml tag = new SimpleXml(xmlTagName);
						decodeXMLattributes(tag, tagAttributesString);
						if(curXml == null) {
							curXml = tag;
						}
						else {
							curXml.add(tag);
						}
					}
					else { // has end tag ...somewhere.
						SimpleXml tag = new SimpleXml(xmlTagName);
						decodeXMLattributes(tag, tagAttributesString);
						int nextTagPos = xml.indexOf('<', xmlStartTagPos+xmlStartTag.length());
						int endStartTagPos = xmlStartTagPos+xmlStartTag.length();
						if(xml.substring(endStartTagPos, endStartTagPos+9).equalsIgnoreCase("<![CDATA[")) {
							// special handling for CDATA
							int endCdataPos = xml.indexOf("]]>", xmlStartTagPos+xmlStartTag.length() + 9);
							if(endCdataPos == -1) {
								tag.setCDATA(xml.substring(xmlStartTagEndPos+10).trim());
								xmlStartTagEndPos = -1;
							}
							else {
								tag.setCDATA(xml.substring(xmlStartTagEndPos+10, endCdataPos).trim());
								nextTagPos = xml.indexOf('<', endCdataPos);
								xmlStartTagEndPos = nextTagPos + xmlTagName.length() + 1;
							}
							if(curXml != null) {
								curXml.add(tag);
							}
							else {
								curXml = tag;
							}
						}
						// if the next tag is the this tag's end tag, then place the
						//    data in between the tags into the structure.
						else if( xml.substring(nextTagPos, nextTagPos+xmlTagName.length()+2).equalsIgnoreCase("</" + xmlTagName) ) { // we are at leaf
							tag.setSimpleValue(xmlStringDecode(xml.substring(xmlStartTagEndPos+1, nextTagPos).trim()));
							if(curXml != null) {
								curXml.add(tag);
							}
							else {
								curXml = tag;
							}
							// push xmlEndTagPos to end of end tag
							xmlStartTagEndPos = nextTagPos + xmlTagName.length() + 2;
						} else { // not at leaf, add to aStructPtr depth
							xmlTagPath.add(tag);
							if(curXml != null) {
								curXml.add(tag);
							}
							curXml = tag;
						}
					}
				}
			}
			// find next tag
			xmlStartTagPos = xml.indexOf('<', xmlStartTagEndPos);
		}
		if(xmlTagPath.isEmpty())
			return null;
		return xmlTagPath.getFirst();
	}
}
