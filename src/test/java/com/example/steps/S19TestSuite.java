package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running S-19 Cucumber features.
 */
@Suite
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
}
