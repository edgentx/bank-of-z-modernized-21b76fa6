package com.example.steps;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * JUnit 5 Suite wrapper for Cucumber S-18 tests.
 * Allows `mvn test` to pick up the feature file and steps.
 */
@Suite
@SelectClasspathResource("features/S-18.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.steps")
public class S18TestSuite {
}