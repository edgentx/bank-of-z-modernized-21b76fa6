package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real-world implementation of SlackPort.
 * In a production environment, this would use a Slack Webhook client or API.
 */
@Component
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    @Override
    public void sendMessage(String channel, String body) {
        // In a real implementation, this would call Slack Web API or a webhook.
        // e.g. slackMethods.postMessage(chatPostMessage -> ...
        log.info("[Slack Mock] Sending to channel {}: {}", channel, body);
    }
}