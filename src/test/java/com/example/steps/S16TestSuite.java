package com.example.steps;

import org.junit.platform.suite.api.IncludeClassnames;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeClassnames("com.example.steps.S16Steps")
@SelectClasspathResource("features/S-16.feature")
public class S16TestSuite {
}
