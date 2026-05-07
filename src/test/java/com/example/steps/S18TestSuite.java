package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-18 (TellerSession).
 * Runs the Cucumber scenarios defined in features/S-18.feature.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-18.feature")
public class S18TestSuite {
}
