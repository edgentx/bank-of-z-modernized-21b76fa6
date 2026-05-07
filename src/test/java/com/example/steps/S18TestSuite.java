package com.example.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = com.example.Application.class)
@ActiveProfiles("test")
public class S18TestSuite {
    // Bootstrap configuration for Cucumber tests
}
