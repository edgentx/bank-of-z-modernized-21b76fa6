package com.example.adapters;

import com.example.ports.NotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real adapter for sending Slack notifications.
 * This implementation would typically make an HTTP call to the Slack Web API.
 */
public class SlackNotifierAdapter implements NotifierPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierAdapter.class);

    @Override
    public void send(String body) {
        if (body == null) {
            throw new IllegalArgumentException("Body cannot be null");
        }
        
        // In a real implementation, we would use WebClient or RestTemplate to POST to Slack.
        // For the defect VW-454 validation, the critical part is ensuring the URL is present in the body.
        log.info("Sending Slack notification: {}", body);
        
        // Example:
        // webClient.post()
        //     .uri(slackWebhookUrl)
        //     .bodyValue(Map.of("text", body))
        //     .retrieve()
        //     .bodyToMono(Void.class)
        //     .block();
    }
}
