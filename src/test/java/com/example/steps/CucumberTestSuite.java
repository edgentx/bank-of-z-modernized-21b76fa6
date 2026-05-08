package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report"},
    monochrome = true
)
public class CucumberTestSuite {
    // Entrypoint for Cucumber tests
}
