package com.example.config;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfiguration {

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationPort();
    }

    @Bean
    public GitHubIssuePort gitHubIssuePort() {
        return new MockGitHubIssuePort();
    }
}
