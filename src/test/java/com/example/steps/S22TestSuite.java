package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite to run Cucumber scenarios for Story S-22.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-22.feature")
public class S22TestSuite {
}
