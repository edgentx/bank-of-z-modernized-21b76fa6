package com.example.config;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuration specific to the test context.
 * Wires the Mock implementations to ensure tests are isolated and deterministic.
 */
@TestConfiguration
@Profile("test")
public class TestConfiguration {

    @Bean
    @Primary
    public SlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationPort();
    }
}