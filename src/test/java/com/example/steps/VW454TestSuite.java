package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features/VW454.feature"},
    glue = {"com.example.steps"},
    plugin = {"pretty", "html:target/cucumber-report/VW454.html"}
)
public class VW454TestSuite {
    // Suite configuration
}
