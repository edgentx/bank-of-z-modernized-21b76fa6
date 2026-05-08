package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for VW-454 Regression.
 * Located in e2e/regression/ equivalent location via the resource selector.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/VW-454.feature") // Assumes feature file is placed in resources/features
public class VW454TestSuite {
    // Suite configuration is handled by annotations
}
