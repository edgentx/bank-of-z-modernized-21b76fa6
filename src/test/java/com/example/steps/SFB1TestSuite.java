package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-FB-1.feature")
public class SFB1TestSuite {
    // This suite runs the Cucumber feature file for S-FB-1
}
