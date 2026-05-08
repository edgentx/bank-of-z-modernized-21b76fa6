package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 Suite configuration to run Cucumber features.
 * <p>
 * Uses JUnit Platform Suite to discover and run Cucumber scenarios located in
 * the classpath resource 'features'.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class S18TestSuite {
}
