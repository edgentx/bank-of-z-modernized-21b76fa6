package com.example;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running Cucumber scenarios.
 * Located in tests/ as per DDD+Hex layout requirements.
 */
@Suite
@IncludeTags("any")
@SelectClasspathResource("features")
public class CucumberTestSuite {
}
