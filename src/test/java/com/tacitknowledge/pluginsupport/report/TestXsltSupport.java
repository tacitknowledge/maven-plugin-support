package com.tacitknowledge.pluginsupport.report;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Feb 17, 2007
 * Time: 2:02:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestXsltSupport extends TestCase {


    public void testStuff() {
        String inputXML = "<com.tacitknowledge.pluginsupport.report.AggregateReport>\n" +
                "  <failure>false</failure>\n" +
                "  <reports>\n" +
                "    <com.tacitknowledge.pluginsupport.report.ReportSummary>\n" +
                "      <reportFile>build/reports</reportFile>\n" +
                "      <reportAlias>Dummy Report</reportAlias>\n" +
                "      <key>dummy</key>\n" +
                "      <passed>true</passed>\n" +
                "      <description>Dummy Report</description>\n" +
                "      <failBuild>false</failBuild>\n" +
                "      <aggregate>C:\\usr\\Fawkes\\promotion\\trunk\\plugins\\target\\reports\\index.xml</aggregate>\n" +
                "      <type>quality</type>\n" +
                "    </com.tacitknowledge.pluginsupport.report.ReportSummary></reports>\n" +
                "</com.tacitknowledge.pluginsupport.report.AggregateReport>";
        InputStream style = this.getClass().getResourceAsStream("/index.xslt");
        ByteArrayInputStream input = new ByteArrayInputStream(inputXML.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XsltSupport support = new XsltSupport(style,input,output);
        support.execute();
        String out = output.toString();
        assertTrue(out.length() > 1600);
    }

    private String expectedOutput() {
        String outputHtml = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\"http://www.w3.org/TR/html4/loose.dtd\">" +
                "<html>" +
                "<head>" +
                "<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                "<style>" +
                ".SummaryTitle  { }" +
                ".SummaryNumber { background-color:#DDDDDD; text-align: center; }" +
                "            .passed { background-color:#00FF00; text-align: center; }" +
                "            .failed { background-color:#FF0000; text-align: center; }" +
                "            .ItemNumber    { background-color: #DDDDDD; }" +
                ".CodeFragment  { background-color: #BBBBBB; display:none; font:normal normal normal 9pt Courier; }" +
                ".ExpandButton  { background-color: #FFFFFF; font-size: 8pt; width: 20px; height: 20px; margin:0px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Build Report Summary</h2>" +
                "    This page provides a dashboard of report results." +
                "    <p></p>" +
                "<p>" +
                "<h4>Correctness Reports</h4>" +
                "</p>" +
                "<table border=\"1\" class=\"summary\" cellpadding=\"2\">" +
                "<tr style=\"background-color:#CCCCCC;\">" +
                "<th>Report</th><th>Description</th><th>Status</th>" +
                "</tr>" +
                "</table>" +
                "<p></p>" +
                "<p>" +
                "<h4>Quality Metrics Reports</h4>" +
                "</p>" +
                "<table border=\"1\" class=\"summary\" cellpadding=\"2\">" +
                "<tr style=\"background-color:#CCCCCC;\">" +
                "<th>Report</th><th>Description</th><th>Status</th>" +
                "</tr>" +
                "<tr>" +
                "<td class=\"SummaryNumber\"><a href=\"build/reports\">Dummy Report</a></td><td class=\"SummaryNumber\">Dummy Report</td><td class=\"passed\">Passed</td>" +
                "</tr>" +
                "</table>" +
                "<p></p>" +
                "<p>" +
                "<h4>Informational Reports</h4>" +
                "</p>" +
                "<table border=\"1\" class=\"summary\" cellpadding=\"2\">" +
                "<tr style=\"background-color:#CCCCCC;\">" +
                "<th>Report</th><th>Description</th><th>Status</th>" +
                "</tr>" +
                "</table>" +
                "<p></p>" +
                "<p>" +
                "<h4>Reports Coming Soon</h4>" +
                "</p>" +
                "<table border=\"1\" class=\"summary\" cellpadding=\"2\">" +
                "<tr style=\"background-color:#CCCCCC;\">" +
                "<th>Report</th><th>Description</th><th>Status</th>" +
                "</tr>" +
                "</table>" +
                "</body>" +
                "</html>";
        return outputHtml;
    }


}
