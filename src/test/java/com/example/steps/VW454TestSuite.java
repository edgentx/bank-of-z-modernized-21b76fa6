package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite for S-FB-1: Validating VW-454
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/S-FB-1.feature",
    glue = "com.example.steps",
    plugin = {"pretty", "html:target/cucumber-report/S-FB-1"}
)
public class VW454TestSuite {
    // Test runner configuration
}
