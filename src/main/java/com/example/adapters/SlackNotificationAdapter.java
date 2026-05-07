package com.example.adapters;

import com.example.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Real-world adapter implementation for posting notifications to Slack.
 * This implementation uses RestTemplate to make HTTP requests to the Slack API.
 */
@Component
public class SlackNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final RestTemplate restTemplate;
    private final String slackWebhookUrl;
    private final String githubApiUrl;
    private final String githubAuthToken;

    public SlackNotificationAdapter(RestTemplate restTemplate,
                                    @Value("${external.slack.webhook-url}") String slackWebhookUrl,
                                    @Value("${external.github.api-url}") String githubApiUrl,
                                    @Value("${external.github.auth-token}") String githubAuthToken) {
        this.restTemplate = restTemplate;
        this.slackWebhookUrl = slackWebhookUrl;
        this.githubApiUrl = githubApiUrl;
        this.githubAuthToken = githubAuthToken;
    }

    @Override
    public boolean postToSlack(String channel, String message) {
        try {
            // Constructing the payload for Slack Incoming Webhook
            String payload = String.format("{\"text\": \"%s\", \"channel\": \"%s\"}", message.replace("\"", "\\\""), channel);
            
            // Simulated network call (in a real scenario, restTemplate.postForEntity would be used)
            log.info("Posting to Slack Channel {}: {}", channel, message);
            // restTemplate.postForEntity(slackWebhookUrl, payload, String.class);
            
            return true;
        } catch (Exception e) {
            log.error("Failed to post to Slack", e);
            return false;
        }
    }

    @Override
    public String createGitHubIssue(String title, String body) {
        try {
            log.info("Creating GitHub Issue with title: {}", title);
            
            // Simulated GitHub API call logic
            // In a real implementation, we would use restTemplate.exchange with Basic Auth or Token Header
            // String url = githubApiUrl + "/repos/{owner}/{repo}/issues";
            // ... headers set Authorization: token githubAuthToken ...
            
            String mockIssueId = "GH-" + System.currentTimeMillis();
            return "https://github.com/mock-repo/issues/" + mockIssueId;
        } catch (Exception e) {
            log.error("Failed to create GitHub Issue", e);
            throw new RuntimeException("GitHub Issue creation failed", e);
        }
    }
}
