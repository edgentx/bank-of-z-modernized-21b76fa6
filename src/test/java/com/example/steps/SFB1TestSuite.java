package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-FB-1.
 * Corresponds to S10TestSuite/S17TestSuite in existing structure.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-FB-1.feature")
public class SFB1TestSuite {
}
