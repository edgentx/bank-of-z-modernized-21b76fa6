package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Suite runner for S-FB-1 Regression.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/S-FB-1.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "summary"},
    monochrome = true
)
public class SFB1TestSuite {
    // Suite executes tests defined in SFB1Steps
}
