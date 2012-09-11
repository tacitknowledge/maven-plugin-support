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
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.IIOException;
import org.codehaus.plexus.util.FileUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class AggregateReportHandlerImplTest {

    private AggregateReportHandler handler = new AggregateReportHandlerImpl();

//    @Test
//    public void testStuff() {
//        File reportsDirectory = new File("target/reports");
//        if (!reportsDirectory.exists())
//            reportsDirectory.mkdir();
//        ReportSummary summary = new ReportSummary(new File(reportsDirectory,"index.xml"),"Dummy Report","build/reports","dummy",true,"Dummy Report",false, ReportSummary.INFO);
//        handler.handleReport(summary);
//        summary = new ReportSummary(new File(reportsDirectory,"index.xml"),"Dummy TWO Report","build/reports","dummy",true,"Dummy Report",false, ReportSummary.INFO);
//        handler = new AggregateReportHandlerImpl();
//        handler.handleReport(summary);
//    }
    @Test
    public void testRetrieveExistingReportWithoutFile() {
        AggregateReport report = handler.retrieveExistingReport(new File("abrakadabra"));
        assertEquals(0, report.getReports().size());
        assertNull(report.getProject());
    }

    @Test
    public void testRetrieveExistingReportWithFile() {
        AggregateReport report = handler.retrieveExistingReport(new File("src/test/resources/dummy-report.xml"));

        assertEquals(1, report.getReports().size());
        ReportSummary reportSummary = (ReportSummary) report.getReports().get(0);
        assertEquals("build/reports", reportSummary.getReportFile());
        assertEquals("Dummy Report", reportSummary.getReportAlias());
        assertEquals("dummy", reportSummary.getKey());
        assertEquals(true, reportSummary.isPassed());
        assertEquals(false, reportSummary.isFailBuild());
        assertEquals("Dummy Report", reportSummary.getDescription());
        assertEquals("C:\\usr\\Fawkes\\promotion\\trunk\\plugins\\target\\reports\\index.xml", reportSummary.getAggregate().getPath());
        assertEquals("quality", reportSummary.getType());

        assertNull(report.getProject());
    }

    @Test
    public void testHandleReport() throws Exception {
        File destReport = File.createTempFile("new-dummy-report", ".xml");
        FileUtils.copyFile(new File("src/test/resources/dummy-report.xml"), destReport);
        final ReportSummary summary = new ReportSummary(destReport, "Dummy Report1", "build/reports", "dummy1", true, "Dummy Report1", false, ReportSummary.INFO);
        handler.handleReport(summary);

        AggregateReport aggregateReport = new AggregateReportHandlerImpl().retrieveExistingReport(destReport);
        assertEquals(2, aggregateReport.getReports().size());

        assertThat(aggregateReport.getReports().toArray(), hasItemInArray(new ReportSummaryMatcher(summary)));
        destReport.delete();
    }

    @Test
    public void testHandleInexistentReport() throws Exception {
        File destReport = File.createTempFile("new-dummy-report", ".xml");
        destReport.delete();

        final ReportSummary summary = new ReportSummary(destReport, "Dummy Report1", "build/reports", "dummy1", true, "Dummy Report1", false, ReportSummary.INFO);
        handler.handleReport(summary);

        assertTrue(destReport.exists());
        AggregateReport aggregateReport = new AggregateReportHandlerImpl().retrieveExistingReport(destReport);
        assertEquals(1, aggregateReport.getReports().size());

        assertThat(aggregateReport.getReports().toArray(), hasItemInArray(new ReportSummaryMatcher(summary)));
        destReport.delete();
    }

    // The expected exception should be other than a general one.
    @Test(expected=Exception.class)
    public void testHandleInexistentReportWithBrokenFileName() throws Exception {
        File destReport = new File(":/$::::");

        final ReportSummary summary = new ReportSummary(destReport, "Dummy Report1", "build/reports", "dummy1", true, "Dummy Report1", false, ReportSummary.INFO);
        handler.handleReport(summary);
    }

    private static class ReportSummaryMatcher extends BaseMatcher<Object> {

        private ReportSummary summary;

        public ReportSummaryMatcher(ReportSummary summary) {
            this.summary = summary;
        }

        public boolean matches(Object item) {
            ReportSummary obj = (ReportSummary) item;
            return summary.isPassed() == obj.isPassed()
                    && summary.isFailBuild() == obj.isFailBuild()
                    && summary.getAggregate().equals(obj.getAggregate())
                    && summary.getDescription().equals(obj.getDescription())
                    && summary.getKey().equals(obj.getKey())
                    && summary.getModuleName().equals(obj.getModuleName())
                    && summary.getReportAlias().equals(obj.getReportAlias())
                    && summary.getReportFile().equals(obj.getReportFile())
                    && summary.getType().equals(obj.getType());
        }

        public void describeTo(Description description) {
        }
    };
}
