package com.example.runners;

import org.junit.platform.suite.api.Configuration;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@Configuration(
    // Ensure strict cucumber matching, though usually defaults are fine
    plugin = {"pretty", "html:target/cucumber-report", "json:target/cucumber.json"}
)
public class CucumberTestSuite {
}
