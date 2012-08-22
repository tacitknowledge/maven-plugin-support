package com.tacitknowledge.pluginsupport.report;


import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Sep 23, 2006
 * Time: 8:11:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class XsltSupport {

    private InputStream style;
    private InputStream input;
    private OutputStream output;


    public XsltSupport(String style, File inputFile, File outputFile) throws FileNotFoundException {
        this(new File(style),inputFile,outputFile);
    }
    public XsltSupport(File style, File inputFile, File outputFile) throws FileNotFoundException {
        this(new FileInputStream(style),new FileInputStream(inputFile), new FileOutputStream(outputFile));
    }
    public XsltSupport(InputStream style, File inputFile, File outputFile) throws FileNotFoundException {
        this(style,new FileInputStream(inputFile), new FileOutputStream(outputFile));
    }

    public XsltSupport(InputStream style, InputStream input, OutputStream output) {
        this.style = style;
        this.input = input;
        this.output = output;
    }

    public void execute() {
        try {
            StreamSource stylesource = new StreamSource(style);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
            StreamResult result = new StreamResult(output);
            Source documentSource = new StreamSource(input);
            transformer.transform(documentSource,result);
            this.style.close();
            this.input.close();
            this.output.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
