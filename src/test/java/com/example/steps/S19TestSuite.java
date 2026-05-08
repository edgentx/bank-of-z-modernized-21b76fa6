package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 Suite configuration to run Cucumber tests for S-19.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
}
