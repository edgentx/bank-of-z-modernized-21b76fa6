package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for Feature S-FB-1.
 * Runs the specific steps defined in SFB1ValidationSteps.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasses(SFB1ValidationSteps.class)
public class SFB1TestSuite {
}
