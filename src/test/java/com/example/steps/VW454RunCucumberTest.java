package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"classpath:features/VW-454.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "summary"}
)
public class VW454RunCucumberTest {
    // Test runner configuration
}
