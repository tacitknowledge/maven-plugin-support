Maven Plugin Support
====================

Common classes for handling:

1. plugin aggregate report dashboard
2. threshold checks
3. xsl processing and html report formatting
4. GraphViz integration

Usage
-----

Sample usage for adding a plugin report to the plugin dashboard.  Evaluates thresholds on code 
coverage and adds its data to the report dashboard.

	try {
	    coverageError = parseOutputFileForErrors(new File(emmaReportsDirectory,"emma.txt"));
	    if (coverageError)
		getLog().error("Coverage did not meet:" + assembleMetrics());
	} catch (IOException e) {
	    throw new MojoExecutionException(e.getMessage(),e);
	}

	ReportSummary emmaSummary = new ReportSummary(new File(indexReportsDirectory,"index.xml"),
					       "Emma Code Coverage Report",
					       "emma/emma.html",
					       "emma",
					       true,
					       "Emma Code Coverage Report",
					       true,
					       ReportSummary.QUALITY);
	emmaSummary.setPassed(!coverageError);
	AggregateReportHandler handler = new AggregateReportHandlerImpl();
	handler.handleReport(emmaSummary);

Dependencies 
------------

- Ant - TODO: Replace with a new xslt approach that does not require ant
