package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Master Cucumber Test Suite.
 * This configuration is standard for Spring Boot + JUnit 5 + Cucumber.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasses({S10Steps.class, S17Steps.class, SFB1Steps.class})
public class CucumberTestSuite {}
