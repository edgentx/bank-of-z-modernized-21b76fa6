package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotificationPort.
 * This adapter acts as the bridge between the application logic and the external Slack API.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    /**
     * Sends a notification to Slack.
     * <p>
     * Note: This implementation currently logs the message to simulate the send operation
     * and returns true. In a full implementation, this would use an HTTP client
     * (e.g., WebClient or RestTemplate) to POST to a Slack Incoming Webhook URL.
     * </p>
     *
     * @param messageBody The formatted message body to send.
     * @return true if sending was acknowledged, false otherwise.
     */
    @Override
    public boolean sendNotification(String messageBody) {
        // Real-world logic would go here, e.g.:
        // try {
        //     WebClient.create().post().uri(webhookUrl)
        //         .bodyValue("{\"text\": \"" + messageBody + "\"}")
        //         .retrieve()
        //         .toBodilessEntity()
        //         .block();
        //     return true;
        // } catch (Exception e) {
        //     log.error("Failed to send Slack notification", e);
        //     return false;
        // }

        log.info("[Slack Adapter] Sending notification: {}", messageBody);
        return true;
    }
}