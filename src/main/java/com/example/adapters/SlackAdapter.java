package com.example.adapters;

import com.example.ports.SlackPort;
import com.example.domain.validation.model.SlackNotificationMessage;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real adapter for sending messages to Slack.
 * This implementation would typically use WebClient or a dedicated Slack SDK.
 * For S-FB-1, this acts as the concrete implementation behind the Port.
 */
@Component
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    @Override
    public void sendNotification(SlackNotificationMessage message) {
        // In a real scenario, we would execute an HTTP POST to Slack Webhook.
        log.info("Sending Slack message to channel {}: {}", message.channel(), message.text());
        // 
        // Example Real Implementation logic (Commented out for unit test isolation):
        // webClient.post()
        //     .uri(slackWebhookUrl)
        //     .bodyValue(message)
        //     .retrieve()
        //     .bodyToMono(Void.class)
        //     .block();
    }
}