package com.tacitknowledge.pluginsupport.report;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Sep 19, 2006
 * Time: 10:10:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class AggregateReport {
    private String project;
    private boolean failure = false;
    private List reports = new ArrayList();

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List getReports() {
        return reports;
    }

    public void setReports(List reports) {
        this.reports = reports;
    }
    public void addReport(ReportSummary reportSummary) {
        if (reportSummary != null)
            reports.add(reportSummary);
    }
    public boolean hasFailure() {

        for (Iterator iter = getReports().iterator();iter.hasNext();) {
            ReportSummary reportSummary = (ReportSummary) iter.next();
            if (reportSummary.isFailBuild() && !reportSummary.isPassed()) return true;
        }

        return false;

    }
    public String getFailureMessage() {
        StringBuffer buf = new StringBuffer();
        for (Iterator iter = getReports().iterator();iter.hasNext();) {
            ReportSummary reportSummary = (ReportSummary) iter.next();
            if (reportSummary.isFailBuild() && !reportSummary.isPassed()) {

                buf.append( "\n\n\n\tError: " + reportSummary.getReportAlias() + " failed threshold checks. See ");

                if( reportSummary.getReportFileOverride() != null)
                {
                    buf.append( reportSummary.getReportFileOverride());
                }
                else
                {
                    //Normally this is what is called
                    buf.append( reportSummary.getReportFile());
                 }
                 buf.append( " for details\n\n");
            }
        }
        return buf.toString();

    }

    public void updateStatus() {
        failure = hasFailure();
    }
}
