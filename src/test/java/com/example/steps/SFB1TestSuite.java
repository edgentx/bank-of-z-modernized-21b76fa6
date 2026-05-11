package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"classpath:features/S-FB-1.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report/S-FB-1"},
    monochrome = true
)
public class SFB1TestSuite {
    // Test runner configuration for S-FB-1
}
