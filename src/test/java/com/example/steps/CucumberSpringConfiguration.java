package com.example.steps;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Spring Test Configuration to provide Mock beans to the Cucumber Context.
 */
@TestConfiguration
public class CucumberSpringConfiguration {

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationPort();
    }
}
