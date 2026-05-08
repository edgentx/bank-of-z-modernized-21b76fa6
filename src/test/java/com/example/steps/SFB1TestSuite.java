package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Runner for S-FB-1.
 * Connects the Step Definitions to the Feature File.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/S-FB-1.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/S-FB-1.html"}
)
public class SFB1TestSuite {
    // Test execution entry point
}
