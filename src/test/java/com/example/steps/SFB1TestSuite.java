package com.example.steps;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@IncludeEngines("cucumber")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.steps")
@SuiteDisplayName("S-FB-1: GitHub URL Validation Suite")
public class SFB1TestSuite {
    // This class acts as the test runner for the feature file
}
