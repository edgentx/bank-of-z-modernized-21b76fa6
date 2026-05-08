package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running S-24 Cucumber scenarios.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-24.feature")
public class S24TestSuite {
}
