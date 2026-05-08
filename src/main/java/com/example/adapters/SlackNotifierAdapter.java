package com.example.adapters;

import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of the SlackNotifierPort.
 * In a production environment, this would use the Slack Web API client.
 * For this context, it acts as the concrete adapter resolving the interface.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierAdapter.class);

    @Override
    public void send(String messageBody) {
        // Real-world implementation would involve WebClient or Apache HttpClient
        // calling https://slack.com/api/chat.postMessage
        
        log.info("Sending Slack notification: {}", messageBody);
        
        // Simulating successful send for the execution path
    }
}
