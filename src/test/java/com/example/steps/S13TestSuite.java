package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-13 Cucumber Tests.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-13.feature")
public class S13TestSuite {
}
