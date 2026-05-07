package com.example.steps;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for Story S-FB-1.
 * Verifies that the GitHub URL is present in the Slack notification body.
 */
@Suite
@SelectClasses(SFB1Steps.class)
public class SFB1TestSuite {
    // JUnit 5 Suite configuration
}
