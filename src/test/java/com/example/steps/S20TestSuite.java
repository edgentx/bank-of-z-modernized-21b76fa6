package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running S-20 Feature.
 * Requires JUnit Platform Suite Engine.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-20.feature")
public class S20TestSuite {
}
