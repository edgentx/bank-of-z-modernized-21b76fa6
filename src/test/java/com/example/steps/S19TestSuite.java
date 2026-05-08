package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-19 Feature.
 * Run with Maven: mvn test -Dtest=S19TestSuite
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
}