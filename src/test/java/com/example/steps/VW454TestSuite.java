package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite for VW-454 Regression Tests.
 * Connects the Step Definitions to the Cucumber Runner.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/S-FB-1.feature"}, // The feature file we need to create
    glue = {"com.example.steps"},
    plugin = {"pretty", "summary"}
)
public class VW454TestSuite {
    // This class serves as the entry point for the JUnit/Cucumber test runner
}
