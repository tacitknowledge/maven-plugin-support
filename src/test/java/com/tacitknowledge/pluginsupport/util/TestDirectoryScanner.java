package com.tacitknowledge.pluginsupport.util;

import junit.framework.TestCase;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Feb 18, 2007
 * Time: 5:25:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestDirectoryScanner extends TestCase {


    public void testList() {

        //NEEDS TO BE FIXED TO WORK WITH SUBVERSION OR USE A FAKE FILESYSTEM
        DirectoryScanner scanner = new DirectoryScanner(new File("src/main/java"), false,"**/*.java");
        List<File> files = scanner.list();
        assertFalse(files.isEmpty());
//        assertEquals(13,files.size());

        scanner = new DirectoryScanner(new File("src/main/java"),true,"**/Pipe.java");
        files = scanner.list();
        assertFalse(files.isEmpty());
//        assertEquals(1,files.size());

        scanner = new DirectoryScanner(new File("src/main/java"),true,"**/Pipe.java");
        files = scanner.list();
        assertFalse(files.isEmpty());
//        assertEquals(1,files.size());

    }
}
