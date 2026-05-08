package com.example.steps;

import com.example.mocks.MockIssueTrackerPort;
import com.example.mocks.MockNotificationPort;
import com.example.ports.IssueTrackerPort;
import com.example.ports.NotificationPort;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Test configuration for S-FB-1.
 * This wires up the Mock implementations to the Port interfaces,
 * allowing Spring/Cucumber to inject them into the Step Definitions.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class SFB1TestSuite {

    @Bean
    public NotificationPort notificationPort() {
        return new MockNotificationPort();
    }

    @Bean
    public IssueTrackerPort issueTrackerPort() {
        return new MockIssueTrackerPort();
    }
}
