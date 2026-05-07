package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite to run VW-454 Regression scenarios.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasses(VW454TestSuite.class)
public class VW454RunCucumberTest {
}
