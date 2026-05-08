package com.example.mocks;

import com.example.ports.VForce360NotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration to provide Mock implementations for Ports.
 */
@TestConfiguration
public class MockVForce360NotificationPortConfiguration {

    @Bean
    public VForce360NotificationPort vForce360NotificationPort() {
        return new MockVForce360NotificationPort();
    }
}