package com.tacitknowledge.pluginsupport.report;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.maven.plugin.MojoExecutionException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.*;


/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Sep 22, 2006
 * Time: 7:02:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportUtils {

//    static public String findReportFileNameFromSummary(ReportSummary reportSummary) {
//        File aggregate = reportSummary.getAggregate();
//        aggregate = aggregate.getName().substring(0,aggregate.getName().lastIndexOf("/"));
//        return aggregate + "/" + reportSummary.getReportFile();
//    }
//    static public File findReportFileFromSummary(ReportSummary reportSummary) {
//        String relativeFileName = findReportFileNameFromSummary(reportSummary);
//        return new File(relativeFileName);
//    }

   /**
    * Given the directory name, creates the directory.
    *
    * @param dir the name of the directory to create
    * @return A File object representing the created directory.
    */
   public static File createDirectory(String dir) {
      File reportsDir = new File(dir);
      reportsDir.mkdirs();
      return reportsDir;
   }

    /**
     * Generates relative path to report html file
     *
     * @param fullPath full path to report. usually {reportParentDirectory}/{reportDirectoryName}
     * @param pathToAggregate path to aggregate file
     * @param htmlFile name of html file to attach
     * @return relative path to html file
     */
    public static String generateRelativePathToReport(File pathToAggregate, File fullPath, String htmlFile)
    {
        fullPath = new File(fullPath, htmlFile);
        return pathToAggregate.getParentFile().toURI().relativize(fullPath.toURI()).getPath();
    }

   /**
    * Transforms some xml by applying the specified stylesheet and writes the output
    * to the supplied output stream.
    *
    * @param xml               the xml to transform
    * @param xsltFilename      the filename of the stylesheet to apply
    * @param transformedOutput the stream to write the output to
    * @throws FileNotFoundException if unable to fine the stylesheet
    */
   public static void transformXml(String xml, String xsltFilename, OutputStream transformedOutput) throws FileNotFoundException {
      if (xml == null) return;
      InputStream style = ReportUtils.class.getResourceAsStream(xsltFilename);
      XsltSupport support = new XsltSupport(style, new ByteArrayInputStream(xml.getBytes()), transformedOutput);
      support.execute();
   }

   /**
    * Inserts the provided report summary into the aggregate report.
    *
    * @param summary          The report to insert into the aggregate report
    * @param reportsDirectory The directory containing of the aggregate report
    * @throws FileNotFoundException
    */
   public static void handleAggregateReporting(ReportSummary summary, File reportsDirectory) throws FileNotFoundException {
      ReportUtils.handleAggregateReporting(summary, reportsDirectory, "index.xml");
   }

   /**
    * Inserts the provided report summary into the aggregate report.
    *
    * @param summary           The report to insert into the aggregate report
    * @param reportsDirectory  The directory containing of the aggregate report
    * @param aggregateFileName The name of the aggregate file
    * @throws FileNotFoundException
    */
   public static void handleAggregateReporting(ReportSummary summary, File reportsDirectory, String aggregateFileName) throws FileNotFoundException {
      AggregateReportHandler handler = new AggregateReportHandlerImpl();
      handler.handleReport(summary);
      InputStream style = ReportUtils.class.getResourceAsStream("/index.xslt");
      XsltSupport support = null;
      support = new XsltSupport(style, new File(reportsDirectory, aggregateFileName), new File(reportsDirectory, "index.html"));
      support.execute();
   }

   /**
    * The utility method to add a image child node that contains a image reference.
    * This will be added to the root node
    *
    * @param xmlFilePath the path to the xml report file
    * @param imageURI the image URI to add
    * @param imageDerscription image's description
    * @throws org.apache.maven.plugin.MojoExecutionException in case of error
    */
   public static void addImageChildNode(String xmlFilePath, String imageURI, String imageDerscription) throws MojoExecutionException {

      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(xmlFilePath);
         // get the document root node
         Element rootElement = doc.getDocumentElement();

         // Get the images container node
         NodeList imagesNodes = rootElement.getElementsByTagName("ReportImages");
         Node imagesNode = null;

         if (imagesNodes.getLength() == 0) {
            imagesNode = doc.createElement("ReportImages");
            rootElement.appendChild(imagesNode);
         } else {
            imagesNode = imagesNodes.item(0);
         }

         imagesNode.appendChild(createImageNode(doc, imageURI, imageDerscription));

         writeDOMTree(doc, new File(xmlFilePath));
      }

      // handle exception creating TransformerFactory
      catch (Exception e) {
         throw new MojoExecutionException("Error adding image to report", e);
      }

   }

   public static Node createImageNode(Document document, String imageURI, String imageDerscription) {

      // create FirstName and LastName elements
      Element imageURIElement = document.createElement("URI");
      Element imageALTElement = document.createElement("ALT");

      imageURIElement.appendChild(document.createTextNode(imageURI));
      imageALTElement.appendChild(document.createTextNode(imageDerscription));

      // create contact element
      Element image = document.createElement("image");

      // create attribute
      Attr imageAttribute = document.createAttribute("imageAttribute");
      imageAttribute.setValue("test");

      // append attribute to contact element
      image.setAttributeNode(imageAttribute);
      image.appendChild(imageURIElement);
      image.appendChild(imageALTElement);

      return image;
   }

   /**
    * Writes the DOM tree to disk
    * @param document
    * @param file
    * @throws IOException
    * @throws TransformerException
    */
   protected static void writeDOMTree(Document document, File file) throws IOException, TransformerException {
      // create DOMSource for source XML document
      Source xmlSource = new DOMSource(document);

      // create StreamResult for transformation result
      Result result = new StreamResult(new FileOutputStream(file));

      // create TransformerFactory
      TransformerFactory transformerFactory = TransformerFactory.newInstance();

      // create Transformer for transformation
      Transformer transformer = transformerFactory.newTransformer();

      transformer.setOutputProperty("indent", "yes");

      // transform and deliver content to client
      transformer.transform(xmlSource, result);
   }

}