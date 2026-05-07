package com.example.steps;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.CucumberJUnitPlatformExecutionMode.SCENARIO;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-16.feature")
@ConfigurationParameter(key = "cucumber.execution-mode", value = SCENARIO)
@ConfigurationParameter(key = "cucumber.filter.tags", value = "not @Skip")
public class S16TestSuite {
}
