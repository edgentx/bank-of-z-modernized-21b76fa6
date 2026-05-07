package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for running Cucumber features for S-20.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class S20TestSuite {
}
