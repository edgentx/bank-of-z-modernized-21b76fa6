package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-9.feature")
public class S9TestSuite {
    // This class acts as the JUnit 5 Suite runner for the Cucumber Feature file
}
