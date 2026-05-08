package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Real-world implementation of SlackNotificationPort using WebClient.
 * Connects to Slack API to post messages.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private static final String SLACK_API_BASE = "https://slack.com/api";

    private final WebClient webClient;
    private final String botToken;

    /**
     * Constructs the adapter.
     *
     * @param webClientBuilder The WebClient builder (injected by Spring)
     * @param botToken         The Slack Bot Token (OAuth token starting with xoxb-)
     */
    public SlackNotificationAdapter(WebClient.Builder webClientBuilder, String botToken) {
        this.botToken = botToken;
        this.webClient = webClientBuilder
                .baseUrl(SLACK_API_BASE)
                .build();
    }

    @Override
    public void postMessage(String channel, String message) {
        try {
            // In a real scenario, we would decode the block kit JSON or post simple text.
            // Here we post a simple message to the chat.postMessage endpoint.
            String response = webClient.post()
                    .uri("/api/chat.postMessage")
                    .headers(h -> h.setBearerAuth(botToken))
                    .bodyValue(new SlackPostRequest(channel, message))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Block for synchronous execution in this workflow step

            log.info("Slack response: {}", response);
        } catch (WebClientResponseException e) {
            log.error("Failed to post to Slack: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to post notification to Slack", e);
        } catch (Exception e) {
            log.error("Error during Slack notification", e);
            throw new RuntimeException("Error during Slack notification", e);
        }
    }

    /**
     * DTO for Slack API Post Message request.
     */
    private record SlackPostRequest(String channel, String text) {}
}
