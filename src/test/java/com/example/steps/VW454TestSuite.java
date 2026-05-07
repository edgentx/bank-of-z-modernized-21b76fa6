package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for VW-454 Regression Tests.
 */
@Suite
@IncludeEngines(Cucumber.class)
@SelectClasspathResource("features/S-FB-1.feature")
@ConfigurationParameter(key = "glue", value = "com.example.steps")
public class VW454TestSuite {
}
