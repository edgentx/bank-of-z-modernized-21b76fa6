package com.example.steps;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

@Test
@CucumberOptions(features = "src/test/resources/features", glue = "com.example.steps")
public class SFb1TestSuite extends AbstractTestNGCucumberTests {
}
