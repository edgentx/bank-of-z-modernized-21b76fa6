package com.example.domain.validation.service;

import com.example.domain.validation.ports.DefectReporter;
import com.example.domain.validation.ports.GitHubClient;
import com.example.domain.validation.ports.SlackPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service implementation of DefectReporter.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
@Service
public class DefectReportService implements DefectReporter {

    private final GitHubClient gitHubClient;
    private final SlackPublisher slackPublisher;

    public DefectReportService(GitHubClient gitHubClient, SlackPublisher slackPublisher) {
        this.gitHubClient = gitHubClient;
        this.slackPublisher = slackPublisher;
    }

    @Override
    public void reportDefect(String title, String description) {
        // 1. Create the GitHub issue and retrieve the URL
        String issueUrl = gitHubClient.createIssue(title, description);

        // 2. Publish the notification to Slack with the URL in the body
        String messageBody = "Defect reported: " + issueUrl;
        slackPublisher.publishMessage("#vforce360-issues", Map.of("text", messageBody));
    }
}