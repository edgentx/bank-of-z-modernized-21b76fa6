package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite Runner for S-FB-1.
 * Placed in src/test/java to be picked up by Maven/Surefire.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/S-FB-1.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-reports/S-FB-1.html"}
)
public class SFB1TestSuite {
    // Test Suite entry point
}
