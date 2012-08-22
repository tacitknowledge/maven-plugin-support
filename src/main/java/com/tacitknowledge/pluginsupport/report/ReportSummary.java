package com.tacitknowledge.pluginsupport.report;


import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Sep 19, 2006
 * Time: 3:53:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportSummary {

    public static final String CORRECTNESS = "correctness";
    public static final String QUALITY = "quality";
    public static final String INFO = "info";
    public static final String TBD = "tbd";
    private String reportFile;
    private String reportAlias = "null";
    private String key;
    private boolean passed = true;
    private String description;
    private boolean failBuild = true;
    private File aggregate;
    private String type = INFO;
    private String reportFileOverride;     /* Used in special cases ( ie Emma reporting) where we want to apecify a different file to look for results.
                                           ** Normally unused ( if set to null). Must be explicitly set.
                                           */

    private String moduleName;

    public ReportSummary() {

    }

    public ReportSummary(String moduleName,File aggregate,String alias, String reportFile, String key, boolean passed, String description, boolean failBuild, String type) {
        this.aggregate = aggregate;
        this.reportFile = reportFile;
        this.reportAlias = alias;
        this.key = key;
        this.passed = passed;
        this.description = description;
        this.failBuild = failBuild;
        this.type = type;
        this.reportFileOverride = null;
        this.moduleName = moduleName;
    }

    public ReportSummary(File aggregate,String alias, String reportFile, String key, boolean passed, String description, boolean failBuild, String type) {

     this("unknown",aggregate, alias,reportFile,key,passed,description,failBuild,type);
    }

    public String getReportFileOverride()
    {
        return reportFileOverride;
    }
    public void setReportFileOverride( String s)
    {
        reportFileOverride = s;
    }


    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFailBuild() {
        return failBuild;
    }

    public void setFailBuild(boolean failBuild) {
        this.failBuild = failBuild;
    }

    public String getReportAlias() {
        return reportAlias;
    }

    public void setReportAlias(String reportAlias) {
        this.reportAlias = reportAlias;
    }

    public File getAggregate() {
        return aggregate;
    }

    public void setAggregate(File aggregate) {
        this.aggregate = aggregate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }
}
