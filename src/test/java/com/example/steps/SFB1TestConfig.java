package com.example.steps;

import com.example.ports.GithubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SFB1TestConfig {

    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(GithubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        return new ReportDefectWorkflow(githubIssuePort, slackNotificationPort);
    }
}