package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running S-18 Gherkin features.
 * Executes via JUnit Platform Suite engine.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-18.feature")
public class S18TestSuite {
}
