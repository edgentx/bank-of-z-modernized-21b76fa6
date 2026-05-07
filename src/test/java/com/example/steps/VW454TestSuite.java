package com.example.steps;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite to run the Cucumber test for S-FB-1.
 */
@Suite
@IncludeClassNamePatterns(".*Test")
@SelectClasses(VW454Steps.class)
public class VW454TestSuite {
    // JUnit 5 Suite configuration
}
