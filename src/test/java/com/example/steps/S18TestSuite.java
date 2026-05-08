package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
public class S18TestSuite {
    // This class acts as the JUnit 5 suite entry point for Cucumber
}
