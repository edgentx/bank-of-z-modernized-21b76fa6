package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation for the Defect Reporting Activity.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
@Component
public class DefectReportingActivityImpl implements DefectReportingActivities {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingActivityImpl.class);

    private final GitHubPort gitHubPort;
    private final SlackNotifierPort slackNotifierPort;

    public DefectReportingActivityImpl(GitHubPort gitHubPort, SlackNotifierPort slackNotifierPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotifierPort = slackNotifierPort;
    }

    @Override
    public String execute(String title, String description) {
        log.info("Executing defect report for title: {}", title);

        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(title, description);

        // 2. Notify Slack with the URL embedded in the body
        // Expected behavior: Slack body includes GitHub issue: <url>
        String message = "Defect Reported: " + title + "\nGitHub Issue: " + issueUrl;
        slackNotifierPort.notify(message);

        return issueUrl;
    }
}