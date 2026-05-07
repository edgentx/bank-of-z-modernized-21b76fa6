package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"classpath:features/S-FB-1.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report"}
)
public class VW454TestSuite {
    // Test Suite Entry Point
}
