package com.example.config;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.VForce360NotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for VForce360 notifications.
 * Instantiates the real adapter if dependencies are present, otherwise allows mock.
 */
@Configuration
public class VForce360Config {

    @Bean
    @ConditionalOnMissingBean(SlackNotificationAdapter.class)
    public VForce360NotificationPort vForce360NotificationPort() {
        // If the real adapter is not enabled via @ConditionalOnProperty,
        // this ensures the interface is still wired if no other bean exists,
        // though typically tests would provide the Mock via @TestConfiguration.
        // In a production-only scenario, this might throw an exception.
        return new VForce360NotificationPort() {
            @Override
            public void publishDefect(String title, String description, String githubUrl) {
                throw new UnsupportedOperationException("No VForce360 adapter configured");
            }
        };
    }
}
