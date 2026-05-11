package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite for VW-454 Regression.
 * Connects the Step Definitions to the Feature File.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/VW-454.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report/VW-454.html"},
    monochrome = true
)
public class VW454TestSuite {
}
