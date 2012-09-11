package com.tacitknowledge.pluginsupport.report;

import com.thoughtworks.xstream.XStream;

import java.io.*;



/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Sep 19, 2006
 * Time: 10:02:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class AggregateReportHandlerImpl implements AggregateReportHandler {

    private Writer writer = null;
    private Reader reader = null;

    /**
     * This is to support unit testing
     * @param writer
     * @param reader
     */
    public AggregateReportHandlerImpl(Writer writer, Reader reader) {
        this.writer = writer;
        this.reader = reader;
    }

    public AggregateReportHandlerImpl() {

    }

    public void handleReport(ReportSummary reportSummary) {
        File reportFile = getReportAggregate(reportSummary);
        AggregateReport aggregateReport = retrieveExistingReport(reportFile);
        aggregateReport.addReport(reportSummary);
        writeAggregateReport(aggregateReport,reportFile);
    }

    private File getReportAggregate(ReportSummary reportSummary) {
        return reportSummary.getAggregate();
    }

    private void writeAggregateReport(AggregateReport aggregateReport, File file) {
        if (writer == null) {
            writer = getWriter(file);
        }

        try {
            XStream xStream = new XStream();
            xStream.toXML(aggregateReport,writer);
        } finally {
            if (writer != null) try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public AggregateReport retrieveExistingReport(File  file) {
        if (reader == null) {
            reader = getReader(file);
        }
        if (reader == null) {
            return new AggregateReport();
        }
        try {
            XStream xStream = new XStream();
            AggregateReport aggregateReport = (AggregateReport) xStream.fromXML(reader);
            return aggregateReport;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Writer getWriter(File file) {
        FileWriter writer = null;

// This piece of code is redundant. The FileWriter creates the file if is needed. The code which shows
// to the user the message about impossiblity to create the file in catch statement.
//
//        if (!file.exists()) {
//            try {
//                if (!file.createNewFile()) {
//                    System.out.println("Could not create [" + file.getName() + "]");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            return null;
        }
        return writer;
    }

    private Reader getReader(File file) {

        try {
            Reader reader = new FileReader(file);
            return reader;
        } catch (FileNotFoundException e) {
            return null;
        }
    }


}
