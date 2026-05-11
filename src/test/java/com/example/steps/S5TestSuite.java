package com.example.steps;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.CucumberJUnitPlatformRunnerOptions.GLUE_PROPERTY_NAME;

@Suite
@SelectClasspathResource("features")
@SelectClasspathResource("features/S-5.feature") // Explicitly run S-5
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.steps")
public class S5TestSuite {
}
