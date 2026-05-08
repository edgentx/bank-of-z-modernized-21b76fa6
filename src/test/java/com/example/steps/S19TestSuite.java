package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 Suite wrapper for running Cucumber tests for S-19.
 * Annotated to discover features in the classpath.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
    // This empty class is used as a hook for the JUnit Platform Suite Engine
}
