package com.example.steps;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for the Regression Test Suite.
 * Loads Mock Adapters for ports to ensure isolated testing.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = VW454TestSuite.TestConfig.class)
public class VW454TestSuite {

    @Configuration
    static class TestConfig {

        @Bean
        @Primary
        public SlackNotificationPort slackNotificationPort() {
            return new MockSlackNotificationPort();
        }

        @Bean
        @Primary
        public GitHubPort gitHubPort() {
            return new MockGitHubPort();
        }
    }
}
