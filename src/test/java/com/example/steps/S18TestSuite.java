package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-18 Features.
 * Connects JUnit 5 Platform to Cucumber.
 */
@Suite
@SelectClasspathResource("features/S-18.feature")
public class S18TestSuite {
}
