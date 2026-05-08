package com.example.adapters.impl;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotificationPort.
 * In a real environment, this would use the Slack WebAPI client.
 * For the scope of this fix (VW-454), it logs the action to simulate the integration.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void postMessage(String channel, String body) {
        // Real implementation would look like:
        // MethodsClient methods = slackClient.methods();
        // ChatPostMessageRequest request = ChatPostMessageRequest.builder()
        //     .channel(channel)
        //     .text(body)
        //     .build();
        // methods.chatPostMessage(request);

        log.info("[SLACK] Posting to channel {}: {}", channel, body);
    }
}
