package com.example.steps;

import org.junit.platform.suite.api.IncludeClassnames;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-18 (TellerSession).
 * JUnit 5 Suite wrapper to execute Cucumber features.
 */
@Suite
@IncludeClassnames("com.example.steps.S18Steps")
@SelectClasspathResource("features/S-18.feature")
public class S18TestSuite {
}
