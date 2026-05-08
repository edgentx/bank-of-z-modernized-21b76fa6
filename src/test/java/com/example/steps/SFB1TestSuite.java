package com.example.steps;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class SFB1TestSuite {
    // Configuration is typically handled in junit-platform.properties or annotations here
}
