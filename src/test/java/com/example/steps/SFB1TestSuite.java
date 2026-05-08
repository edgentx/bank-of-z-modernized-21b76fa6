package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines(Cucumber.class)
@SelectClasspathResource("features")
@ConfigurationParameter(key = "glue", value = "com.example.steps")
public class SFB1TestSuite {
    // Test Suite Configuration
}
