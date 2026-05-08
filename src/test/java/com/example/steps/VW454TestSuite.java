package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"features/VW-454.feature"},
        glue = {"com.example.steps"},
        plugin = {"pretty", "summary"}
)
public class VW454TestSuite {
    // Test Suite runner for VW-454
}
