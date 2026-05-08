package com.example.steps;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Spring Test Configuration for VW-454 Regression Tests.
 * This wires up the Mock Adapters to the Ports.
 */
@TestConfiguration
@CucumberContextConfiguration
public class VW454TestSuite {

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationPort();
    }

    @Bean
    public GitHubPort gitHubPort() {
        return new MockGitHubPort();
    }
}
