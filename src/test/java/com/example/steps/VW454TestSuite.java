package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "features/VW-454.feature",
    glue = "com.example.steps",
    plugin = {"pretty", "html:target/cucumber-report/VW-454"}
)
public class VW454TestSuite {}
