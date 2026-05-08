package com.example.steps;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import com.example.mocks.MockVForce360NotificationPortConfiguration;

/**
 * Base configuration for Cucumber tests.
 * Ensures Spring context is loaded with Mocks.
 */
@SpringBootTest
@Import(MockVForce360NotificationPortConfiguration.class)
public class CucumberSpringConfiguration {
    // This empty class is used to load the Spring Context for Cucumber
}