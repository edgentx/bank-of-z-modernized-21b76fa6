package com.example.services;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DefectReportingService {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and then notifying Slack.
     * This satisfies the validation requirement S-FB-1.
     *
     * @param title The defect title.
     * @param body  The defect details.
     */
    public void reportDefect(String title, String body) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(title, body);

        // 2. Notify Slack with the URL included in the body
        // This aligns with the test expectation `contains(expectedGitHubUrl)`
        String slackMessage = "New defect reported: " + title + "\n" + issueUrl;
        slackNotificationPort.sendMessage("#vforce360-issues", slackMessage, Set.of());
    }
}