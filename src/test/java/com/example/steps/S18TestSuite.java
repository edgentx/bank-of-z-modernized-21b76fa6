package com.example.steps;

import org.junit.platform.suite.api.IncludeClassNames;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-18 Cucumber Scenarios.
 */
@Suite
@IncludeClassNames("com.example.steps.S18Steps")
@SelectClasspathResource("features/S-18.feature")
public class S18TestSuite {
}
