package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite for Story S-FB-1.
 * Connects the VW454Steps to the Cucumber runner.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "../../features/S-FB-1.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/S-FB-1"}
)
public class SFB1TestSuite {
}