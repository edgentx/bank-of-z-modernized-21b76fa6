package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running S-22 Cucumber features.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-22.feature")
public class S22TestSuite {
}
