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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Serj Soloviov <serj@qitsoft.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportTest {

    @Spy
    private AbstractReport report = new TestAbstractReport();

    private MavenProject project = new MavenProject();

    @Before
    public void setUp() {
        report.setProject(project);
    }

    @Test
    public void testGettingFullDirectoryPath() {
        report.setReportParentDirectory(new File("parent"));
        report.setReportDirectoryName("report");
        File fullPath = report.getFullReportDirectory();

        assertEquals("parent/report", fullPath.getPath());
    }

    @Test
    public void testPreparePathsWhenTheReportPathDoesNotExist() throws Exception {
        File reportPath = mock(File.class);
        when(reportPath.exists()).thenReturn(false);
        when(reportPath.mkdirs()).thenReturn(true);
        doReturn(reportPath).when(report).getFullReportDirectory();

        report.preparePaths();

        verify(reportPath).mkdirs();
    }

    @Test
    public void testPreparePathsWhenTheReportPathExist() throws Exception {
        File reportPath = mock(File.class);
        when(reportPath.exists()).thenReturn(true);
        doReturn(reportPath).when(report).getFullReportDirectory();

        report.preparePaths();

        verify(reportPath, never()).mkdirs();
    }

    @Test(expected=MojoExecutionException.class)
    public void testPreparePathsWhenTheReportPathFailedToCreate() throws Exception {
        File reportPath = mock(File.class);
        when(reportPath.exists()).thenReturn(false);
        when(reportPath.mkdirs()).thenReturn(false);
        when(reportPath.getAbsolutePath()).thenReturn("absolute/path");
        doReturn(reportPath).when(report).getFullReportDirectory();

        report.preparePaths();
    }

    @Test
    public void testGetIncludeExcludeStringForNull() {
        assertEquals("", report.getIncludeExcludeString(null));
    }

    @Test
    public void testGetIncludeExcludeStringForEmptyArray() {
        assertEquals("", report.getIncludeExcludeString(new String[]{}));
    }

    @Test
    public void testGetIncludeExcludeStringForOneElementArray() {
        assertEquals("a", report.getIncludeExcludeString(new String[]{"a"}));
    }

    @Test
    public void testGetIncludeExcludeStringForManyElementsArray() {
        assertEquals("a,b", report.getIncludeExcludeString(new String[]{"a", "b"}));
    }

    @Test
    public void testCanGenerateReportsForEmptyFilesToProcess() throws Exception {
        doReturn(Collections.EMPTY_MAP).when(report).getFilesToProcess();
        assertFalse(report.canGenerateReport());
    }

    @Test
    public void testCanGenerateReportsForNonEmptyFIleToProcess() throws Exception {
        Map map = new HashMap();
        map.put(new File("a"), new File("b"));
        doReturn(map).when(report).getFilesToProcess();

        assertTrue(report.canGenerateReport());
    }

    @Test
    public void testGetFilesToProcessDefault() throws Exception {
        report.setCompileSourceRoots(Arrays.asList("src/main/java", "src/test/java", "src/main/resources"));

        Map<? extends File, ? extends File> result = report.getFilesToProcess();

        assertThat(result, hasEntry(
                matchesFilePath(equalTo("src/main/java/com/tacitknowledge/pluginsupport/report/AbstractReport.java")),
                matchesFilePath(equalTo("src/main/java"))));

        assertThat(result, hasEntry(
                matchesFilePath(equalTo("src/test/java/com/tacitknowledge/pluginsupport/report/ReportTest.java")),
                matchesFilePath(equalTo("src/test/java"))));

        assertThat(result, not(hasEntry(
                matchesFilePath(Matchers.startsWith("/src/main/resources")),
                matchesFilePath(equalTo("src/main/resources")))));
    }

    @Test
    public void testGetFilesToProcessWithExclusions() throws Exception {
        report.setCompileSourceRoots(Arrays.asList("src/main/java"));
        report.setExcludes(new String[]{"**/Abstract*.java"});

        Map<? extends File, ? extends File> result = report.getFilesToProcess();

        assertThat(result, not(hasEntry(
                matchesFilePath(equalTo("src/main/java/com/tacitknowledge/pluginsupport/report/AbstractReport.java")),
                matchesFilePath(equalTo("src/main/java")))));

        assertThat(result, hasEntry(
                matchesFilePath(equalTo("src/main/java/com/tacitknowledge/pluginsupport/report/ReportSummary.java")),
                matchesFilePath(equalTo("src/main/java"))));
    }

    @Test
    public void testGetFilesToProcessWithExcludeRoots() throws Exception {
        report.setCompileSourceRoots(Arrays.asList("src/main/java", "src/test/java"));
        report.setExcludeRoots(Arrays.asList("src/test/java"));

        Map<? extends File, ? extends File> result = report.getFilesToProcess();

        assertThat(result, not(hasEntry(
                matchesFilePath(equalTo("src/test/java/com/tacitknowledge/pluginsupport/report/ReportTest.java")),
                matchesFilePath(equalTo("src/test/java")))));

        assertThat(result, hasEntry(
                matchesFilePath(equalTo("src/main/java/com/tacitknowledge/pluginsupport/report/AbstractReport.java")),
                matchesFilePath(equalTo("src/main/java"))));
    }

    @Test
    public void testGetFilesToProcessWithInclusions() throws Exception {
        report.setCompileSourceRoots(Arrays.asList("src/main/java", "src/main/resources"));
        report.setIncludes(new String[]{"**/*.xsl"});

        Map<? extends File, ? extends File> result = report.getFilesToProcess();

        assertThat(result, hasEntry(
                matchesFilePath(equalTo("src/main/resources/junit-frames.xsl")),
                matchesFilePath(equalTo("src/main/resources"))));
    }

    @Test
    public void testGetFilesToProcessTestsIncludes() throws Exception {
        report.setCompileSourceRoots(Arrays.asList("src/main/java"));
        report.setTestSourceRoots(Arrays.asList("src/test/java"));
        report.setIncludeTests(true);

        Map<? extends File, ? extends File> result = report.getFilesToProcess();

        assertThat(result, hasEntry(
                matchesFilePath(equalTo("src/test/java/com/tacitknowledge/pluginsupport/report/ReportTest.java")),
                matchesFilePath(equalTo("src/test/java"))));
    }

    private Matcher<File> matchesFilePath(final Matcher<String> matcher) {
        return new BaseMatcher<File>() {

            public boolean matches(Object item)
            {
                return matcher.matches(((File)item).getPath());
            }

            public void describeTo(Description description)
            {

            }
        };
    }

    private static class TestAbstractReport extends AbstractReport {

        private boolean generateAggregate;

        private File aggregateReportFile;

        private List compileSourceRoots;

        private List excludeRoots;

        private String[] excludes;

        private String[] includes;

        private boolean includeTests;

        private String reportDirectoryName;

        private File reportParentDirectory;

        private List testSourceRoots;

        private MavenProject project;

        @Override
        public boolean isGenerateAggregate() {
            return generateAggregate;
        }

        @Override
        public void setGenerateAggregate(boolean generateAggregate) {
            this.generateAggregate = generateAggregate;
        }

        @Override
        public File getAggregateReportFile() {
            return aggregateReportFile;
        }

        @Override
        public void setAggregateReportFile(File aggregateReportFile) {
            this.aggregateReportFile = aggregateReportFile;
        }

        @Override
        public List getCompileSourceRoots() {
            return compileSourceRoots;
        }

        @Override
        public void setCompileSourceRoots(List compileSourceRoots) {
            this.compileSourceRoots = compileSourceRoots;
        }

        @Override
        public List getExcludeRoots() {
            return excludeRoots;
        }

        @Override
        public void setExcludeRoots(List excludeRoots) {
            this.excludeRoots = excludeRoots;
        }

        @Override
        public String[] getExcludes() {
            return excludes;
        }

        @Override
        public void setExcludes(String[] excludes) {
            this.excludes = excludes;
        }

        @Override
        public String[] getIncludes() {
            return includes;
        }

        @Override
        public void setIncludes(String[] includes) {
            this.includes = includes;
        }

        @Override
        public boolean isIncludeTests() {
            return includeTests;
        }

        @Override
        public void setIncludeTests(boolean includeTests) {
            this.includeTests = includeTests;
        }

        @Override
        public String getReportDirectoryName() {
            return reportDirectoryName;
        }

        @Override
        public void setReportDirectoryName(String reportDirectoryName) {
            this.reportDirectoryName = reportDirectoryName;
        }

        @Override
        public File getReportParentDirectory() {
            return reportParentDirectory;
        }

        @Override
        public void setReportParentDirectory(File reportParentDirectory) {
            this.reportParentDirectory = reportParentDirectory;
        }

        @Override
        public List getTestSourceRoots() {
            return testSourceRoots;
        }

        @Override
        public void setTestSourceRoots(List testSourceRoots) {
            this.testSourceRoots = testSourceRoots;
        }

        @Override
        public void setProject(MavenProject project) {
            this.project = project;
        }

        @Override
        public MavenProject getProject() {
            return project;
        }

        public void execute() throws MojoExecutionException, MojoFailureException {

        }
    }
}
