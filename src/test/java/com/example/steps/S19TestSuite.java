package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
    // JUnit 5 Suite to run Cucumber tests via JUnit Platform
}
