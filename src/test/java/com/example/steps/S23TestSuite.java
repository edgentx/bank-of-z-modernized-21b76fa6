package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite configuration for Cucumber tests related to S-23.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features/S-23.feature", // Assumes feature file is in resources
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report/S-23"}
)
public class S23TestSuite {
    // Suite class acts as the entry point for the Cucumber Runner
}
