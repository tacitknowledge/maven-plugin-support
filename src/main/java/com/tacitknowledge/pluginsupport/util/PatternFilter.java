/* Copyright 2012 Tacit Knowledge
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

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
