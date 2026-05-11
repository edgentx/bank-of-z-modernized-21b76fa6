package com.example.domain.vforce;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Cucumber Test Suite for VForce360 Validation.
 * S-FB-1: Regression test covering the GitHub URL scenario.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/S-FB-1.feature", // We assume this feature file is created or updated
    glue = {"com.example.domain.vforce", "com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-html/S-FB-1"},
    tags = "@VW454"
)
public class VW454ValidationTestSuite {
    // Suite runs tests defined in VW454ValidationSteps
}
