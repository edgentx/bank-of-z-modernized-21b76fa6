package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Runner for S-FB-1.
 * This is the entry point for the JVM test execution.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "../../features/S-FB-1.feature", // Path relative to class path target
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report/S-FB-1"},
    tags = "@S-FB-1" // Scoping to specific story
)
public class SFB1TestSuite {
    // Test class body is empty; Cucumber handles execution via annotations
}