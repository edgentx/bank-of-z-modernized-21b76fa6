package com.example.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;

/**
 * Test configuration for Cucumber tests related to S-FB-1.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = SFB1TestSuite.TestConfig.class)
public class SFB1TestSuite {

    @TestConfiguration
    static class TestConfig {
        
        @Bean
        public NotificationPort notificationPort() {
            return new MockNotificationPort();
        }

        @Bean
        public GitHubPort gitHubPort() {
            return new MockGitHubPort();
        }
    }
}
