package com.example.steps;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * Test Suite for S-18.
 * Executes the Gherkin features defined in S-18.feature.
 */
@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.steps")
public class S18TestSuite {
    // This empty class is used by JUnit 5 to run the Cucumber suite
}
