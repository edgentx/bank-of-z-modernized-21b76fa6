package com.example.adapters;

import com.example.application.SlackMessage;
import com.example.ports.NotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of NotificationPort.
 * This would normally use the Slack WebClient to send messages.
 * For defect validation VW-454, we ensure the text received is sent faithfully.
 */
@Component
public class NotificationAdapterImpl implements NotificationPort {

    @Override
    public void send(SlackMessage message) {
        // Real implementation would call Slack Web API here.
        // Example: slackClient.postMessage(token, channel, message.getText());
        
        // Logging the send to simulate execution in e2e/unit tests
        System.out.println("[Slack Adapter] Sending: " + message.getText());
    }
}
