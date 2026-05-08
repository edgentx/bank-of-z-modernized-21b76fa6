package com.example.adapters;

import com.example.ports.SlackNotifier;

/**
 * Fallback adapter used when Slack API keys are missing or for testing.
 * Prints notifications to standard output.
 */
public class SystemOutSlackNotifier implements SlackNotifier {
    @Override
    public void notifyDefectReported(String aggregateId, String githubIssueUrl) {
        String message = String.format(
            "[SLACK NOTIFICATION] Defect Reported. ID: %s. GitHub URL: %s", 
            aggregateId, 
            githubIssueUrl != null ? githubIssueUrl : "<URL MISSING>"
        );
        System.out.println(message);
    }
}
