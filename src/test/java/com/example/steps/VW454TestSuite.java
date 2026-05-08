package com.example.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test Suite configuration for running VW-454 Glue code.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = VW454TestConfig.class)
public class VW454TestSuite {
    // Configuration class handles the mock beans
}
