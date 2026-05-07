package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter for Slack notifications.
 * Validates that the URL is present in the payload before sending.
 */
@Component
public class SlackNotificationAdapter implements SlackPort {
    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void notifyDefectReported(String summary, String githubUrl) {
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException("Cannot notify Slack: GitHub URL is missing.");
        }
        
        String message = String.format(
            "New Defect Reported: %s\nGitHub Issue: %s", 
            summary, 
            githubUrl
        );
        
        // Simulate sending to Slack webhook
        log.info("Sending to Slack: {}", message);
        // logic to post to webhook would go here
    }
}
