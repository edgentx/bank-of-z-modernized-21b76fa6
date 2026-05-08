package com.example.domain.vforce;

import com.example.ports.SlackNotifierPort;

/**
 * Service for reporting defects to VForce360 Slack channel.
 * Acts as a proxy for the Temporal Workflow Worker logic.
 * 
 * Fix for S-FB-1: Ensures the GitHub URL is included in the Slack body.
 */
public class DefectReportService {

    private final SlackNotifierPort slackNotifier;

    public DefectReportService(SlackNotifierPort slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    /**
     * Reports a defect to the VForce360 Slack channel.
     * 
     * @param defectId The ID of the defect (e.g., "VW-454")
     * @param githubUrl The URL to the GitHub issue (passed in to simulate retrieval)
     */
    public void reportDefect(String defectId, String githubUrl) {
        // S-FB-1 FIX: Include the GitHub URL in the body
        // Expected Format: "Defect reported: VW-454. GitHub issue: <url>"
        String body = "Defect reported: " + defectId + ". GitHub issue: " + githubUrl;
        
        // Post to Slack
        slackNotifier.postMessage("#vforce360-issues", body);
    }
}
