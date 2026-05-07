package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for Story S-21.
 * Runs Cucumber tests located in features/S-21.feature
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-21.feature")
public class S21TestSuite {
}
