package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 Suite to run Cucumber tests for S-21.
 */
@Suite
@SelectClasspathResource("features/S-21.feature")
public class S21TestSuite {
}
