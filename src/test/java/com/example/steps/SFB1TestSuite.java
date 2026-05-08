package com.example.steps;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-FB-1.
 * Run this class to execute the Cucumber scenario.
 */
@Suite
@SelectClasses(SFB1Steps.class)
public class SFB1TestSuite {
    // JUnit 5 Suite wrapper
}
