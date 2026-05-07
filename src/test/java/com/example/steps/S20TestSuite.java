package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-20 (TellerSession)
 * Wire this up in JUnit 5 to run the Cucumber features located in features/S-20.feature
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-20.feature")
public class S20TestSuite {
    // This class acts as the entry point for the JUnit Platform to run Cucumber tests
}
