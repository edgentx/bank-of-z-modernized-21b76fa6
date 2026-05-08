package com.example.steps;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Test Suite for VW-454 Regression.
 * Verifies that defect reporting workflows correctly append the GitHub issue link to Slack notifications.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/java/com/example/features/vw-454.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/vw-454"},
    monochrome = true
)
public class VW454TestSuite {
    // Execution entry point for the test runner
}