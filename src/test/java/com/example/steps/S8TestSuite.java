package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-8 Statement Feature.
 * Runs Cucumber tests located in features/S-8.feature.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-8.feature")
public class S8TestSuite {
}
