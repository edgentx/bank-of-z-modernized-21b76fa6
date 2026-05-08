package com.example.steps;

import org.junit.platform.suite.api.Configuration;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Runner to execute the specific feature file for VW-454.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/VW-454.feature")
@Configuration(VW454TestSuite.class)
public class RunCucumberTest {
}
