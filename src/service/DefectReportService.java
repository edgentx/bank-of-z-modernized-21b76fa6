package com.example.service;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360ReportingPort;

/**
 * Service handling the business logic for reporting defects.
 * This is the System Under Test (SUT).
 */
public class DefectReportService {

    private final VForce360ReportingPort vForce360;
    private final SlackNotificationPort slack;

    public DefectReportService(VForce360ReportingPort vForce360, SlackNotificationPort slack) {
        this.vForce360 = vForce360;
        this.slack = slack;
    }

    /**
     * Orchestration of the defect reporting workflow.
     * 1. Fetches GitHub URL from VForce360.
     * 2. Constructs message.
     * 3. Posts to Slack.
     *
     * @param defectId The ID of the defect.
     * @param summary  Summary of the defect.
     */
    public void reportDefect(String defectId, String summary) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("summary cannot be null or blank");
        }

        // 1. Fetch the GitHub URL from the VForce360 system
        String gitHubUrl = vForce360.getGitHubIssueUrl(defectId);

        // 2. Construct the message body including the GitHub URL
        // The test explicitly checks for the label "GitHub issue:"
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Defect Reported: ").append(summary).append("\n");
        messageBuilder.append("GitHub issue: ").append(gitHubUrl);

        // 3. Post to the specific Slack channel
        slack.postMessage("#vforce360-issues", messageBuilder.toString());
    }
}
