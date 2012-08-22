package com.tacitknowledge.pluginsupport.util;

import junit.framework.TestCase;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Feb 19, 2007
 * Time: 2:05:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestJunitXMLMerger extends TestCase {



    public void testStuff() throws IOException, TransformerException {
        File reportsDirectory = new File("target/surefire-reports");
        File indexReportsDirectory = new File("target/reports");
        JunitXMLMerger merger = new JunitXMLMerger(reportsDirectory,new File(indexReportsDirectory,"junit"));
        merger.execute();
    }
}
