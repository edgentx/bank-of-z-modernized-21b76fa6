package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running S-18 Feature files.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-18.feature")
public class S18TestSuite {
}
