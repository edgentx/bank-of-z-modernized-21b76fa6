package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for Story S-FB-1.
 * This configuration allows JUnit 5 to run the Cucumber features defined in resources.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-FB-1.feature")
@Cucumber
public class SFB1TestSuite {
    // Test execution is handled by the Cucumber engine
}
