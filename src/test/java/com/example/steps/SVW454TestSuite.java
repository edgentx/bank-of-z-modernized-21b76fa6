package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite for VW-454 Regression.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/S-VW-454.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/S-VW-454.html"},
    monochrome = true
)
public class SVW454TestSuite {
}