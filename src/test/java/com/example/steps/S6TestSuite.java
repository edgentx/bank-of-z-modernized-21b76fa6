package com.example.steps;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeTags("unit")
@SelectClasspathResource("features")
public class S6TestSuite {
    // JUnit 5 Suite to run Cucumber tests
}
