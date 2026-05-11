package com.example.adapters;

import com.example.ports.SlackPort;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.Slack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Real implementation of SlackPort using the Slack API Client.
 * This adapter connects to the actual Slack service to post messages.
 */
public class DefaultSlackAdapter implements SlackPort {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSlackAdapter.class);
    private final MethodsClient methodsClient;
    private final String token;

    /**
     * Constructor for dependency injection.
     *
     * @param slackInstance The configured Slack instance.
     * @param token         The Slack Bot Token (xoxb-...).
     */
    public DefaultSlackAdapter(Slack slackInstance, String token) {
        this.methodsClient = slackInstance.methods(token);
        this.token = token;
    }

    @Override
    public boolean postMessage(String channelId, String text) {
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelId)
                .text(text)
                .build();

        try {
            ChatPostMessageResponse response = methodsClient.chatPostMessage(request);
            if (response.isOk()) {
                logger.info("Message posted to channel {}: {}", channelId, text);
                return true;
            } else {
                logger.error("Failed to post message to Slack: {} - {}", response.getError(), response.getWarning());
                return false;
            }
        } catch (IOException | SlackApiException e) {
            logger.error("Slack API error", e);
            return false;
        }
    }
}
