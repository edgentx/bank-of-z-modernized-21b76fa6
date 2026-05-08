package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Runner for VW-454 Regression.
 * Place this file in src/test/java to be picked up by Maven/IntelliJ.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/VW-454.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report/VW454"}
)
public class VW454TestSuite {
}
