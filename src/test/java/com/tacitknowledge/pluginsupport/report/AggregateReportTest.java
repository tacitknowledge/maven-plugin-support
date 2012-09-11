/*
 * Copyright 2012 Tacit Knowledge.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tacitknowledge.pluginsupport.report;

import com.tacitknowledge.pluginsupport.util.ReportFileUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
public class AggregateReportTest {

    private AggregateReport aggregateReport = new AggregateReport();

    @Test
    public void testSetReports() {
        ReportSummary reportSummary = new ReportSummary();
        List<ReportSummary> resports = Arrays.asList(reportSummary);

        aggregateReport.setReports(resports);

        assertArrayEquals(new ReportSummary[]{reportSummary}, aggregateReport.getReports().toArray());
    }

    @Test
    public void testAddReport() {
        ReportSummary reportSummary = new ReportSummary();
        aggregateReport.addReport(reportSummary);

        assertArrayEquals(new ReportSummary[]{reportSummary}, aggregateReport.getReports().toArray());
    }

    @Test
    public void testAddNullReport() {
        aggregateReport.addReport(null);

        assertArrayEquals(new ReportSummary[]{}, aggregateReport.getReports().toArray());
    }

    @Test
    public void testReportHasFailure() {
        ReportSummary reportSummary1 = new ReportSummary();
        reportSummary1.setPassed(true);
        reportSummary1.setFailBuild(false);
        aggregateReport.addReport(reportSummary1);

        ReportSummary reportSummary2 = new ReportSummary();
        reportSummary2.setPassed(false);
        reportSummary2.setFailBuild(true);
        aggregateReport.addReport(reportSummary2);

        assertTrue(aggregateReport.hasFailure());
    }

    @Test
    public void testReportHasNotFailure() {
        ReportSummary reportSummary1 = new ReportSummary();
        reportSummary1.setPassed(true);
        reportSummary1.setFailBuild(false);
        aggregateReport.addReport(reportSummary1);

        ReportSummary reportSummary2 = new ReportSummary();
        reportSummary2.setPassed(false);
        reportSummary2.setFailBuild(false);
        aggregateReport.addReport(reportSummary2);

        assertFalse(aggregateReport.hasFailure());
    }

    // TODO: From my point of view this test shoulod fail. Please check the logic.
    @Test
    public void testReportHasFailureAndPassed() {
        ReportSummary reportSummary2 = new ReportSummary();
        reportSummary2.setPassed(true);
        reportSummary2.setFailBuild(true);
        aggregateReport.addReport(reportSummary2);

        assertFalse(aggregateReport.hasFailure());
    }

    @Test
    public void testGetFailureMessageForPassedReports() {
        ReportSummary reportSummary = new ReportSummary();
        reportSummary.setPassed(false);
        reportSummary.setFailBuild(false);
        aggregateReport.addReport(reportSummary);

        assertEquals("", aggregateReport.getFailureMessage());
    }

    // TODO: From my point of view this test shoulod fail. Please check the logic.
    @Test
    public void testGetFailureMessageForFailedAndPassedReports() {
        ReportSummary reportSummary = new ReportSummary();
        reportSummary.setPassed(true);
        reportSummary.setFailBuild(true);
        aggregateReport.addReport(reportSummary);

        assertEquals("", aggregateReport.getFailureMessage());
    }

    @Test
    public void testGetFailureMessageForOneFailedReportWithFileOverride() {
        ReportSummary reportSummary = new ReportSummary();
        reportSummary.setPassed(false);
        reportSummary.setFailBuild(true);
        reportSummary.setReportAlias("ALIAS");
        reportSummary.setReportFile("REPORT-FILE");
        reportSummary.setReportFileOverride("OVERRIDED-FILE");
        aggregateReport.addReport(reportSummary);

        assertEquals("Error: ALIAS failed threshold checks. See OVERRIDED-FILE for details", aggregateReport.getFailureMessage().trim());
    }

    @Test
    public void testGetFailureMessageForOneFailedReportWithReportFile() {
        ReportSummary reportSummary = new ReportSummary();
        reportSummary.setPassed(false);
        reportSummary.setFailBuild(true);
        reportSummary.setReportAlias("ALIAS");
        reportSummary.setReportFile("REPORT-FILE");
        reportSummary.setReportFileOverride(null);
        aggregateReport.addReport(reportSummary);

        assertEquals("Error: ALIAS failed threshold checks. See REPORT-FILE for details", aggregateReport.getFailureMessage().trim());
    }

    @Test
    public void testGetFailureMessageForManyFailedReportWithReportFile() {
        ReportSummary reportSummary1 = new ReportSummary();
        reportSummary1.setPassed(false);
        reportSummary1.setFailBuild(true);
        reportSummary1.setReportAlias("ALIAS");
        reportSummary1.setReportFile("REPORT-FILE");
        reportSummary1.setReportFileOverride(null);
        aggregateReport.addReport(reportSummary1);

        ReportSummary reportSummary2 = new ReportSummary();
        reportSummary2.setPassed(false);
        reportSummary2.setFailBuild(true);
        reportSummary2.setReportAlias("ALIAS1");
        reportSummary2.setReportFileOverride("OVERRIDED-FILE");
        aggregateReport.addReport(reportSummary2);

        String[] messages = aggregateReport.getFailureMessage().trim().split("(\\s*\\n\\s*)");

        assertEquals("Error: ALIAS failed threshold checks. See REPORT-FILE for details", messages[0]);
        assertEquals("Error: ALIAS1 failed threshold checks. See OVERRIDED-FILE for details", messages[1]);
    }



}
