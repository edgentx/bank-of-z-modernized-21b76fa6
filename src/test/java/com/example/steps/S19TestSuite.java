package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
    // This class acts as the JUnit 5 runner for the Cucumber feature
}
