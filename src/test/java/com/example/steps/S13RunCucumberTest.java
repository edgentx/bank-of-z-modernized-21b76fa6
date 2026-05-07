package com.example.steps;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * Test Runner for S-13 Cucumber Scenarios.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-13.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.steps")
public class S13RunCucumberTest {
}
