package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite Runner for S-8 Cucumber tests.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-8.feature")
public class S8TestSuite {
}
