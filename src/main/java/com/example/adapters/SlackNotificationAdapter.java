package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for sending notifications to Slack.
 * In a production environment, this would integrate with the Slack Web API.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    /**
     * Sends a message to a configured Slack channel.
     * <p>
     * Note: This is a placeholder implementation for the adapter pattern.
     * Real implementation would use WebClient or a Slack client library to POST to a webhook.
     *
     * @param messageBody The formatted content of the message.
     * @throws IllegalArgumentException if messageBody is null or blank.
     */
    @Override
    public void sendMessage(String messageBody) {
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("messageBody cannot be blank");
        }

        // Log the action to simulate external interaction without dependencies
        log.info("[Slack Adapter] Sending message: {}", messageBody);

        // Real implementation example:
        // webClient.post()
        //     .uri(slackWebhookUrl)
        //     .bodyValue(BodyInserters.fromValue(Map.of("text", messageBody)))
     //    .retrieve()
     //    .bodyToMono(Void.class)
     //    .block();
    }
}
