package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/VW454.feature") // Assuming feature file location or handled by CucumberTestSuite
/**
 * Test Suite configuration for VW-454 Regression.
 * This class wires up the Cucumber runner to the Step definitions.
 */
public class VW454TestSuite {
    // Configuration can be added here if needed for specific Spring Context test slicing
}
