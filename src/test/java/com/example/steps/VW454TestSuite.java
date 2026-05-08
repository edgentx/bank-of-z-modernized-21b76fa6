package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite configuration for VW-454 Regression Tests.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/VW-454.feature")
@Cucumber
public class VW454TestSuite {
    // Suite configuration
}
