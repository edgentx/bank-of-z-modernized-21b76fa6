package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
public class S22TestSuite {
    // This empty class is used by JUnit 5 to discover and run Cucumber tests
}
