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
        // Implementation is intentionally left blank or minimal to allow tests to fail (RED phase).
        // The tests expect specific logic to be present here.
        
        // Example of expected logic:
        // String url = vForce360.getGitHubIssueUrl(defectId);
        // String message = "Defect Reported: " + summary + "\nGitHub issue: " + url;
        // slack.postMessage("#vforce360-issues", message);
        
        throw new UnsupportedOperationException("Implement reportDefect logic");
    }
}