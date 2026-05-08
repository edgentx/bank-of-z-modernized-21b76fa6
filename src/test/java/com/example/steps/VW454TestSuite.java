package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasses(VW454ValidationSteps.class)
public class VW454TestSuite {
    // Test Suite runner for Cucumber/JUnit 5
}
