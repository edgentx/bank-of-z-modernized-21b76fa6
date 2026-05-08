package com.example.domain.vforce;

import com.example.ports.SlackNotifierPort;

/**
 * Proxy for the Temporal Workflow Worker logic.
 * This class mimics the production behavior of reporting a defect.
 * In the real system, this would be orchestrated via Temporal Activities.
 * 
 * NOTE: This is a STUB used to satisfy the Red Phase requirement.
 * It intentionally does NOT contain the GitHub URL logic yet.
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
        // TEMPORARY IMPLEMENTATION (Missing the URL fix)
        // This is the RED phase baseline.
        String body = "Defect reported: " + defectId + ". Please check internal dashboard.";
        
        // Post to Slack
        slackNotifier.postMessage("#vforce360-issues", body);
    }
}
