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
        glue = "com.example.steps",
        plugin = {"pretty", "summary"}
)
public class VW454TestSuite {
}
