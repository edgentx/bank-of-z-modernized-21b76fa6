package com.example.services;

import com.example.ports.GitHubClientPort;
import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Service;

/**
 * Service handling the defect reporting workflow logic.
 * Orchestrates fetching data from GitHub and notifying Slack.
 */
@Service
public class DefectReportService {

    private final GitHubClientPort gitHubClient;
    private final SlackNotifierPort slackNotifier;

    /**
     * Constructor injection for ports (adapters).
     * Spring will automatically inject the Mock implementations during tests
     * and Real implementations (once configured) during production.
     */
    public DefectReportService(GitHubClientPort gitHubClient, SlackNotifierPort slackNotifier) {
        this.gitHubClient = gitHubClient;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Reports a defect by retrieving its URL and sending a notification.
     * Corresponds to the temporal-worker exec trigger.
     * 
     * @param defectId The ID of the defect (e.g., "VW-454")
     */
    public void reportDefect(String defectId) {
        // 1. Retrieve the URL from GitHub via the port
        String issueUrl = gitHubClient.getIssueUrl(defectId);

        // 2. Construct the Slack message body including the URL
        // (Validation check: Ensure URL is present to satisfy the 'Expected Behavior')
        if (issueUrl == null || issueUrl.isEmpty()) {
            throw new IllegalStateException("GitHub URL could not be retrieved for defect: " + defectId);
        }

        String slackBody = "Defect Report: " + defectId + "\n" +
                           "GitHub Issue: " + issueUrl;

        // 3. Send notification via the Slack port
        slackNotifier.notify(slackBody);
    }
}
