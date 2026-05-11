package com.example.adapters;

import com.example.ports.SlackPort;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Default implementation of {@link SlackPort} using the official Slack API SDK.
 * This is the "real" adapter used in production profiles.
 */
public class DefaultSlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(DefaultSlackAdapter.class);
    private final MethodsClient methodsClient;
    private final String botToken; // Typically starts with "xoxb-"

    /**
     * Constructor for dependency injection.
     *
     * @param methodsClient The configured Slack API MethodsClient.
     * @param botToken      The Slack Bot Token for authentication.
     */
    public DefaultSlackAdapter(MethodsClient methodsClient, String botToken) {
        this.methodsClient = methodsClient;
        this.botToken = botToken;
    }

    @Override
    public void sendMessage(String channel, String text) {
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channel)
                .text(text)
                .build();

        try {
            ChatPostMessageResponse response = methodsClient.chatPostMessage(request, botToken);
            if (!response.isOk()) {
                String error = String.format("Slack API Error: %s - %s", response.getError(), response.getWarning());
                log.error(error);
                throw new RuntimeException(error);
            }
            log.info("Message sent to Slack channel {} successfully", channel);
        } catch (IOException | SlackApiException e) {
            log.error("Failed to send message to Slack", e);
            throw new RuntimeException("Failed to send message to Slack", e);
        }
    }
}
