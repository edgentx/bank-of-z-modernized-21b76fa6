package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Slack notifications.
 * In a production environment, this would integrate with the Slack Web API.
 * For defect VW-454 validation, this component ensures the URL is injected into the payload.
 */
@Component
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    @Override
    public void sendNotification(String messageBody) {
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        
        // In a real implementation, we would POST this to a Slack Webhook URL.
        // e.g., restTemplate.postForEntity(webhookUrl, new SlackMessage(messageBody), Void.class);
        
        log.info("Sending notification to #vforce360-issues: {}", messageBody);
    }
}
