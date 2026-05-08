package com.example.adapters;

import com.example.ports.SlackPort;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Default implementation of the Slack port using the official Slack API SDK.
 * Addresses S-FB-1: Ensure GitHub URLs are appended to the body.
 */
@Component
public class DefaultSlackAdapter implements SlackPort {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSlackAdapter.class);
    private final MethodsClient slackMethodsClient;
    private final String defaultChannel;

    public DefaultSlackAdapter(MethodsClient slackMethodsClient,
                               @Value("${slack.default.channel:}") String defaultChannel) {
        this.slackMethodsClient = slackMethodsClient;
        this.defaultChannel = defaultChannel;
    }

    @Override
    public void sendAlert(String channel, String message, String githubIssueUrl) {
        String targetChannel = (channel != null && !channel.isBlank()) ? channel : defaultChannel;
        if (targetChannel == null || targetChannel.isBlank()) {
            logger.warn("Slack channel is not configured. Message not sent.");
            return;
        }

        // S-FB-1: Append GitHub URL if present
        String fullMessage = message;
        if (githubIssueUrl != null && !githubIssueUrl.isBlank()) {
            fullMessage = message + "\nGitHub issue: " + githubIssueUrl;
        }

        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(targetChannel)
                    .text(fullMessage)
                    .build();

            ChatPostMessageResponse response = slackMethodsClient.chatPostMessage(request);
            if (response.isOk()) {
                logger.info("Message sent to Slack channel {}", targetChannel);
            } else {
                logger.error("Failed to send message to Slack: {}", response.getError());
            }
        } catch (IOException | SlackApiException e) {
            logger.error("Error communicating with Slack API", e);
            throw new RuntimeException("Failed to send Slack alert", e);
        }
    }
}
