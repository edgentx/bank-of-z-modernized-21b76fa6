package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Real adapter for posting messages to Slack.
 * Uses the official Slack API Client.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final MethodsClient slackMethodsClient;

    public SlackNotificationAdapter(MethodsClient slackMethodsClient) {
        this.slackMethodsClient = slackMethodsClient;
    }

    @Override
    public boolean postMessage(String channel, String body) {
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channel)
                .text(body)
                .build();

        try {
            ChatPostMessageResponse response = slackMethodsClient.chatPostMessage(request);
            if (response.isOk()) {
                log.info("Successfully posted message to Slack channel {}", channel);
                return true;
            } else {
                log.error("Failed to post message to Slack: {} - {}", response.getError(), response.getWarning());
                return false;
            }
        } catch (IOException | SlackApiException e) {
            log.error("Exception while calling Slack API", e);
            return false;
        }
    }
}
