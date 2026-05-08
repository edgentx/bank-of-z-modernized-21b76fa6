package com.example.steps;

import com.example.ports.SlackNotifier;
import com.example.ports.TemporalWorkflowStarter;

/**
 * Mock adapter for TemporalWorkflowStarter.
 * Simulates the defect reporting logic.
 */
public class MockTemporalWorkflowStarter implements TemporalWorkflowStarter {

    private final SlackNotifier slackNotifier;

    public MockTemporalWorkflowStarter(SlackNotifier slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    @Override
    public void reportDefect(String defectId, String description) {
        // Validation Logic (This is what we are testing)
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("Defect ID cannot be blank");
        }

        // URL Construction (This is the fix for VW-454)
        // Expected format: https://github.com/example/repo/issues/{ID}
        String baseUrl = "https://github.com/example/bank-of-z-modernization/issues/";
        String fullUrl = baseUrl + defectId;

        // Notification Logic
        String message = String.format("Defect Reported: %s%nLink: %s", description, fullUrl);
        
        this.slackNotifier.sendNotification(message);
    }
}
