package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasses({SFB1Steps.class})
public class SFB1TestSuite {
    // Cucumber JUnit 5 Suite configuration
}
