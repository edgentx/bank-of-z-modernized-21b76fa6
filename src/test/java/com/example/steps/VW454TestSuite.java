package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"../../features/VW-454.feature"}, // Pointing to the feature file
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report/VW454"}
)
public class VW454TestSuite {
    // Test runner configuration
}
