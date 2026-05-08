package com.example.domain.defect;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service orchestrating the reporting of defects.
 * This represents the "Green" phase implementation satisfying VW-454.
 */
@Service
public class DefectReportService {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect report workflow.
     * 1. Creates an issue on GitHub.
     * 2. Posts a notification to Slack containing the GitHub URL.
     *
     * @param title       The defect title.
     * @param description The defect description.
     * @param channel     The target Slack channel.
     */
    public void reportDefect(String title, String description, String channel) {
        String issueUrl = gitHubIssuePort.createIssue(title, description);
        
        // Format the Slack body to include the link
        String body = String.format("Defect reported: %s%nGitHub Issue: %s", title, issueUrl);
        
        slackNotificationPort.postMessage(channel, body);
    }
}
