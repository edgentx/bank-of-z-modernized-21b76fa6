package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Runner for S-18 Features.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "features/S-18.feature",
        glue = "com.example.steps",
        plugin = {"pretty", "summary"}
)
public class S18TestSuite {
}
