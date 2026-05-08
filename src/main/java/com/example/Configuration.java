package com.example;

import com.example.application.DefectWorkflowService;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test Configuration to wire the Mock implementation for the Port.
 * In production, this would be replaced by a real adapter bean configuration.
 */
@TestConfiguration
public class Configuration {

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationPort();
    }

    @Bean
    public DefectWorkflowService defectWorkflowService(SlackNotificationPort slackNotificationPort) {
        return new DefectWorkflowService(slackNotificationPort);
    }
}
