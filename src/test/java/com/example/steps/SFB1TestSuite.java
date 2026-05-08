package com.example.steps;

import com.example.ports.VForce360Port;
import com.example.mocks.MockVForce360Adapter;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration for S-FB-1 specific dependencies.
 * Ensures the Mock Adapter is used instead of a real implementation.
 */
@TestConfiguration
@CucumberContextConfiguration
public class SFB1TestSuite {

    @Bean
    public VForce360Port vForce360Port() {
        return new MockVForce360Adapter();
    }
}
