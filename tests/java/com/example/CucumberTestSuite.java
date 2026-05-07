package com.example;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@SuiteDisplayName("Cucumber Test Suite")
public class CucumberTestSuite {
}
