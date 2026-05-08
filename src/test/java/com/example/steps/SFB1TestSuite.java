package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite configuration for Cucumber tests related to Feature S-FB-1.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasses(SFB1Steps.class)
public class SFB1TestSuite {
    // This class acts as the glue for the JUnit 5 Platform to find the Cucumber tests
}
