package com.example.steps;

import org.junit.platform.suite.api.Configuration;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@Configuration(
    // If using strict glue, uncomment and ensure package matches
    // glue = "com.example.steps"
)
public class CucumberTestSuite {
    // This class acts as the JUnit 5 entry point for Cucumber
}