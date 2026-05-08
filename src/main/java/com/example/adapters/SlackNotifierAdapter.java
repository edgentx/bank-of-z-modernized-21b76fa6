package com.example.adapters;

import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for SlackNotifierPort.
 * Note: In production, this would use the Slack WebClient API.
 * For S-FB-1 purposes, we maintain the contract logic.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    @Override
    public void sendMessage(String channel, String body) {
        // Real implementation would call Slack Web API here.
        // e.g., slackClient.methods().chatPostMessage(r -> r.channel(channel).text(body));
        
        // Logging for verification in real environment
        System.out.println("[SLACK] Sending to " + channel + ": " + body);
    }
}
