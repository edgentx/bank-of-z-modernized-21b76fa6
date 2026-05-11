package com.example.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"../../features/S-18.feature"}, glue = {"com.example.steps"})
public class S18TestSuite {
    // Suite for S-18: StartSessionCmd
}