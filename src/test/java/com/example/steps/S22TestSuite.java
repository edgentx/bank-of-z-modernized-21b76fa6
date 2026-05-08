package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
public class S22TestSuite {
    // JUnit 5 Suite to run Cucumber tests via JUnit Platform
}
