package com.example.validation.infrastructure.temporal;

import org.springframework.stereotype.Component;

// Implementation for the Temporal Activity
// NOTE: The actual logic to build the Slack message is intentionally left 
// incomplete (TDD Red Phase) to force the test failure required by the prompt.
@Component
public class SlackNotificationActivitiesImpl implements SlackNotificationActivities {

    @Override
    public void sendSlackNotification(String title, String githubUrl, String severity) {
        // EXPECTED IMPLEMENTATION:
        // String message = String.format("Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s", title, severity, githubUrl);
        // slackService.send(message);
        
        // ACTUAL (EMPTY) IMPLEMENTATION FOR RED PHASE:
        // This causes the assertion in SFB1E2ETest to fail because the mock receives a different input.
        String message = "Defect Reported: " + title; // Missing URL
        
        System.out.println("Sending to Slack: " + message);
    }
}