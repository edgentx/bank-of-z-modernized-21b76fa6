package com.example.steps;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeTags("unit") // Or simply @Suite if running all
@SelectClasspathResource("features")
public class S19TestSuite {
    // JUnit 5 Suite configuration to run Cucumber via CucumberTestSuite if needed,
    // but typically Cucumber runs via the standard runner. 
    // In Spring Boot / Maven, Cucumber is usually invoked by the JUnit Platform Engine.
}
