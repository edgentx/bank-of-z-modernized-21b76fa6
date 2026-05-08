package com.example.adapters;

import com.example.ports.VForce360NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for VForce360NotificationPort.
 * In a production environment, this would use a Slack WebClient or similar HTTP client
 * to post the message to a webhook.
 * 
 * For the purpose of this modernization module, we log the action to simulate
 * the side effect, ensuring the system behavior is verifiable.
 */
@Component
public class SlackNotificationAdapter implements VForce360NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void reportDefect(String defectId, String message) {
        // Simulated external call
        // In production: webClient.post().uri(webhookUrl).body(message).send();
        log.info("Sending Slack notification for defect {}: [{}], message body: \n{}", defectId, defectId, message);
        
        // Logic to connect to IBM MQ or Temporal activity could go here if required
        // by the wider architecture, but the Port interface abstracts this away.
    }
}
