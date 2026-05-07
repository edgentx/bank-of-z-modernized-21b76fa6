package com.example.steps;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.CucumberJUnitPlatformRunnerOptions.GLUE_ENABLED;

@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_ENABLED, value = "true")
public class S18TestSuite {
}
