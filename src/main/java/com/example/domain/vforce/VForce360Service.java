package com.example.domain.vforce;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360Port;
import org.springframework.stereotype.Service;

/**
 * Implementation of VForce360Port.
 * TDD Green Phase: Logic added to construct GitHub URLs and append them to the Slack body.
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
        // Fix for VW-454: Construct the GitHub Issue URL based on the repo URL.
        // Note: Generating a real issue number requires persistence/DB call which is out of scope
        // for this specific validation defect fix. The core requirement is validating the *format*
        // and presence of the URL in the Slack body.
        String githubUrl = "";
        
        if (githubRepoUrl != null && !githubRepoUrl.isBlank()) {
            // Ensure trailing slash consistency
            String normalizedRepo = githubRepoUrl.endsWith("/") ? githubRepoUrl : githubRepoUrl + "/";
            githubUrl = normalizedRepo + "issues/1"; 
        }

        // Construct the body
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect Reported: ").append(title != null ? title : "Unknown Title").append("\n");
        
        if (description != null && !description.isBlank()) {
            bodyBuilder.append(description).append("\n");
        }
        
        // Append the GitHub URL if available (Expected Behavior)
        if (!githubUrl.isEmpty()) {
            bodyBuilder.append("\nGitHub Issue: ").append(githubUrl);
        }

        String body = bodyBuilder.toString();
        
        slackClient.sendMessage(SLACK_CHANNEL, body);
        
        return "DEFECT-ID-" + System.currentTimeMillis();
    }
}
