package com.example.steps;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasses(VW454Steps.class)

/**
 * Test Suite configuration for running VW-454 validation.
 * This executes the Cucumber tests defined in the steps.
 */
public class VW454TestSuite {
    // JUnit 5 Suite wrapper
}