package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 Suite wrapper to run Cucumber features.
 * This is detected by the Maven Surefire plugin during the test phase.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class S18TestSuite {
    // No code needed here, annotations drive the execution
}