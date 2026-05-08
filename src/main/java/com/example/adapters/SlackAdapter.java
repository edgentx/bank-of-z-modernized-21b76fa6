package com.example.adapters;

import com.example.ports.SlackPort;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Production adapter for Slack integration.
 * Connects to the actual Slack API using the configured Slack client.
 */
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);
    private final MethodsClient slackMethodsClient;
    private final String channelId;

    public SlackAdapter(MethodsClient slackMethodsClient, String channelId) {
        this.slackMethodsClient = slackMethodsClient;
        this.channelId = channelId;
    }

    @Override
    public void sendMessage(String messageBody) {
        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channelId)
                    .text(messageBody)
                    .build();

            // Execute the API call
            slackMethodsClient.chatPostMessage(request);
            log.info("Successfully posted message to Slack channel {}", channelId);

        } catch (IOException | SlackApiException e) {
            log.error("Failed to post message to Slack channel {}", channelId, e);
            // Depending on requirements, we might throw a RuntimeException here
            // to trigger retry logic in Temporal.
            throw new RuntimeException("Slack API call failed", e);
        }
    }
}
