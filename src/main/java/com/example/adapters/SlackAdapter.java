package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Real adapter for Slack API interactions.
 * Implements the SlackMessageValidator interface to satisfy the build and domain contracts.
 */
public class SlackAdapter implements SlackMessageValidator {

    private static final Logger logger = LoggerFactory.getLogger(SlackAdapter.class);
    private final MethodsClient methodsClient;
    private final String token;

    public SlackAdapter(MethodsClient methodsClient, String token) {
        this.methodsClient = methodsClient;
        this.token = token;
    }

    /**
     * Sends a message to a Slack channel.
     * @param channel The target channel ID or name.
     * @param text The message text.
     */
    public void postMessage(String channel, String text) {
        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .text(text)
                    .build();
            
            ChatPostMessageResponse response = methodsClient.chatPostMessage(request);
            
            if (!response.isOk()) {
                logger.error("Slack API Error: {} - {}", response.getError(), response.getWarning());
            }
        } catch (IOException | SlackApiException e) {
            logger.error("Failed to post message to Slack", e);
        }
    }

    /**
     * Validates that the message body contains a GitHub URL.
     * Corresponds to S-FB-1 / VW-454 validation logic.
     * @param messageBody The Slack message body to check.
     * @return true if a GitHub URL is found, false otherwise.
     */
    @Override
    public boolean isValid(String messageBody) {
        if (messageBody == null) {
            return false;
        }
        // Simple regex check for http/s URLs containing github.com
        return messageBody.matches(".*https?://([a-zA-Z0-9-]+\\.)*github\\.com/.*");
    }
}
