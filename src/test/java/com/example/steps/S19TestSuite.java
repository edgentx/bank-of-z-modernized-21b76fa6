package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for Cucumber S-19 tests.
 * Annotated with JUnit 5 annotations for discovery.
 */
@Suite
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
}
