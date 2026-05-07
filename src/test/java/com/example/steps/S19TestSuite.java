package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for Cucumber S-19 Features.
 * Runs scenarios defined in features/S-19.feature.
 */
@Suite
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
}
