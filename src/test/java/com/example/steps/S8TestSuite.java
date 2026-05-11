package com.example.steps;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
@IncludeTags("S-8") // Assuming we might tag features, or simply relying on resource discovery
public class S8TestSuite {
}
