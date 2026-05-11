package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite for S-FB-1.
 * Run this class to execute the regression test for the GitHub URL validation.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/S-FB-1.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/S-FB-1.html"},
    monochrome = true
)
public class SFB1TestSuite {
    // This class serves as the entry point for the JUnit runner to execute Cucumber features.
}
