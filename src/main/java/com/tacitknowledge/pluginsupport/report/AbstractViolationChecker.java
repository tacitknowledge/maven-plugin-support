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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Base class for mojos that check if there were any violations in an XML file.
 * 
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public abstract class AbstractViolationChecker extends AbstractMojo
{
    private static final Boolean FAILURES_KEY = Boolean.TRUE;

    private static final Boolean WARNINGS_KEY = Boolean.FALSE;

    protected void executeCheck(String filename, String tagName, String key, int failurePriority)
        throws MojoFailureException, MojoExecutionException
    {
        if (!getProject().isExecutionRoot())
        {
            return;
        }
        
        if ("java".equals(getLanguage()))
        {
            File outputFile = new File(getReportDirectory(), filename);
            if (outputFile.exists())
            {
                try
                {
                    XmlPullParser xpp = new MXParser();
                    FileReader freader = new FileReader(outputFile);
                    BufferedReader breader = new BufferedReader(freader);
                    xpp.setInput(breader);

                    Map violations = getViolations(xpp, tagName, failurePriority);

                    List failures = (List) violations.get(FAILURES_KEY);
                    List warnings = (List) violations.get(WARNINGS_KEY);

                    int failureCount = failures.size();
                    int warningCount = warnings.size();

                    String message = getMessage(failureCount, warningCount, key, outputFile);

                    if (failureCount > 0 && isFailOnViolation())
                    {
                        throw new MojoFailureException(message);
                    }

                    getLog().info(message);
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException("Unable to read PMD results xml: " 
                    		+ outputFile.getAbsolutePath(), e);
                }
                catch (XmlPullParserException e)
                {
                    throw new MojoExecutionException("Unable to read PMD results xml: " 
                    		+ outputFile.getAbsolutePath(), e);
                }
            }
            else
            {
                throw new MojoFailureException("Unable to perform check, " + "unable to find " 
                		+ outputFile);
            }
        }
    }

    /**
     * Method for collecting the violations found by the PMD tool
     * 
     * @param xpp the xml parser object
     * @param tagName the element that will be checked
     * @return an int that specifies the number of violations found
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Map getViolations(XmlPullParser xpp, String tagName, int failurePriority)
        throws XmlPullParserException, IOException
    {
        int eventType = xpp.getEventType();

        List<Map> failures = new ArrayList<Map>();
        List<Map> warnings = new ArrayList<Map>();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if (eventType == XmlPullParser.START_TAG && tagName.equals(xpp.getName()))
            {
                Map details = getErrorDetails(xpp);
                try
                {
                    int priority = Integer.parseInt((String) details.get("priority"));
                    if (priority <= failurePriority)
                    {
                        failures.add(details);
                    }
                    else
                    {
                        warnings.add(details);
                    }
                }
                catch (NumberFormatException e)
                {
                    // i don't know what priority this is. Treat it like a failure
                    failures.add(details);
                }
                catch (NullPointerException e)
                {
                    // i don't know what priority this is. Treat it like a failure
                    failures.add(details);
                }
            }

            eventType = xpp.next();
        }

        HashMap<Boolean, List<Map>> map = new HashMap<Boolean, List<Map>>(2);
        map.put(FAILURES_KEY, failures);
        map.put(WARNINGS_KEY, warnings);
        
        return map;
    }

    /**
     * Gets the output message
     * 
     * @param failureCount
     * @param warningCount
     * @param key
     * @param outputFile
     * @return
     */
    private String getMessage(int failureCount, int warningCount, String key, File outputFile)
    {
        StringBuffer message = new StringBuffer();
        
        if (failureCount > 0 || warningCount > 0)
        {
            if (failureCount > 0)
            {
                message.append("You have " + failureCount + " " + key 
                		+ (failureCount > 1 ? "s" : ""));
            }

            if (warningCount > 0)
            {
                if (failureCount > 0)
                {
                    message.append(" and ");
                }
                else
                {
                    message.append("You have ");
                }
                message.append(warningCount + " warning" + (warningCount > 1 ? "s" : "" ));
            }

            message.append(". For more details see:" + outputFile.getAbsolutePath());
        }
        
        return message.toString();
    }

    /**
     * Gets the attributes and text for the violation tag and puts them in a
     * HashMap
     * 
     * @param xpp
     * @throws XmlPullParserException
     * @throws IOException
     */
    protected abstract Map getErrorDetails(XmlPullParser xpp)
        throws XmlPullParserException, IOException;

	/**
	 * @return the failOnViolation
	 */
	public abstract boolean isFailOnViolation();

	/**
	 * @param failOnViolation the failOnViolation to set
	 */
	public abstract void setFailOnViolation(boolean failOnViolation);

	/**
	 * @return the language
	 */
	public abstract String getLanguage();

	/**
	 * @param language the language to set
	 */
	public abstract void setLanguage(String language);

	/**
	 * @return the project
	 */
	public abstract MavenProject getProject();

	/**
	 * @param project the project to set
	 */
	public abstract void setProject(MavenProject project);

	/**
	 * @return the reportDirectory
	 */
	public abstract File getReportDirectory();

	/**
	 * @param reportDirectory the reportDirectory to set
	 */
	public abstract void setReportDirectory(File reportDirectory);
}