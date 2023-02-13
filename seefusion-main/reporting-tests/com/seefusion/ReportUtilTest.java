/*
 * UtilTest.java
 * JUnit based test
 *
 * Created on September 1, 2005, 6:40 PM
 */

package com.seefusion;

import java.io.File;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Daryl
 */
public class ReportUtilTest extends TestCase {
    
	Document doc;
	
	@Before
    public void setUp() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        File f = new File("tests/com/seefusion/reporting/UtilTestParams.xml");
        log(f.getAbsolutePath());
        doc = builder.build(f);
    }
    
    /**
     * Test of unHex method, of class com.seefusion.Util.
     */
	@Test
    @SuppressWarnings("unchecked")
	public void testGetQuerySummary() {
		log("testGetQuerySummary");
        for(Iterator<Element> iter = doc.getRootElement().getChildren("testGetQuerySummary").iterator(); iter.hasNext(); ) {
        	String qtxt = iter.next().getChildText("queryText");
        	Query q = new Query();
            q.queryText = qtxt;
            assertEquals( qtxt.substring(0, 20), ReportUtil.getQuerySummary(q).substring(0, 20) );
        }
        
    }
    
}
