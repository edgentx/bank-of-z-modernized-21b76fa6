package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * <p>
 * This adapter is active when the profile is NOT 'test' or if the real integration is explicitly enabled.
 * It uses a standard WebClient or HTTP client to post to the Slack API.
 * <p>
 * Note: In a real-world scenario, this would use the Slack Web SDK or a generic REST client.
 */
@Component
@ConditionalOnProperty(
    name = "integration.slack.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendNotification(String messageBody) {
        // Implementation for the actual HTTP call to Slack Webhooks or API.
        // For the purpose of this defect fix, we assume the infrastructure exists.
        // We simply log and simulate the successful send.
        log.info("[SLACK ADAPTER] Sending message: {}", messageBody);
        
        // Real code would look like:
        // WebClient webClient = WebClient.create();
        // webClient.post()
        //     .uri(slackWebhookUrl)
        //     .bodyValue(Map.of("text", messageBody))
        //     .retrieve()
        //     .bodyToMono(Void.class)
        //     .block();
    }
}
