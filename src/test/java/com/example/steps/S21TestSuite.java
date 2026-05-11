package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 Suite runner for Cucumber S-21 tests.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-21.feature")
public class S21TestSuite {
}
