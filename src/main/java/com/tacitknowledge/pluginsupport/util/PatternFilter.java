package com.tacitknowledge.pluginsupport.util;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;

import java.io.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Mar 29, 2007
 * Time: 2:11:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternFilter implements FileFilter {

    final String[] specialChars = new String[]{
            "\\",
            ".",
            "*",
            "+",
            "?",
            "{",
            "[",
            "(",
            ")",
            "]",
            "}",
            "|",
            "^",
            "$"
    };

    String pattern;
    public PatternFilter(String pattern) {
        this.pattern = pattern;
    }

    public boolean accept(File pathname) {
        return accept(pathname.getAbsolutePath().replace(File.separator,"/"));
    }

    public boolean accept(String s) {

        RegularExpression regularExpression = new RegularExpression(replacePattern(pattern));
        return regularExpression.matches(s);
    }
    String replacePattern(String input) {
        for (String s : specialChars) {
            input = input.replace(s,'\\' + s);
        }
        input = input.replace("\\*", ".*");
        return input;
    }
}
