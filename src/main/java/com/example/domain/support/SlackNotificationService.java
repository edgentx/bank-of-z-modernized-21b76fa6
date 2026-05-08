package com.example.domain.support;

import com.example.ports.SlackClient;
import com.example.ports.GitHubClient;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Service responsible for reporting defects to Slack.
 * This is the class under test.
 */
@Service
public class SlackNotificationService {

    private final SlackClient slackClient;
    private final GitHubClient gitHubClient;

    public SlackNotificationService(SlackClient slackClient, GitHubClient gitHubClient) {
        this.slackClient = slackClient;
        this.gitHubClient = gitHubClient;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * 
     * @param title The title of the defect
     * @param description The description of the defect
     * @param referenceTag The unique reference tag (e.g., VW-454)
     */
    public void reportDefect(String title, String description, String referenceTag) {
        // Step 1: Obtain GitHub URL
        // Note: This is a stub. In the Red phase, this logic might be empty or incorrect.
        // The test will drive the implementation here.
        String gitUrl = gitHubClient.createIssueUrl(referenceTag);

        if (gitUrl == null || gitUrl.isBlank()) {
            throw new IllegalStateException("Failed to generate GitHub URL for reference: " + referenceTag);
        }

        // Step 2: Construct Slack Payload
        Map<String, Object> payload = new HashMap<>();
        // CRITICAL: The defect states the URL was missing. The implementation must ensure it is present.
        payload.put("text", description + "\nGitHub Issue: " + gitUrl); 

        // Step 3: Send Notification
        slackClient.sendMessage(payload);
    }
}
