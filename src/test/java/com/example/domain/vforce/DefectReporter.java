package com.example.domain.vforce;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

import java.util.Optional;

/**
 * Domain service for reporting defects.
 * Orchestrates the retrieval of GitHub issue URLs and notification via Slack.
 * This logic is triggered by the Temporal workflow (_report_defect).
 */
public class DefectReporter {

    private final SlackNotificationPort slackPort;
    private final GitHubPort gitHubPort;

    public DefectReporter(SlackNotificationPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Reports a defect to the specified Slack channel, including a link to the GitHub issue.
     *
     * @param issueId The ID of the issue (e.g., "VW-454")
     * @param channel The Slack channel (e.g., "#vforce360-issues")
     */
    public void reportDefect(String issueId, String channel) {
        Optional<String> issueUrlOptional = gitHubPort.getIssueUrl(issueId);
        
        String body;
        if (issueUrlOptional.isPresent()) {
            String url = issueUrlOptional.get();
            body = "Defect Reported: " + issueId + " - GitHub issue: " + url;
        } else {
            // Fallback behavior if GitHub URL cannot be retrieved
            body = "Defect Reported: " + issueId + " - (GitHub URL unavailable)";
        }

        slackPort.postMessage(channel, body);
    }
}
