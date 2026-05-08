package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/VW454.feature", // We assume this feature file exists or will be added
    plugin = {"pretty", "html:target/cucumber-report/VW454.html"},
    glue = {"com.example.steps"},
    monochrome = true
)
public class VW454TestSuite {
    // This suite runs the specific scenario for VW-454
}
