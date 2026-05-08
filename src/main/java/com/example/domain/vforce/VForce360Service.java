package com.example.domain.vforce;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360Port;
import org.springframework.stereotype.Service;

/**
 * Implementation of VForce360Port.
 * This class is intentionally INCOMPLETE/STUBBED to ensure tests FAIL (Red Phase).
 * 
 * Logic to append GitHub URLs to the Slack body is missing.
 */
@Service
public class VForce360Service implements VForce360Port {

    private final SlackNotificationPort slackClient;
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    public VForce360Service(SlackNotificationPort slackClient) {
        this.slackClient = slackClient;
    }

    @Override
    public String reportDefect(String title, String description, String githubRepoUrl) {
        // MISSING LOGIC:
        // 1. The logic to construct a valid GitHub URL is missing.
        // 2. The logic to append this URL to the Slack body is missing.
        
        String body = "Defect Reported: " + title + "\n" + description;
        
        // This currently sends a body WITHOUT the GitHub URL,
        // causing SlackNotificationValidatorTest to fail.
        slackClient.sendMessage(SLACK_CHANNEL, body);
        
        return "DEFECT-ID-" + System.currentTimeMillis();
    }
}
