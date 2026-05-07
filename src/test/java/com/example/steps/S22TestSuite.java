package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-22 Feature.
 * Runs the Cucumber scenarios defined in features/S-22.feature.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-22.feature")
public class S22TestSuite {
}
