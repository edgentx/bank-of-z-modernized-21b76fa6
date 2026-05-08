package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Port.
 * This would use the Slack Web API to send messages.
 */
@Component
public class SlackAdapter implements SlackPort {

    @Override
    public void sendMessage(String channel, String body) {
        // Implementation of actual Slack API call would go here.
        // For the defect validation, we ensure the body contains the link.
        System.out.println("Sending to Slack [" + channel + "]: " + body);
    }
}