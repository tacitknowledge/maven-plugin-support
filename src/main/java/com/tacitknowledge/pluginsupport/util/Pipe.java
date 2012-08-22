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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Feb 18, 2007
 * Time: 7:50:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pipe implements Runnable {
    private InputStream in;
    private OutputStream out;

    private int bufferSize = 1024;

    /**
     * Constructor
     * @param in stream to read from
     * @param out stream to write to
     */
    public Pipe(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public Pipe(InputStream in, OutputStream out, int bufferSize) {
        this(in, out);
        this.bufferSize = bufferSize;
    }

    /**
     * start reading and writing
     */
    public void run() {
        try {
            byte[] buf = new byte[bufferSize];
            int pos;

            while ((pos = in.read(buf)) != -1) {
                out.write(buf, 0, pos);
                out.flush();
            }
            in.close();
            out.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
