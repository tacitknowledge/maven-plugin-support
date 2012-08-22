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
