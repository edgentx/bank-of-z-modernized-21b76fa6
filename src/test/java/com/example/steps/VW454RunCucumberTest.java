package com.example.steps;

import org.junit.platform.suite.api.Configuration;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/example/steps")
@Configuration(
    features = "features/VW454.feature",
    glue = "com.example.steps"
)
public class VW454RunCucumberTest {
    // JUnit 5 Suite wrapper to run Cucumber via Maven Surefire/JUnit Platform
}