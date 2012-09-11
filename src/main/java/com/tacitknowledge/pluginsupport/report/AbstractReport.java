package com.tacitknowledge.pluginsupport.report;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Base class for PMD reports.
 *
 * @author Andrian Rusnac (arusnac@tacitknowledge.com)
 */
public abstract class AbstractReport extends AbstractMojo
{
	/**
     * @see org.apache.maven.reporting.AbstractMavenReport#canGenerateReport()
     */
    public boolean canGenerateReport()
    {
//        if (!getProject().isExecutionRoot())
//        {
//            getLog().info("Skipping report, project is not execution root");
//            return false;
//        }

        try
        {
            Map filesToProcess = getFilesToProcess();

            if (filesToProcess.isEmpty())
            {
                getLog().info("Skipping report, no files to process");
                return false;
            }
        }
        catch (IOException e)
        {
            getLog().error(e);
        }
        return true;
    }

    /**
     * Convenience method to get the list of files on which the plugin tool will be executed
     *
     * @return a List of the files on which the plugin tool will be executed
     * @throws java.io.IOException
     */
    protected Map getFilesToProcess() throws IOException
    {
//        if (!getProject().isExecutionRoot())
//        {
//            return Collections.EMPTY_MAP;
//        }

        List excludeRoots = getExcludeRoots();
        if (excludeRoots == null)
        {
            excludeRoots = Collections.EMPTY_LIST;
        }
        List<File> excludeRootFiles = new ArrayList<File>(excludeRoots.size());

        for (Object excludeRoot : excludeRoots)
        {
            String root = (String) excludeRoot;
            File file = new File(root);

            if (file.exists() && file.isDirectory())
            {
                excludeRootFiles.add(file);
            }
        }

        List<File> directories = new ArrayList<File>();

        List compileSourceRoots = getCompileSourceRoots();
        getLog().info("Found ["+ compileSourceRoots.size() + "] source roots");
        for (Object compileSourceRoot : compileSourceRoots)
        {
            String root = (String) compileSourceRoot;
            File sroot = new File(root);
            directories.add(sroot);
        }

        if (isIncludeTests())
        {
            List testSourceRoots = getTestSourceRoots();
            for (Object testSourceRoot : testSourceRoots)
            {
                String root = (String) testSourceRoot;
                File sroot = new File(root);
                directories.add(sroot);
            }
        }

        String excluding = getIncludeExcludeString(getExcludes());
        String including = getIncludeExcludeString(getIncludes());
        Map<File, File> files = new TreeMap<File, File>();
        StringBuffer excludesStr = new StringBuffer();
        String[] defaultExcludes = FileUtils.getDefaultExcludes();

        if (StringUtils.isEmpty(including))
        {
            including = "**/*.java";
        }

        if (StringUtils.isNotEmpty(excluding))
        {
            excludesStr.append(excluding);
        }

        for (String defaultExclude : defaultExcludes)
        {
            if (excludesStr.length() > 0)
            {
                excludesStr.append(",");
            }
            excludesStr.append(defaultExclude);
        }
        getLog().debug("Excluded files: '" + excludesStr + "'");

        for (Iterator it = directories.iterator(); it.hasNext();)
        {
            File sourceDirectory = (File) it.next();
            if (sourceDirectory.exists() && sourceDirectory.isDirectory()
                && !excludeRootFiles.contains(sourceDirectory))
            {
                List newfiles = FileUtils.getFiles(sourceDirectory, including,
                		excludesStr.toString());
                for (Iterator it2 = newfiles.iterator(); it2.hasNext();)
                {
                    files.put((File) it2.next(), sourceDirectory);
                }
            }
        }

        return files;
    }

    /**
     * Convenience method to return the target directory that receives plugin's report output
     * @return the target directory that receives plugin's report output
     */
    protected File getFullReportDirectory()
    {
    	return new File(getReportParentDirectory(), getReportDirectoryName());
    }

    /**
     * The method creates the directories needed for report generation
     *
     * @throws MojoExecutionException
     */
    protected void preparePaths() throws MojoExecutionException
	{
		boolean folderCreated = true;
		File fullReportDirectory = getFullReportDirectory();

		if (!fullReportDirectory.exists())
		{
			folderCreated = fullReportDirectory.mkdirs();
		}

		if (!folderCreated)
		{
			throw new MojoExecutionException("Could not create report directory: "
					+ fullReportDirectory.getAbsolutePath());
		}
	}

    /**
     * Convenience method that concatenates the files to be excluded into the appropriate format.
     *
     * @param arr the array of Strings that contains the files to be excluded
     * @return a String that contains the concatenated file names
     */
    protected String getIncludeExcludeString(String[] arr)
    {
        StringBuffer str = new StringBuffer();

        if (arr != null)
        {
            for (String anArr : arr)
            {
                if (str.length() > 0)
                {
                    str.append(',');
                }
                str.append(anArr);
            }
        }

        return str.toString();
    }

    /**
     * Renders the aggregation report with PMD validation results
     * @param reportSummary report containing validation results
     * @throws MojoExecutionException In case of an IO error
     */
    protected void renderAggregateReport(ReportSummary reportSummary) throws MojoExecutionException
    {
    	if (isGenerateAggregate())
    	{
	    	getLog().info("Adding plugin report results to aggregate report...");

    		boolean reportError = !reportSummary.isPassed();
	    	if (reportError)
	    	{
	    		getLog().error("Plugin violations detected");
	    	}

	    	try
	    	{
				ReportUtils.handleAggregateReporting(reportSummary,
						getAggregateReportFile().getParentFile(),
						getAggregateReportFile().getName());
			}
	    	catch (FileNotFoundException e)
	    	{
				throw new MojoExecutionException("Aggregation files could not be found", e);
			}
    	}
    }

	/**
	 * @return true if aggregate report must be created
	 */
	public abstract boolean isGenerateAggregate();

	/**
	 * @param generateAggregate pass true if you want an aggregate report to be generated
	 */
	public abstract void setGenerateAggregate(boolean generateAggregate);

	/**
	 * @return The location where the report for this plugin will be generated.
	 */
	public abstract File getAggregateReportFile();

	/**
	 * Sets the location where the report for this plugin will be generated.
	 * @param aggregateReportFile location where the report for this plugin will be generated.
	 */
	public abstract void setAggregateReportFile(File aggregateReportFile);

	/**
	 * @return The directories containing the sources to be compiled.
	 */
	public abstract List getCompileSourceRoots();

	/**
	 * Sets the directories containing the sources to be compiled.
	 * @param compileSourceRoots The directories containing the sources to be compiled.
	 */
	public abstract void setCompileSourceRoots(List compileSourceRoots);

	/**
	 * @return The project source directories that should be excluded.
	 */
	public abstract List getExcludeRoots();

	/**
	 * Sets the project source directories that should be excluded.
	 * @param excludeRoots The project source directories that should be excluded.
	 */
	public abstract void setExcludeRoots(List excludeRoots);

	/**
	 * @return a list of files to exclude from checking.
	 */
	public abstract String[] getExcludes();

	/**
	 * Sets a list of files to exclude from checking. Can contain ant-style wildcards and double
     * wildcards.
	 * @param excludes the excludes to set
	 */
	public abstract void setExcludes(String[] excludes);

	/**
	 * @return a list of files to include in checking.
	 */
	public abstract String[] getIncludes();

	/**
	 * Sets a list of files to include in checking. Can contain ant-style wildcards and double
	 * wildcards. Defaults to **\/*.java.
	 * @param includes the includes to set
	 */
	public abstract void setIncludes(String[] includes);

	/**
	 * @return true if test sources must be considered by the plugin
	 */
	public abstract boolean isIncludeTests();

	/**
	 * Sets a value indicating whether test sources must be considered by the plugin
	 * @param includeTests a value indicating whether test sources must be considered by the plugin
	 */
	public abstract void setIncludeTests(boolean includeTests);

	/**
	 * The directory where the report for this plugin will be generated.
	 * @return the reportDirectoryName
	 */
	public abstract String getReportDirectoryName();

	/**
	 * Sets the directory where the report for this plugin will be generated.
	 * @param reportDirectoryName the reportDirectoryName to set
	 */
	public abstract void setReportDirectoryName(String reportDirectoryName);

	/**
	 * @return The parent directory for reports.
	 */
	public abstract File getReportParentDirectory();

	/**
	 * Sets the parent directory for reports.
	 * @param reportParentDirectory the reportParentDirectory to set
	 */
	public abstract void setReportParentDirectory(File reportParentDirectory);

	/**
	 * @return The directories containing the test-sources to be compiled.
	 */
	public abstract List getTestSourceRoots();

	/**
	 * Sets the directories containing the test-sources to be compiled.
	 * @param testSourceRoots The directories containing the test-sources to be compiled.
	 */
	public abstract void setTestSourceRoots(List testSourceRoots);

	/**
	 * Sets the project to analyse.
	 * @param project The project to analyse.
	 */
	public abstract void setProject(MavenProject project);

    /**
     * @return The project to analyse.
     */
    public abstract MavenProject getProject();
}
