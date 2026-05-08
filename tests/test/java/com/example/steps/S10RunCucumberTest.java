package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Placeholder to maintain existing structure.
 * Actual execution would use a standard Cucumber/JUnit runner configuration.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class S10RunCucumberTest {}
