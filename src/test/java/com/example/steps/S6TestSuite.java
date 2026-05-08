package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running Cucumber tests for S-6.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-6.feature")
public class S6TestSuite {
}
