package com.example.steps;

import com.example.e2e.regression.MockGitHubClient;
import com.example.e2e.regression.MockSlackNotifier;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test Configuration for Cucumber Test Suites.
 * Wires up the mock adapters for the regression tests.
 */
@TestConfiguration
public class CucumberTestSuite {

    @Bean
    public SlackPort slackPort() {
        return new MockSlackNotifier();
    }

    @Bean
    public GitHubPort gitHubPort() {
        return new MockGitHubClient();
    }
}
