package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-FB-1.
 * Groups the specific step definitions for the regression test.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasses(SFB1Steps.class)
public class SFB1TestSuite {
}
