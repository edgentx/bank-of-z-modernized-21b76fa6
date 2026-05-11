package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-FB-1.
 * Located in src/test/java/com/example/steps to match existing E2E pattern.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-FB-1.feature")
@Cucumber
public class SFB1TestSuite {
}