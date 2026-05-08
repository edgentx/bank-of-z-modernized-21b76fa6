package com.example.steps;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite Runner for S-19 Cucumber Scenarios.
 */
@Suite
@IncludeTags("S-19")
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
}
