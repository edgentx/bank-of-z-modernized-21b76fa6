package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for Cucumber Scenarios related to S-19 (TellerSession Navigation).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
}
