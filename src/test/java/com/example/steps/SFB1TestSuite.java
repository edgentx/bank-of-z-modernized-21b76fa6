package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-FB-1.
 * Runs the specific feature file for GitHub URL validation.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/example/steps/S-FB-1.feature")
public class SFB1TestSuite {
}