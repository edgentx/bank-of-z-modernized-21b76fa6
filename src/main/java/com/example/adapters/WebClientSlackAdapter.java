package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Adapter for sending notifications to Slack.
 * Implements the SlackNotificationPort interface.
 */
@Component
public class WebClientSlackAdapter implements SlackNotificationPort {

    private final WebClient webClient;

    public WebClientSlackAdapter(WebClient.Builder webClientBuilder) {
        // Assuming a base URL is configured elsewhere or defaulting
        this.webClient = webClientBuilder.baseUrl("https://slack.com/api").build();
    }

    @Override
    public void sendNotification(Map<String, String> messagePayload) {
        // Actual implementation would POST to Slack API
        // System.out.println("Sending to Slack: " + messagePayload);
    }
}
