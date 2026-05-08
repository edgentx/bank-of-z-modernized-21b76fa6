package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for VW-454 Regression.
 * Executes the Cucumber scenarios defined in VW454Steps.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasses(VW454Steps.class)
public class VW454TestSuite {
}
