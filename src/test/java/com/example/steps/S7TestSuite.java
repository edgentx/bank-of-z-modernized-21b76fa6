package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running S-7 Feature.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-7.feature")
public class S7TestSuite {
}
