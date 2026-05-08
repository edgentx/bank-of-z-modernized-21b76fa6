package com.example.steps;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.CucumberJUnitPlatformRunnerOptions.GLUE;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-6.feature")
@ConfigurationParameter(key = GLUE, value = "com.example.steps")
public class S6TestSuite {
}
