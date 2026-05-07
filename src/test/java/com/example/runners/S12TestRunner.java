package com.example.runners;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-12.feature")
public class S12TestRunner {
    // This class acts as the JUnit 5 Suite runner for Cucumber
}
