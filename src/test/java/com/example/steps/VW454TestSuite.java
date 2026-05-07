package com.example.steps;

import io.cucumber.core.options.Constants;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for VW-454 Regression.
 * This runs the specific feature file for this defect.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/VW454.feature")
@CucumberContextConfiguration
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
public class VW454TestSuite {
}
