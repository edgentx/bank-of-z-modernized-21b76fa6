package com.example.domain.validation.adapter;

import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotifierPort.
 * Uses a stub to satisfy the build.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    @Override
    public void notify(String body) {
        // Real implementation would use WebClient or Slack API client to send the message.
        System.out.println("[Slack] Sending notification: " + body);
    }
}
