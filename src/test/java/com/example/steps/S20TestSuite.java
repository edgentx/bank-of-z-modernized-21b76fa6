package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-20 BDD Scenarios.
 * Run with: mvn test -Dtest=S20TestSuite
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-20.feature")
public class S20TestSuite {
}
