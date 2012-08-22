package com.tacitknowledge.pluginsupport.util;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Mar 29, 2007
 * Time: 2:12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPatternFilter extends TestCase {

    public void testPatternFilter() throws Exception {
        String pattern = "**/**/three";
        PatternFilter pf = new PatternFilter(pattern);

        String s1 = "one/two/three";
        String s2 = "one/two/dir/three";
        String s3 = "one/one/one";


        assertTrue(pf.accept(s1));
        assertTrue(pf.accept(s2));
        assertFalse(pf.accept(s3));

        pattern = "one/**/three";
        pf = new PatternFilter(pattern);
        assertTrue(pf.accept(s1));
        assertTrue(pf.accept(s2));
        assertFalse(pf.accept(s3));

        pattern = "**/*.java";
        pf = new PatternFilter(pattern);
        assertFalse(pf.accept(s1));
        assertFalse(pf.accept(s2));
        assertFalse(pf.accept(s3));
        assertTrue(pf.accept("one/two/three.java"));
    }

    public void testReplacePattern() {
        String expectedPattern = ".*.*/.*\\.java";
        String input = "**/*.java";
        PatternFilter pf = new PatternFilter("*");
        assertEquals(expectedPattern,pf.replacePattern(input));
    }



}
