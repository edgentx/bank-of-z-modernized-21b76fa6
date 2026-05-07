package com.example.runners;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite to run S-10 BDD Scenarios.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class S10TestSuite {
}
