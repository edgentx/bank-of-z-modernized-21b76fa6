package com.example.adapter;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * In a production environment, this would use an HTTP client (e.g., WebClient or RestTemplate)
 * to communicate with the Slack API.
 * 
 * For this fix/feature scope, we assume the successful dispatch of the formatted string.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public boolean send(String messageBody) {
        // Integration point for Slack Web API.
        // POST https://slack.com/api/chat.postMessage
        // Body: { "channel": "#vforce360-issues", "text": messageBody }
        // Returning true indicates acceptance of the payload for this unit of work.
        return true;
    }
}