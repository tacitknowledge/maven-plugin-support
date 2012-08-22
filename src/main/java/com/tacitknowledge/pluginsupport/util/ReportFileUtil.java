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

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Sep 20, 2006
 * Time: 12:31:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportFileUtil {

    public static int getLines(String fileName) throws IOException{
        File file = new File(fileName);
        return getLines(file);
    }

    public static int getLines(File file) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            while((line = bufferedReader.readLine())!= null) {
                count++;
            }
            return count;
        } finally {
            if (bufferedReader != null) bufferedReader.close();
        }
    }
    public static String loadFile(File file) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            StringBuffer buf = new StringBuffer();
            while((line = bufferedReader.readLine())!= null) {
                buf.append(line);
            }
            return buf.toString();
        } finally {
            if (bufferedReader != null) bufferedReader.close();
        }
    }

    public static boolean fileContainsLineWith(File file,String string) throws IOException {
        return fileContainsLineWith(file,new String[] {string});
    }
    public static boolean fileContainsLineWith(File file,String[] strings) throws IOException {
        if (!file.exists()) return false;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while((line = bufferedReader.readLine())!= null) {
                int tokencount = 0;
                for (String string : strings) {
                    if (line.indexOf(string) != -1) tokencount++;
                }
                if(tokencount == strings.length) return true;
            }
            return false;
        } finally {
            if (bufferedReader != null) bufferedReader.close();
        }
    }

}
