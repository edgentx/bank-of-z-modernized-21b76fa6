package com.example.steps;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features/S-18.feature")
public class S18TestSuite {
    // This empty class is used by JUnit 5 to run the Cucumber feature
}
