package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * Mock configuration for SlackNotificationPort.
 * This allows us to use Mockito to spy/sty interactions without real network calls.
 */
@TestConfiguration
public class MockSlackNotificationPort {

    @Bean
    @Primary
    public SlackNotificationPort slackNotificationPort() {
        return mock(SlackNotificationPort.class);
    }
}
