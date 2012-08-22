package com.tacitknowledge.pluginsupport.report;

import java.io.File;


/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Sep 19, 2006
 * Time: 3:52:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AggregateReportHandler {

    void handleReport(ReportSummary reportSummary);
    public AggregateReport retrieveExistingReport(File file);
}
