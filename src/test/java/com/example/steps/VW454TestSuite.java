package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for VW-454 Regression.
 * Groups the Cucumber steps for execution.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasses(VW454Steps.class)
public class VW454TestSuite {
    // Suite class to run the tests
}