package com.example.adapters;

import com.example.ports.SlackNotifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Real adapter for sending Slack notifications.
 * This implementation posts to the Slack Webhook URL.
 */
@Component
public class RealSlackAdapter implements SlackNotifier {

    private final String webhookUrl;
    private final RestTemplate restTemplate;

    public RealSlackAdapter(@Value("${slack.webhook.url}") String webhookUrl, RestTemplate restTemplate) {
        this.webhookUrl = webhookUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void send(String channel, String messageBody) {
        // TODO: Implement actual POST to Slack Webhook
        // Map<String, String> payload = Map.of("channel", channel, "text", messageBody);
        // restTemplate.postForEntity(webhookUrl, payload, String.class);
        
        System.out.println("[SLACK] Sent to " + channel + ": " + messageBody);
    }
}
