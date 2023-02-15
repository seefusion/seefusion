package com.seefusion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dumping ground for various static utility methods
 * 
 */
public class Util {

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(Util.class.getName());
	
	/** Creates a new instance of Util */
	public Util() {
	}

	public static byte[] unHex(String hex) {
		int numBytes = hex.length() / 2;
		byte[] bytes = new byte[hex.length() / 2];
		String thisHex;
		for (int i = 0; i < numBytes; ++i) {
			thisHex = hex.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(thisHex, 16);
		}
		return bytes;
	}

	public static String toHex(byte[] bytes) {
		String thisByte;
		StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < bytes.length; ++i) {
			thisByte = Integer.toHexString(0xFF & bytes[i]);
			if (thisByte.length() == 2) {
				hexString.append(thisByte);
			}
			else {
				hexString.append("0").append(thisByte);
			}
		}
		return hexString.toString();
	}

	
	/**
	 * Returns a millisecond date as 0d:00:00:00.00
	 * @param ms date in System.getDate() milliseconds
	 * @return formatted string "0d:00:00:00.00"
	 */
	public static String msFormat(long ms) {
		final long msSecond = 1000;
		final long msMinute = msSecond * 60;
		final long msHour = msMinute * 60;
		final long msDay = msHour * 24;
		long days = ms / msDay;
		ms -= days * msDay;
		long hours = ms / msHour;
		ms -= hours * msHour;
		long minutes = ms / msMinute;
		ms -= minutes * msMinute;
		long seconds = ms / msSecond;
		ms -= seconds * msSecond;
		StringBuilder ret = new StringBuilder();
		if (days != 0) {
			ret.append(days).append("d");
			ret.append(hours).append(":");
		}
		else if (hours != 0) {
			ret.append(hours).append(":");
		}
		ret.append(dd(minutes)).append(":");
		ret.append(dd(seconds)).append(".");
		ret.append(ms / 100);
		return ret.toString();
	}

	static String dd(long val) {
		if (val >= 10)
			return Long.toString(val);
		else
			return "0" + Long.toString(val);
	}

	static String ddd(long val) {
		if (val >= 100)
			return Long.toString(val);
		else if (val >= 10)
			return "0" + Long.toString(val);
		else
			return "00" + Long.toString(val);
	}

	public static String xmlStringFormat(String str) {
		if (str == null) {
			return "";
		}
		if (str.length() == 0) {
			return str;
		}
		StringBuilder ret = new StringBuilder();
		char ch;
		int i = 0;
		ch = str.charAt(i);
		// trim leading whitespace
		while (i < str.length() && (ch == '\r' || ch == '\n' || ch == '\t' || ch == ' ')) {
			ch = str.charAt(++i);
		}
		char c;
		for(; i < str.length(); ++i) {
			c = str.charAt(i);
			switch(c) {
			case 0:
				ret.append("\\0");
				break;
			case '&': 
				ret.append("&amp;");
				break;
			case '>':
				ret.append("&gt;");
				break;
			case '<':
				ret.append("&lt;");
				break;
			case '"':
				ret.append("&quot;");
				break;
			default:
				ret.append(c);
			}
		}
		return ret.toString();
//		return str.substring(i).replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
	}

	public static boolean parseYesNoParam(String yesNoString) throws java.lang.NumberFormatException {
		return parseYesNoParam(yesNoString, null);
	}
	public static boolean parseYesNoParam(String yesNoString, Boolean dfault) throws java.lang.NumberFormatException {
		if(yesNoString==null) { 
			return dfault==null ? false : dfault;
		}
		yesNoString = yesNoString.toLowerCase().trim();
		if (yesNoString.equals("yes")
				|| yesNoString.equals("true")
				|| yesNoString.equals("1")
				|| yesNoString.equals("on")) {
			return true;
		}
		if (yesNoString.equals("no")
				|| yesNoString.equals("false")
				|| yesNoString.equals("0")
				|| yesNoString.equals("off")) {
			return false;
		}
		if(dfault != null) {
			return dfault;
		}
		throw new java.lang.NumberFormatException("Unable to parse \"" + yesNoString + "\" to boolean.");
	}

	static long showHideCtr = 0;

	static String showHide(String str) {
		return showHide(str, 200);
	}

	static String showHide(String str, int len) {

		// not worried about thread safety on Util.showHideCtr, as we only
		// really care that each thread gets locally unique numbers
		String showHideCtr = Long.toString(Util.showHideCtr++);
		if (str.length() <= len) {
			return str;
		}
		else {
			StringBuilder buf = new StringBuilder();
			buf.append("<span id='fnShowHide");
			buf.append(showHideCtr);
			buf.append("a'>");
			buf.append("<a href='#' onClick=\"document.getElementById('fnShowHide");
			buf.append(showHideCtr);
			buf.append("a').style.display='none';document.getElementById('fnShowHide");
			buf.append(showHideCtr);
			buf.append("b').style.display=''; return false;\">[+]</a>");
			buf.append(str.substring(0, len));
			buf.append("...</span>");
			buf.append("<span id='fnShowHide");
			buf.append(showHideCtr);
			buf.append("b' style='display:none'>");
			buf.append("<a href='#' onClick=\"document.getElementById('fnShowHide");
			buf.append(showHideCtr);
			buf.append("a').style.display='';document.getElementById('fnShowHide");
			buf.append(showHideCtr);
			buf.append("b').style.display='none'; return false;\">[-]</a><br>");
			buf.append(crToBr(str));
			buf.append("</span>");
			return buf.toString();
		}
	}

	static String crToBr(String str) {
		return str.replaceAll("\n", "<BR>\n");
	}

	static void setFrameworkActionParam(String s) {
		if (s == null || s.length() == 0) {
			frameworkActionParam = null;
			frameworkActionPattern = null;
		}
		else {
			frameworkActionParam = s;
			frameworkActionPattern = Pattern.compile("(^|&)" + s + "=[^&]+", Pattern.CASE_INSENSITIVE);
		}
	}

	static String frameworkActionParam;

	static String getFrameworkActionParam() {
		return frameworkActionParam;
	}

	static boolean isFrameworkActionParamSet() {
		return frameworkActionPattern != null;
	}

	static Pattern frameworkActionPattern = null;

	static String findFrameworkAction(String str) {
		if (str == null || frameworkActionPattern == null) {
			return null;
		}
		Matcher m = frameworkActionPattern.matcher(str);
		if (m.find()) {
			String ret = m.group();
			if (ret.startsWith("&")) {
				return ret.substring(1);
			}
			else {
				return ret;
			}
		}
		else {
			return null;
		}
	}

	static String jsStringFormat(String s) {
		return s.replaceAll("'", "\\'").replaceAll("\"", "\\\"").replaceAll("&", "&amp;").replaceAll("<", "&lt;");
	}

	static String recordStack(List<String> debugStackTargets) {
		if (debugStackTargets == null || debugStackTargets.isEmpty()) {
			return "";
		}
		Exception e = new Exception();
		StringWriter str = new StringWriter();
		e.printStackTrace(new java.io.PrintWriter(str));
		StringTokenizer st = new StringTokenizer(str.getBuffer().toString(), "\r\n");
		boolean us = false;
		boolean foundUs = false;
		String curLine = null;
		Iterator<String> iter;
		while (st.hasMoreTokens() && (!foundUs || us)) {
			curLine = st.nextToken();
			if (curLine.indexOf("com.seefusion") != -1) {
				foundUs = true;
				us = true;
			}
			else {
				us = false;
			}
		}
		StringBuilder sb = new StringBuilder();
		while (curLine != null) {
			iter = debugStackTargets.iterator();
			while (iter.hasNext()) {
				if (curLine.toLowerCase().indexOf(iter.next()) != -1) {
					sb.append(curLine).append("\r\n");
					break;
				}
			}
			if (st.hasMoreTokens()) {
				curLine = st.nextToken();
			}
			else {
				curLine = null;
			}
		}
		return sb.toString();
	}
	
	static final Pattern selectRegex = Pattern.compile("^[\\s]*select[\\s]", Pattern.CASE_INSENSITIVE);

	static boolean isSelectStatement(String sql) {
		return selectRegex.matcher(sql).find();
	}

	static int objToInt(Object o) {
		if (o == null) {
			return 0;
		}
		else if (o instanceof Long) {
			return ((Long) o).intValue();
		}
		else if (o instanceof Integer) {
			return ((Integer) o).intValue();
		}
		else {
			try {
				return Integer.parseInt(o.toString());
			}
			catch (NumberFormatException e) {
				return 0;
			}
		}
	}

	static long objToLong(Object o) {
		if (o == null) {
			return 0;
		}
		else if (o instanceof Long) {
			return ((Long) o).longValue();
		}
		else if (o instanceof Integer) {
			return ((Integer) o).longValue();
		}
		else if (o instanceof Timestamp) {
			return ((Timestamp) o).getTime();
		}
		else {
			try {
				return Long.parseLong(o.toString());
			}
			catch (NumberFormatException e) {
				return 0;
			}
		}
	}

	static Timestamp objToTimestamp(Object o) {
		if (o == null) {
			return null;
		}
		else if (o instanceof Long) {
			return new Timestamp(((Long) o).longValue());
		}
		else if (o instanceof Timestamp) {
			return (Timestamp) o;
		}
		else if (o instanceof Date) {
			return new Timestamp(((Date) o).getTime());
		}
		else {
			try {
				return Timestamp.valueOf(o.toString());
			}
			catch (IllegalArgumentException e) {
				return null;
			}
		}
	}
	
	
	// takes something like '/json/tacos', and returns '/tacos' if it starts with '/json', otherwise returns ''
	static String getJsonMethod(String pageName) {
		if(!pageName.startsWith("/json")){
			return "";
		}
		
		String methodName = pageName.substring(5, pageName.length());
		
		return methodName;
	}

	static String readLineNull(BufferedReader in) {
		StringBuilder buf = new StringBuilder(120);
		int c;
		try {
			while ((c = in.read()) != -1) {
				// System.out.println(c);
				if (c == 0) {
					break;
				}
				else if (c == 13 || c == 10) {
					in.mark(2);
					c = in.read();
					if (c == 13 || c == 10 || c == 0) {
						// ignore
					}
					else {
						in.reset();
					}
					break;
				}
				else {
					buf.append((char) c);
				}
			}
			if (buf.length() == 0) {
				return null;
			}
		}
		catch (IOException e) {
			return null;
		}
		return buf.toString();
	}

	static String readUntilNull(BufferedReader in) {
		StringBuilder buf = new StringBuilder(300);
		int c;
		try {
			while ((c = in.read()) != -1) {
				if (c == 0) {
					break;
				}
				else {
					buf.append((char) c);
				}
			}
			if (c == -1) {
				return null;
			}
		}
		catch (IOException e) {
			return null;
		}
		return buf.toString();
	}

	/*
	static Properties parseSimpleXML(String xml) {
		Properties ret = new Properties();
		int beginTagStartPos, beginTagEndPos, endTagStartPos, endTagEndPos;
		String thisTagName;
		beginTagStartPos = xml.indexOf('<');
		beginTagEndPos = xml.indexOf('>', beginTagStartPos);
		if (beginTagStartPos == -1 || beginTagEndPos == -1) {
			return ret;
		}
		thisTagName = xml.substring(beginTagStartPos + 1, beginTagEndPos).toLowerCase();
		ret.setProperty("_root", thisTagName);
		// System.out.println("1. "+thisTagName);
		endTagStartPos = xml.indexOf("</" + thisTagName + ">");
		if (endTagStartPos == -1) {
			return ret;
		}
		xml = xml.substring(beginTagEndPos + 1, endTagStartPos);
		// System.out.println(xml);
		beginTagStartPos = xml.indexOf('<');
		while (beginTagStartPos < xml.length() && beginTagStartPos != -1) {
			beginTagEndPos = xml.indexOf('>', Math.max(0, beginTagStartPos));
			if (beginTagStartPos == -1 || beginTagEndPos == -1) {
				return ret;
			}
			if (xml.charAt(beginTagEndPos - 1) == '/') {
				thisTagName = xml.substring(beginTagStartPos + 1, beginTagEndPos - 1).trim().toLowerCase();
				// System.out.println("2. " + thisTagName);
				ret.setProperty(thisTagName, "");
				endTagEndPos = beginTagEndPos;
			}
			else {
				thisTagName = xml.substring(beginTagStartPos + 1, beginTagEndPos).trim().toLowerCase();
				// System.out.println("2. " + thisTagName);
				endTagStartPos = xml.indexOf('<', beginTagEndPos);
				endTagEndPos = xml.indexOf('>', Math.max(0, endTagStartPos));
				if (endTagStartPos == -1 || endTagEndPos == -1) {
					return ret;
				}
				ret.setProperty(thisTagName, xml.substring(beginTagEndPos + 1, endTagStartPos));
			}
			// System.out.println("->" + thisTagName + " = " +
			// ret.get(thisTagName));
			beginTagStartPos = xml.indexOf('<', endTagEndPos);
		}
		return ret;
	}
	*/

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		}
		catch (InterruptedException e) {
			// ignore, but clear Interrupted flag
			Thread.interrupted();
		}

	}

	public static String readFile(String fn) throws IOException {
		return readFile(new File(fn));
	}

	public static String readFile(File f) throws IOException {
		return readAll(new FileReader(f));
	}
	
	public static String readAll(Reader in) throws IOException {
		StringWriter out = new StringWriter();
		char[] buf = new char[2048];
		int len;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
		return out.toString();
		
	}

	public static void writeFile(String fn, String content) throws IOException {
		writeFile(new File(fn), content);
	}

	public static void writeFile(File f, String content) throws IOException {
		if(f != null) {
			FileWriter out = new FileWriter(f);
			out.write(content);
			out.close();
		}
	}
	
	static String shortString(String s, int maxLength) {
		if(s==null)
			return null;
		if(s.length() > maxLength) {
			return s.substring(0, maxLength);
		}
		else {
			return s;
		}
	}
	
	static SimpleDateFormat flexDF = new SimpleDateFormat("d HH:mm:ss 'GMT'Z yyyy");
	@SuppressWarnings("deprecation")
    static String flexDateFormat(long dateMs) {
		// hardcode day of week, since some localizations break it
		StringBuilder ret = new StringBuilder();
		Date date = new Date(dateMs);
		switch(date.getDay()) {
		case 0: ret.append("Sun "); break; 
		case 1: ret.append("Mon "); break; 
		case 2: ret.append("Tue "); break; 
		case 3: ret.append("Wed "); break; 
		case 4: ret.append("Thu "); break; 
		case 5: ret.append("Fri "); break; 
		case 6: ret.append("Sat "); break; 
		}
		switch(date.getMonth()) {
		case 0: ret.append("Jan "); break;
		case 1: ret.append("Feb "); break;
		case 2: ret.append("Mar "); break;
		case 3: ret.append("Apr "); break;
		case 4: ret.append("May "); break;
		case 5: ret.append("Jun "); break;
		case 6: ret.append("Jul "); break;
		case 7: ret.append("Aug "); break;
		case 8: ret.append("Sep "); break;
		case 9: ret.append("Oct "); break;
		case 10: ret.append("Nov "); break;
		case 11: ret.append("Dec "); break;
		}
		ret.append(flexDF.format(date));
		return ret.toString();
	}
	
	// found this simple guy on stackoverflow 
	// http://stackoverflow.com/questions/309424/in-java-how-do-a-read-convert-an-inputstream-in-to-a-string
	public static String convertStreamToString(InputStream is) {
		Scanner s = new Scanner(is);
		String value = s.useDelimiter("\\A").next();
		s.close();
		return value;
	}
	
	public static String readFileAsString(String filePath) {
		try {
		    byte[] buffer = new byte[(int) new File(filePath).length()];
		    FileInputStream f = new FileInputStream(filePath);
		    f.read(buffer);
		    String value = new String(buffer);
			try {
				f.close();
			} catch(IOException e){
				// don't care
			}
		    return value;
		} catch(IOException e){
			return "Util.readFileAsString: Unable to read file: " + filePath + " - " + e.getStackTrace();
		}
	}
	
	// parses string into a map, split at the first instance of token splitAt
	// if split token is not found, value will be ""
	// MAP KEYS WILL BE CONVERTED TO LOWERCASE.
	static void parseKeyValue(String s, char splitAt, Properties p) {
		int pos = s.indexOf(splitAt);
		if(pos==-1) {
			p.setProperty(s.toLowerCase(), "");
		} else if (pos==s.length()) {
			p.setProperty(s.substring(0, s.length()-1).toLowerCase(), "");
		} else {
			p.setProperty(s.substring(0, pos).toLowerCase(), s.substring(pos+1, s.length()).trim() );
		}
	}

	public static long parseLong(String sval, long dfault) {
		if (sval == null || sval.length() == 0) {
			// fast fail
			return dfault;
		}
		try {
			return Long.parseLong(sval);
		}
		catch (NumberFormatException e) {
			return dfault;
		}
	}

	public static int parseInt(String sval, int dfault) {
		if (sval == null || sval.length() == 0) {
			// fast fail
			return dfault;
		}
		try {
			return Integer.parseInt(sval);
		}
		catch (NumberFormatException e) {
			return dfault;
		}
	}
	
	public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
	    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
	    String[] pairs = query.split("&");
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    }
	    return query_pairs;
	}

	public static boolean isEmptyString(String name) {
		for(int i=0; i<name.length(); ++i) {
			if(!Character.isWhitespace(name.charAt(i))) {
				return false;
			}
		}
		return true;
	}

    private static ThreadLocal<SimpleDateFormat> df = new ThreadLocal<SimpleDateFormat>() {
    	@Override
		protected SimpleDateFormat initialValue() {
    		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	};
    };

    static synchronized String now() {
    	return dateFormat(System.currentTimeMillis());
	}
	
    static synchronized String dateFormat(long ms) {
		return df.get().format(new Date(ms));
	}

	public static String[] splitCommaDelimString(String s) {
		if(s==null) {
			return null;
		}
		return s.split("[,\\s]+");
	}

	public static String getEnv(String name, String dfault) {
		String ret = System.getProperty(name);
		if(ret == null) {
			ret = System.getenv(name);
		}
		if(ret == null) {
			ret = dfault;
		}
		return ret;
	}

}
