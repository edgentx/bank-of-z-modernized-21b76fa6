package com.example.steps;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    VW454ValidationTest.class,
    S10Steps.class,
    S17Steps.class
})
public class CucumberTestSuite {
    // Standard JUnit Suite to run all verification tests
}