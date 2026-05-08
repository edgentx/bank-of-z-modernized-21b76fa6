package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite for S-FB-1.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "features/S-FB-1.feature",
        glue = {"com.example.steps"},
        plugin = {"pretty", "html:target/cucumber-report/S-FB-1.html"}
)
public class SFB1TestSuite {
    // Suite runs the feature file
}