package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test Runner for the VW-454 Regression Suite.
 * Equivalent to S10RunCucumberTest.java in existing structure.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/VW454.feature",
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber/VW454"}
)
public class VW454RunCucumberTest {
}
