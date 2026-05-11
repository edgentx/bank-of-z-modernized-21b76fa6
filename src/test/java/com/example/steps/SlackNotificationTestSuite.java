package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/S-FB-1.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "json:target/cucumber/S-FB-1.json"},
    monochrome = true
)
public class SlackNotificationTestSuite {
    // Test suite configuration
}