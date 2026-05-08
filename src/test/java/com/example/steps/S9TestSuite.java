package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for Cucumber Scenarios related to S-9.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-9.feature")
public class S9TestSuite {
}
