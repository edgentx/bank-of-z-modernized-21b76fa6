package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite Runner for Cucumber S-19 features.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
    // This empty class is required for JUnit 5 to discover and run the Cucumber feature
}
