package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasses(VW454Steps.class)
public class VW454RunCucumberTest {
    // This suite runs the VW-454 regression test
}
