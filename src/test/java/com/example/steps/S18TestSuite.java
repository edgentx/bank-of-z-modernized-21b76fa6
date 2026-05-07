package com.example.steps;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-18 Feature.
 * Runs Cucumber scenarios defined in features/S-18.feature.
 */
@Suite
@IncludeTags("any") // You can filter by tags if needed
@SelectClasspathResource("features/S-18.feature")
public class S18TestSuite {
}
