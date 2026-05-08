package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/S-FB-1.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report/S-FB-1.html"}
)
public class SFB1TestSuite {
    // This class runs the feature file for the FB-1 Defect
}