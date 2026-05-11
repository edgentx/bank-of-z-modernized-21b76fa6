package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/S-FB-1.feature"}, // Assuming feature file exists or will be created
    plugin = {"pretty", "html:target/cucumber-report/S-FB-1.html"},
    glue = {"com.example.steps"},
    monochrome = true
)
public class VW454TestSuite {
    // Test Suite Class for VW-454
}