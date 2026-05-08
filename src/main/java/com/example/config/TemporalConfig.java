package com.example.config;

import com.example.domain.validation.port.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TemporalConfig {

    @Bean
    @Primary
    public SlackNotificationPort mockSlackNotificationPort() {
        return new MockSlackNotificationPort();
    }
}
