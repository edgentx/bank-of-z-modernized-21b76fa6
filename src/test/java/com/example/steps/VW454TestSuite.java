package com.example.steps;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/S-FB-1.feature", 
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-reports/S-FB-1.html"}
)
public class VW454TestSuite {
    // This class serves as the test runner for the Cucumber feature file.
}
