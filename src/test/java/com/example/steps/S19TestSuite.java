package com.example.steps;

import org.junit.platform.suite.api.IncludeClassnamesRegexes;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeClassnamesRegexes(".*Steps")
@SelectClasspathResource("features")
public class S19TestSuite {
}
