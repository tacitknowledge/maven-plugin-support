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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.taskdefs.optional.junit.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Feb 19, 2007
 * Time: 1:33:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class JunitXMLMerger {


    java.lang.String TESTSUITES = "testsuites";
    java.lang.String TESTSUITE = "testsuite";
    java.lang.String TESTCASE = "testcase";
    java.lang.String ERROR = "error";
    java.lang.String FAILURE = "failure";
    java.lang.String SYSTEM_ERR = "system-err";
    java.lang.String SYSTEM_OUT = "system-out";
    java.lang.String ATTR_PACKAGE = "package";
    java.lang.String ATTR_NAME = "name";
    java.lang.String ATTR_TIME = "time";
    java.lang.String ATTR_ERRORS = "errors";
    java.lang.String ATTR_FAILURES = "failures";
    java.lang.String ATTR_TESTS = "tests";
    java.lang.String ATTR_TYPE = "type";
    java.lang.String ATTR_MESSAGE = "message";
    java.lang.String PROPERTIES = "properties";
    java.lang.String PROPERTY = "property";
    java.lang.String ATTR_VALUE = "value";
    java.lang.String ATTR_CLASSNAME = "classname";
    java.lang.String ATTR_ID = "id";


      /** the current generated id */
    protected int generatedId = 0;

    File rootDir;
    File toDir;

    public JunitXMLMerger(File rootDir, File toDir) {
        this.rootDir = rootDir;
        this.toDir = toDir;
    }
   
    public void execute() throws TransformerException, IOException {
        Element doc = createDocument();
        InputStream style = this.getClass().getResourceAsStream("/junit-frames.xsl");
        StreamSource stylesource = new StreamSource(style);
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        OutputStream outputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outputStream);
        Source documentSource = new DOMSource(doc.getOwnerDocument());
        transformer.setParameter("output.dir", toDir.getAbsolutePath());
        transformer.transform(documentSource,result);
        style.close();
        outputStream.close();
    }

    /**
     * Get all <code>.xml</code> files in the fileset.
     *
     * @return all files in the fileset that end with a '.xml'.
     */
    protected File[] getFiles() {
        DirectoryScanner scanner = new DirectoryScanner(rootDir,true,"*.xml");
        List<File> files = scanner.list();
        return files.toArray(new File[files.size()]);
    }

    /**
     * <p> Create a DOM tree.
     * Has 'testsuites' as firstchild and aggregates all
     * testsuite results that exists in the base directory.
     * @return  the root element of DOM tree that aggregates all testsuites.
     */
    protected Element createDocument() {
        // create the dom tree
        DocumentBuilder builder = getDocumentBuilder();
        Document doc = builder.newDocument();
        Element rootElement = doc.createElement(TESTSUITES);
        doc.appendChild(rootElement);

        generatedId = 0;

        // get all files and add them to the document
        File[] files = getFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                if(file.length()>0) {
                    Document testsuiteDoc
                            = builder.parse("file:///" + file.getAbsolutePath());
                    Element elem = testsuiteDoc.getDocumentElement();
                    // make sure that this is REALLY a testsuite.
                    if (TESTSUITE.equals(elem.getNodeName())) {
                        addTestSuite(rootElement, elem);
                        generatedId++;
                    } else {
                    }
                } else {
                }
            } catch (SAXException e) {
                // a testcase might have failed and write a zero-length document,
                // It has already failed, but hey.... mm. just put a warning
            } catch (IOException e) {
            }
        }
        return rootElement;
    }

    protected void addTestSuite(Element root, Element testsuite) {
        String fullclassname = testsuite.getAttribute(ATTR_NAME);
        int pos = fullclassname.lastIndexOf('.');

        // a missing . might imply no package at all. Don't get fooled.
        String pkgName = (pos == -1) ? "" : fullclassname.substring(0, pos);
        String classname = (pos == -1) ? fullclassname : fullclassname.substring(pos + 1);
        Element copy = (Element) DOMUtil.importNode(root, testsuite);

        // modify the name attribute and set the package
        copy.setAttribute(ATTR_NAME, classname);
        copy.setAttribute(ATTR_PACKAGE, pkgName);
        copy.setAttribute(ATTR_ID, Integer.toString(generatedId));
    }

    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception exc) {
            throw new ExceptionInInitializerError(exc);
        }
    }
}
