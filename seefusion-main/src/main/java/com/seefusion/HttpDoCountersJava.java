/**
 * 
 */
package com.seefusion;


/**
 * @author Daryl
 *
 */
class HttpDoCountersJava extends HttpRequestHandler {

	public String doGet(HttpTalker talker) {
		

		StringBuilder result = new StringBuilder(1000);
		result.append("<center><object \r\n" + 
				"  classid=\"clsid:8AD9C840-044E-11D1-B3E9-00805F499D93\" width=\"90%\" height=\"90%\" \r\n" +
				"  codebase=\"http://java.sun.com/products/plugin/autodl/jinstall-1_4_2-windows-i586.cab#Version=1,4,2,0\">" +
				"  <PARAM name=\"code\" value=\"com.seefusion.CountersScroller\">" +
				"  <PARAM NAME=\"archive\" VALUE=\"seefusionbrowser.jar\">" + 
				"    <comment>\r\n" + 
				"      <embed height=\"90%\" width=\"90%\" code=\"com.seefusion.CountersScroller\"\r\n" + 
				"        type=\"application/x-java-applet;version=1.4.2\"" +
				"		 archive=\"seefusionbrowser.jar\">\r\n" + 
				"        <noembed>\r\n" + 
				"          No Java Support.\r\n" + 
				"        </noembed>\r\n" + 
				"      </embed>\r\n" + 
				"    </comment>\r\n" + 
				"  </object></center>");
		return result.toString();
		/*
		talker.doMessage("<applet code=\"com.seefusion.DebugScroller.class\" \r\n" + 
				"	archive=\"seefusionbrowser.jar\" \r\n" + 
				"	width=\"100%\" height=\"100%\">\r\n" + 
				"\r\n" + 
				"Your browser is completely ignoring the &lt;APPLET&gt; tag!\r\n" + 
				"\r\n" + 
				"</applet>");
		*/
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}
}
