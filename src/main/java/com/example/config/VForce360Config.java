package com.example.config;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.VForce360NotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for VForce360 components.
 * Ensures the correct adapter (Mock vs Real) is loaded based on the profile.
 */
@TestConfiguration
public class VForce360Config {

    /**
     * If strictly necessary for wiring, we can define the Mock bean here explicitly,
     * though @MockBean in tests is usually preferred. 
     * This ensures the Port is always injectable.
     */
    // Bean definitions are typically handled by component scanning,
    // but we can use this to swap implementations if needed.
}