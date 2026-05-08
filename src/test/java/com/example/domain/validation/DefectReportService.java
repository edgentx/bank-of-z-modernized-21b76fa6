package com.example.domain.validation;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Service class responsible for reporting defects.
 * This class acts as the "System Under Test" (SUT).
 * In a real TDD cycle, this file would be created empty or with stubs to fail compilation,
 * then filled in during the Green phase.
 * 
 * Included here to allow the test logic to be illustrative of the desired behavior.
 */
public class DefectReportService {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to Slack, including a link to the GitHub issue if available.
     *
     * @param issueId  The ID of the issue (e.g., VW-454).
     * @param channel  The Slack channel to notify.
     */
    public void reportDefect(String issueId, String channel) {
        String url = gitHubIssuePort.getIssueUrl(issueId)
                .orElse("URL not found");

        String messageBody = String.format(
            "Defect Report for %s.\nGitHub Issue: %s",
            issueId, url
        );

        slackNotificationPort.sendMessage(channel, messageBody);
    }
}