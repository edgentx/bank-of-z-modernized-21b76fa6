package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines(Cucumber.class)
@SelectClasspathResource("features")
// Additional configuration to ensure strict run
@ConfigurationParameter(key = "cucumber.filter.tags", value = "@S-FB-1")
public class SFB1TestSuite {
}