package com.example.domain.defect;

import com.example.ports.SlackNotificationPort;

/**
 * Placeholder service for the workflow activity.
 * This file acts as the "System Under Test" scaffold.
 * In the next phase (Green), this file will be removed/renamed/moved to src/main 
 * and implemented with the actual logic.
 * 
 * Keeping it here in src/test allows the tests to compile and run (and FAIL) immediately
 * without requiring the main implementation yet.
 */
public class DefectReportingService {

    private final SlackNotificationPort slackPort;

    public DefectReportingService(SlackNotificationPort slackPort) {
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect to Slack.
     * 
     * @param defectId The ID of the defect (e.g. VW-454).
     * @param githubUrl The URL of the GitHub issue.
     */
    public void reportDefect(String defectId, String githubUrl) {
        // Intentionally left empty or incorrect for RED phase.
        // Implementation will be provided to make tests pass.
        
        // Current behavior (likely to fail tests):
        // 1. Does it check for null? No.
        // 2. Does it send the URL? No.
    }
}
