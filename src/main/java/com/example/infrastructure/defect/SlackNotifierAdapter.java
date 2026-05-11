package com.example.infrastructure.defect;

import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotifierPort.
 * Connects to Slack API to send notifications.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    @Override
    public void sendNotification(String messageBody) {
        // Real implementation would use WebClient to POST to Slack Webhook
        // Example:
        // 
        // WebClient client = WebClient.create();
        // client.post()
        //     .uri(slackWebhookUrl)
        //     .bodyValue("{\"text\": \"" + JsonEscape.escape(messageBody) + "\"}")
        //     .retrieve()
        //     .bodyToMono(Void.class)
        //     .block();
        
        // System.out.println("[SlackAdapter] Sending: " + messageBody);
    }
}
