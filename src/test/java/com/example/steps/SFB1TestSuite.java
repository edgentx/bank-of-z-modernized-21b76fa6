package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/S-FB-1.feature"},
    plugin = {"pretty", "html:target/cucumber/S-FB-1.html"},
    glue = {"com.example.steps"}
)
public class SFB1TestSuite {
    // Test Suite runner for S-FB-1
}