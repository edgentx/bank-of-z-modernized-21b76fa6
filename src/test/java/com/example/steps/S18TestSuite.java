package com.example.steps;

import org.junit.platform.suite.api.IncludeClassPatterns;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeClassPatterns("^com\.example\.steps\..*$")
@SelectClasspathResource("features")
public class S18TestSuite {
    // JUnit 5 Suite configuration to run Cucumber tests via JUnit Platform
}
