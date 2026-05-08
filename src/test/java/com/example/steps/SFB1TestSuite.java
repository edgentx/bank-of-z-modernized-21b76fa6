package com.example.steps;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.CucumberJUnitExecutionParameters.GLUE_PROPERTY_NAME;

/**
 * Test Suite for S-FB-1.
 * Run this to execute the Red-phase tests.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.steps")
public class SFB1TestSuite {
    // This class acts as the entry point for the Cucumber Runner
}
