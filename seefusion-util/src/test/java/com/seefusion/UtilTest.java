package com.seefusion;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Properties;

import org.junit.Test;
import static org.junit.Assert.*;

public class UtilTest {
    
    /**
     * Test of unHex method, of class com.seefusion.Util.
     */
	@Test
    public void testUnHex() {
        byte[] b = Util.unHex("FF");
        assertTrue(b.length == 1);
        assertTrue(b[0] == -1);
    }
    
    /**
     * Test of toHex method, of class com.seefusion.Util.
     */
	@Test
    public void testToHex() {
        byte[] b = new byte[1];
        b[0]=-1;
        assertEquals("ff", Util.toHex(b));
    }
    
    /**
     * Test of msFormat method, of class com.seefusion.Util.
     */
	@Test
    public void testMsFormat() {
        assertEquals("01:00.0", Util.msFormat(60000));
    }
    
    /**
     * Test of dd method, of class com.seefusion.Util.
     */
	@Test
    public void testDd() {
        assertEquals("09", Util.dd(9));
        assertEquals("10", Util.dd(10));
        assertEquals("100", Util.dd(100));
    }
    
    /**
     * Test of ddd method, of class com.seefusion.Util.
     */
	@Test
    public void testDdd() {
        assertEquals("009", Util.ddd(9));
        assertEquals("010", Util.ddd(10));
        assertEquals("100", Util.ddd(100));
    }
    
    /**
     * Test of xmlStringFormat method, of class com.seefusion.Util.
     */
	@Test
    public void testXmlStringFormat() {
        assertEquals("this is &amp; test", Util.xmlStringFormat("this is & test"));
        int i = 0;
        String asciiNull = new Character((char)i).toString();
        String s = "This is a test." + asciiNull + " foo!";
        // System.out.println(Util.xmlStringFormat(s));
        assertTrue(Util.xmlStringFormat(s).indexOf(asciiNull) == -1);
    }
    
    /**
     * Test of parseYesNoParam method, of class com.seefusion.Util.
     */
	@Test
    public void testParseYesNoParam() {
        assertTrue(Util.parseYesNoParam("Yes"));
        assertTrue(Util.parseYesNoParam("yES"));
        assertTrue(Util.parseYesNoParam("1"));
        assertTrue(Util.parseYesNoParam("true"));
        assertFalse(Util.parseYesNoParam("foo", false));
        assertTrue(Util.parseYesNoParam("foo", true));
    }
    
    /**
     * Test of showHide method, of class com.seefusion.Util.
     */
	@Test
    public void testShowHide() {
        assertEquals("this is a test", Util.showHide("this is a test"));
        assertTrue("this is a test".compareTo(Util.showHide("this is a test", 5)) != 0);
    }
    
    /**
     * Test of crToBr method, of class com.seefusion.Util.
     */
	@Test
    public void testCrToBr() {
        assertEquals("Did not convert properly", Util.crToBr("\n\n\n"), "<BR>\n<BR>\n<BR>\n");
    }
    
	@Test
    public void testFindFuseaction() {
        Util.setFrameworkActionParam("fuseActIon");
        assertNull(Util.findFrameworkAction(null));
        String[] queryString={"fuseaction=foo", "blah&fuseaction=foo", "blah&fuseaction=foo&blah"};
        for(int i=0; i < queryString.length; ++i) {
            String fuseAction = Util.findFrameworkAction(queryString[i]);
            assertNotNull("Did not find fuseaction", fuseAction);
            assertEquals("fuseaction did not match", "fuseaction=foo", fuseAction);
        }
    }

	@Test
    public void testIsSelectStatement() {
        // System.out.println(Util.selectRegex.toString());

        assertFalse(Util.isSelectStatement("UPDATE Employees SET Lastname = ? WHERE EmployeeID = ?"));
        assertFalse(Util.isSelectStatement(" UPDATE Employees SET Lastname = 'SELECT' WHERE EmployeeID = ?"));
       
        assertTrue(Util.isSelectStatement("SELECT * FROM Employees WHERE EmployeeID = ?"));        
        assertTrue(Util.isSelectStatement(" SELECT * FROM Employees WHERE EmployeeID = ?"));        
        assertTrue(Util.isSelectStatement("\t\t\t SELECT * FROM Employees WHERE EmployeeID = ?"));        
        assertTrue(Util.isSelectStatement("\r\n\r\n\t\t\t SELECT * FROM Employees WHERE EmployeeID = ?"));
    }

	@Test
    public void testReadLineNull() throws Exception {
    	String s;
    	StringReader sr;
    	BufferedReader in;
    	s = "GET /foo HTTP/1.0\r\nhost: mydomain.com\r\n";
    	sr = new StringReader(s);
    	in = new BufferedReader(sr);
    	assertEquals("GET /foo HTTP/1.0", Util.readLineNull(in));
    	assertEquals("host: mydomain.com", in.readLine());

    	s = "<policy-file-request/>\0<another-policy-file-request/>\0";
    	sr = new StringReader(s);
    	in = new BufferedReader(sr);
    	assertEquals("<policy-file-request/>", Util.readLineNull(in));
    	assertEquals("<another-policy-file-request/>", Util.readLineNull(in));
    	assertNull(Util.readLineNull(in));
    }

	@Test
    public void testReadUntilNull() throws Exception {
    	String s;
    	StringReader sr;
    	BufferedReader in;
    	s = "<policy-file-request/>\0<another-policy-file-request/>\0";
    	sr = new StringReader(s);
    	in = new BufferedReader(sr);
    	assertEquals("<policy-file-request/>", Util.readUntilNull(in));
    	assertEquals("<another-policy-file-request/>", Util.readUntilNull(in));
    	
    	s = "<policy-file-request/>\0<some-data>\r\nFoo!\r\n</some-data>\0";
    	sr = new StringReader(s);
    	in = new BufferedReader(sr);
    	assertEquals("<policy-file-request/>", Util.readUntilNull(in));
    	assertEquals("<some-data>\r\nFoo!\r\n</some-data>", Util.readUntilNull(in));
    	assertNull(Util.readUntilNull(in));
    	
    }
    
    /*
    public void testParseSimpleXml() {
    	System.out.println("testParseSimpleXml");
    	Properties foo = Util.parseSimpleXML("<foo><bar>1</bar></foo>");
    	assertEquals("1", (String)foo.get("bar"));
    	foo = Util.parseSimpleXML("<foo><bar>1</bar>");
    	assertNull(foo.get("bar"));
    	foo = Util.parseSimpleXML("");
    	assertNull(foo.get("bar"));
    	foo = Util.parseSimpleXML("<foo></foo>");
    	assertNull(foo.get("bar"));
    	foo = Util.parseSimpleXML("<foo></foo>");
    	assertNull(foo.get("bar"));
    	foo = Util.parseSimpleXML("<foo></foo>");
    	assertNull(foo.get("bar"));
    	foo = Util.parseSimpleXML("<foo>/foo>");
    	assertNull(foo.get("bar"));
    	foo = Util.parseSimpleXML("<foo><bar1><bar2>1</bar2></foo>");
    	assertNull(foo.get("bar2"));
    	foo = Util.parseSimpleXML("<foo><bar1></bar1><bar2>1</bar2></foo>");
    	assertEquals("1", foo.get("bar2"));
    	foo = Util.parseSimpleXML("<foo><bar1></bar1><bar2>1</bar2></foo>");
    	assertEquals("", foo.get("bar1"));
    	foo = Util.parseSimpleXML("<loginvo><username /><password/></loginvo>");
    	assertEquals("", foo.get("username"));
    	assertEquals("", foo.get("password"));
    	
    }
    */
    
    @Test
    public void testParseKeyValue() {
    	Properties m = new Properties();
    	Util.parseKeyValue("Foo", ':', m);
    	assertTrue(m.containsKey("foo"));
    	Util.parseKeyValue("bar:1", ':', m);
    	// System.out.println(m.toString());
    	assertTrue(m.containsKey("bar"));
    	assertEquals("1", m.get("bar"));
    	Util.parseKeyValue("baz:", ':', m);
    	assertTrue(m.containsKey("baz"));
    	assertEquals("", m.get("baz"));
    	Util.parseKeyValue(":baz:", ':', m);
    	assertTrue(m.containsKey(""));
    	assertEquals("baz:", m.get(""));
    }    

    @Test
    public void testIsEmptyString() {
    	assertTrue(Util.isEmptyString(""));
    	assertTrue(Util.isEmptyString("   "));
    	assertTrue(Util.isEmptyString("\t\t"));
    	assertFalse(Util.isEmptyString("asd"));
    	assertFalse(Util.isEmptyString("\rtest\n"));
    }

}
