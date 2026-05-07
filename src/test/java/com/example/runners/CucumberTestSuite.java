package com.example.runners;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
public class CucumberTestSuite {
    // This class acts as the entry point for the Cucumber runner via JUnit 5
}
