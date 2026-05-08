package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Real implementation of SlackNotificationPort using the Slack API Client.
 * This adapter is configured via Spring Boot application properties/yml.
 */
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final Slack slackClient;
    private final String authToken;

    public SlackNotificationAdapter(String authToken) {
        this.authToken = authToken;
        this.slackClient = Slack.getInstance();
    }

    @Override
    public boolean postMessage(String channel, String body, Map<String, String> contextMetadata) {
        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .text(body)
                    // We could unfurl links or add attachments based on contextMetadata here
                    .build();

            var response = slackClient.methods(authToken).chatPostMessage(request);

            if (response.isOk()) {
                log.info("Successfully posted message to Slack channel {}", channel);
                return true;
            } else {
                log.error("Failed to post message to Slack: {} - {}", response.getError(), response.getWarning());
                return false;
            }
        } catch (IOException | SlackApiException e) {
            log.error("Error communicating with Slack API", e);
            return false;
        }
    }
}