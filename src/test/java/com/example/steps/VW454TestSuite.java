package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/VW454.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/VW454.html"},
    monochrome = true
)
public class VW454TestSuite {
    // Test Suite Entry Point
}
