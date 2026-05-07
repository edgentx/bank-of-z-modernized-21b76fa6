package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite Runner for the VW-454 Regression Test.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/VW-454.feature", 
    plugin = {"pretty", "html:target/cucumber-report/VW-454.html"},
    glue = {"com.example.steps"}
)
public class VW454TestSuite {
    // Test runner class
}
