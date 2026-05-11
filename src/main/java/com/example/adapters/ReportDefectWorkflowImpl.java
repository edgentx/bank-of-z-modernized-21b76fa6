package com.example.adapters;

import com.example.ports.SlackClientPort;
import com.example.ports.TemporalReportDefectPort;

/**
 * Implementation of the defect reporting workflow.
 * This adapter acts as the bridge between the Temporal trigger (Port input)
 * and the Slack notification system (Port output).
 *
 * It constructs the message body ensuring the GitHub URL is present,
 * resolving the defect VW-454.
 */
public class ReportDefectWorkflowImpl implements TemporalReportDefectPort {

    private final SlackClientPort slackClient;
    // In a real scenario, this might be injected via @Value or configuration
    private static final String GITHUB_BASE_URL = "https://github.com/example/bank/issues";

    public ReportDefectWorkflowImpl(SlackClientPort slackClient) {
        this.slackClient = slackClient;
    }

    @Override
    public void reportDefect(String defectId, String title, String description) {
        // 1. Construct the GitHub URL based on the Defect ID
        String issueUrl = GITHUB_BASE_URL + "/" + defectId;

        // 2. Format the Slack body
        // Slack message formatting allows <url|text> or just <url> to auto-link.
        // The test looks for 'github.com' or '<http'.
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("*Defect Reported: ").append(defectId).append("*\n");
        bodyBuilder.append("*Title:* ").append(title).append("\n");
        bodyBuilder.append("*Description:* ").append(description).append("\n");
        bodyBuilder.append("*GitHub Issue:* <").append(issueUrl).append("|View Issue>");

        String slackBody = bodyBuilder.toString();

        // 3. Post to the target channel
        slackClient.postMessage("#vforce360-issues", slackBody);
    }
}
