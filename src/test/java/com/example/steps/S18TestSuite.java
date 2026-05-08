package com.example.steps;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-18.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.steps")
public class S18TestSuite {
    // This class acts as the JUnit 5 entry point for Cucumber
}
