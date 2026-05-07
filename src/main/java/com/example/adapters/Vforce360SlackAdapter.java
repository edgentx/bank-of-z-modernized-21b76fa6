package com.example.adapters;

import com.example.domain.vforce.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

@Component
public class Vforce360SlackAdapter implements SlackNotificationPort {

    @Override
    public void postMessage(String channel, String text) {
        // Implementation to call Slack API
        System.out.println("[SLACK] Posting to " + channel + ": " + text);
    }
}
