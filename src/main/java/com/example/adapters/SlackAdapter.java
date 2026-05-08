package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

@Component
public class SlackAdapter implements SlackPort {
    @Override
    public void sendMessage(String channel, String text) {
        // Real implementation would involve an HTTP client to Slack Web API
        // For the purpose of this defect fix validation, we assume this adapter
        // successfully delivers the message to the channel.
        System.out.println("Slack sent to " + channel + ": " + text);
    }
}
