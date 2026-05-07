package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-FB-1 Regression Tests.
 */
@Suite
@IncludeEngines(Cucumber.class)
@SelectClasspathResource("features/S-FB-1.feature")

public class SFB1TestSuite {
    // Suite configuration is handled by annotations
}
