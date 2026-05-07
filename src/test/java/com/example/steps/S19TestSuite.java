package com.example.steps;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeTags("unit")
@SelectClasspathResource("features/S-19.feature")
public class S19TestSuite {
}
