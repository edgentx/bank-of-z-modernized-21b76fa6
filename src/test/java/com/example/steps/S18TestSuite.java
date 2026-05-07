package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite configuration for Cucumber tests related to Story S-18.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "../../../../../features/S-18.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report/S-18.html"}
)
public class S18TestSuite {
    // Suite class.
}
