package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * JUnit 5 wrapper to run Cucumber features for VW-454.
 */
@Suite
@IncludeEngines("cucumber")
@SuiteDisplayName("VW-454 Regression Tests")
public class VW454RunCucumberTest {
    // This class triggers the Cucumber runner via JUnit 5 Platform Suite
}