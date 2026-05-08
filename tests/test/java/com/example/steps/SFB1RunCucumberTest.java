package com.example.steps;

import org.junit.platform.suite.api.Configuration;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test runner for S-FB-1 regression suite.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-FB-1.feature")
@Configuration(
    classes = {
        com.example.Application.class, // Main Spring Boot app config
        SFB1TestSuite.class            // Test-specific mocks config
    }
)
public class SFB1RunCucumberTest {
}
