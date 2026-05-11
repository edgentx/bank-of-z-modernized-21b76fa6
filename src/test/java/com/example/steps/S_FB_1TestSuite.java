package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite for Story S-FB-1.
 * Placed in src/test/java/com/example/steps/ to adhere to existing repo structure.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/S-FB-1.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/S-FB-1.html"},
    monochrome = true
)
public class S_FB_1TestSuite {
    // Suite class runs the Gherkin feature file
}
