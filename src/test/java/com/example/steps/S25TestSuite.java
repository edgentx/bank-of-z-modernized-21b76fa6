package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/S-25.feature",
    glue = "com.example.steps",
    plugin = {"pretty", "html:target/cucumber-report/S-25"}
)
public class S25TestSuite {
    // Test Suite class for Cucumber
}
