package com.example.steps;

import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;

/**
 * Standard Spring Boot Test configuration for Cucumber.
 * Ensures the application context is loaded for dependency injection.
 */
@CucumberContextConfiguration
@SpringBootTest
public class CucumberSpringConfiguration {
    // Configuration class to bootstrap Spring context for Cucumber tests
}
