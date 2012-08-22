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
import java.io.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Feb 18, 2007
 * Time: 5:18:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryScanner {

    public static final FileFilter DIR_FILTER = new FileFilter() {
       public boolean accept(File pathname) {
           return pathname.isDirectory();
       }
    };

    final File rootDir;
    final boolean recurse;
    final List<File> found = new ArrayList<File>();
    final FileFilter fileFilter;
    final String patternFilter;
    final PatternFilter filter;


    public DirectoryScanner(final File rootDir, final boolean recurse, final String patternFilter) {
        this.rootDir = rootDir;
        this.recurse = recurse;
        this.patternFilter = patternFilter;
        this.filter = new PatternFilter(patternFilter);
        fileFilter = new FileFilter() {
           public boolean accept(File pathname) {
               return !pathname.isDirectory() && filter.accept(pathname);
           }
        };
    }

    public DirectoryScanner(File rootDir) {
        this(rootDir,true,"*.java");
    }

    public List<File> list() {
        list(rootDir);
        return found;
    }


    void list(File directory) {
        File[] children = directory.listFiles(fileFilter);
        if (children != null)
            found.addAll(Arrays.asList(children));

        File[] childDirectories = directory.listFiles(DIR_FILTER);
        if (childDirectories != null) {
            for (File file : childDirectories) {
                list(file);
            }
        }

    }

    private static class FileFilterDecorator implements FileFilter {
        private FileFilter delegate;

        public FileFilterDecorator(FileFilter delegate) {
            this.delegate = delegate;
        }

        public boolean accept(File file) {
            if (delegate == null)
                return true;
            return delegate.accept(file);
        }
    }
}
