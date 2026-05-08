package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite configuration for running VW-454 regression tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "../../features/VW-454.feature", 
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/VW-454.html"}
)
public class VW454TestSuite {
    // This class bootstraps the Cucumber runner
}
