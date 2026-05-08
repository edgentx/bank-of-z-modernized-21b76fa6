package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Runner for S-FB-1 (Regression Test for VW-454).
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "../../features/S-FB-1.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/S-FB-1.html"},
    monochrome = true
)
public class SFB1TestSuite {
    // Test runner class only
}
