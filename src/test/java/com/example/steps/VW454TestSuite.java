package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite for VW-454 Regression.
 * Ensures that when a defect is reported, the resulting GitHub URL is propagated to Slack.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/VW-454.feature",
    glue = {"com.example.validation", "com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/VW-454.html"},
    monochrome = true
)
public class VW454TestSuite {
    // Suite entry point
}
