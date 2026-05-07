package com.example;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite configuration for S-FB-1.
 * Configures Cucumber tests.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasses(com.example.steps.SFB1Steps.class)
public class SFB1TestSuite {
}
