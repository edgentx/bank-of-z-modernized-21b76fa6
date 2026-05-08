package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit Platform wrapper for Cucumber tests specific to S-FB-1.
 * Enables `mvn test` or `gradle test` to pick up the Gherkin feature.
 */
@Suite
@IncludeEngines("cucumber")
@ConfigurationParameter(key = Cucumber.GLUE_PROPERTY_NAME, value = "com.example.steps")
@ConfigurationParameter(key = Cucumber.FEATURE_PROPERTY_NAME, value = "features/S-FB-1.feature")
public class SFB1RunCucumberTest {
}
