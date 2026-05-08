package com.example.steps;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    S10TestSuite.class,
    S17TestSuite.class,
    VW454TestSuite.class // Added regression suite
})
public class CucumberTestSuite {
    // Master Suite
}
